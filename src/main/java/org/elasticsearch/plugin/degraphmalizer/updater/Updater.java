package org.elasticsearch.plugin.degraphmalizer.updater;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.index.aliases.IndexAlias;
import org.elasticsearch.index.aliases.IndexAliasesService;

/**
 * This class handles Change instances. The class can be configured via elasticsearch.yml (see README.md for
 * more information). The Updater manages a queue of Change objects, executes HTTP requests for these
 * changes and retries changes when HTTP requests fail.
 */
public final class Updater implements Runnable {
    private static final ESLogger LOG = Loggers.getLogger(Updater.class);
    private static final int NAPTIME = 5 * 1000;
    private final HttpClient httpClient;

    private final String uriScheme;
    private final String uriHost;
    private final int uriPort;
    private final long retryDelayOnFailureInMillis;
    private final int maxRetries;

    private final String index;
	private final IndexAliasesService aliasesService;

    private File errorFile;

    private UpdaterQueue queue;
    private boolean shutdownInProgress = false;
    private boolean sending = false;


    public Updater(final String index, IndexAliasesService indexAliases, final String uriScheme, final String uriHost, final int uriPort, final long retryDelayOnFailureInMillis, final String logPath, final int queueLimit, final int maxRetries) {
        this.index = index;
		this.aliasesService = indexAliases;
        this.uriScheme = uriScheme;
        this.uriHost = uriHost;
        this.uriPort = uriPort;
        this.retryDelayOnFailureInMillis = retryDelayOnFailureInMillis;
        this.maxRetries = maxRetries;

        queue = new UpdaterQueue(logPath, index, queueLimit);
        new Thread(queue,"updaterqueue-" + index).start();

        errorFile = new File(logPath, index + "-error.log");


        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 50000);
        HttpConnectionParams.setSoTimeout(params, 50000);
        httpClient = new DefaultHttpClient(params);


        LOG.info("Updater instantiated for index {}. Updates will be sent to {}://{}:{}. Retry delay on failure is {} milliseconds.", index, uriScheme, uriHost, uriPort, retryDelayOnFailureInMillis);
        LOG.info("Updater will overflow in {} after limit of {} has been reached, messages will be retried {} times ", logPath, queueLimit, maxRetries);
    }

    public void shutdown() {
        shutdownInProgress = true;
    }

    public int getQueueSize() {
        return queue.size();
    }

    public void flushQueue() {
        queue.clear();
    }

    public void stopSending() {
        sending = false;
    }

    public void startSending() {
        sending = true;
    }

    @Override
	public void run() {
		while (true) {
            Change change = null;
            try {
                if (sending) {
                    change = queue.take().thing();
                    perform(change);
                } else {
                    Thread.sleep(NAPTIME);
                }

				if (shutdownInProgress) {
                    queue.shutdown();
					break;
                }
            } catch (Exception e) {
                LOG.error("Updater for index {} got exception: {} for the change {}", new Object[]{index, e, change});
            }
        }
        httpClient.getConnectionManager().shutdown();
        queue.shutdown();
        LOG.info("Updater stopped for index {}.", index);

    }

    public void add(final Change change) {
        queue.add(DelayedImpl.immediate(change));
        LOG.trace("Received {}", change);
    }

    private void perform(final Change change) {
        if (change.getIndexNameOrAlias() == null) {
            perform(change, index);
            for (IndexAlias alias : aliasesService) {
                perform(change, alias.alias());
            }
        } else {
            perform(change, change.getIndexNameOrAlias());
        }
    }

	private void perform(final Change change, String indexNameOrAlias) {
		final HttpRequestBase request = toRequest(change, indexNameOrAlias);

		try {
			final HttpResponse response = httpClient.execute(request);

			if (!isSuccessful(response)) {
				LOG.warn("Request {} {} was not successful. Response status code: {}.", request.getMethod(), request.getURI(), response.getStatusLine().getStatusCode());
				retry(change, indexNameOrAlias);
			} else {
				LOG.debug("Change performed: {} : {}", indexNameOrAlias, change);
			}

			EntityUtils.consume(response.getEntity());
		} catch (IOException e) {
			LOG.warn("Error executing request {} {}: {}", request.getMethod(), request.getURI(), e.getMessage());
			retry(change, indexNameOrAlias);
		}
	}

    private HttpRequestBase toRequest(final Change change, String indexNameOrAlias) {
        final HttpRequestBase request;

		URI uri = buildURI(change, indexNameOrAlias);
        final Action action = change.action();
        switch (action) {
            case UPDATE:
                request = new HttpGet(uri);
                break;
            case DELETE:
                request = new HttpDelete(uri);
                break;
            default:
                throw new RuntimeException("Unknown action " + action + " for " + change + " on index " + indexNameOrAlias);
        }

        return request;
    }

    private URI buildURI(final Change change, String indexNameOrAlias) {
        final String type = change.type();
        final String id = change.id();
        final long version = change.version();

        final String path;


		try {
			path = String.format("/%s/%s/%s/%d", URLEncoder.encode(indexNameOrAlias, "UTF-8"), URLEncoder.encode(type, "UTF-8"), URLEncoder.encode(id, "UTF-8"), version);
		} catch (UnsupportedEncodingException e) {
			// cannot happen UTF-8 is supported
			throw new RuntimeException(e);
		}

		try {
            return new URI(uriScheme + "://" + uriHost + ":" + uriPort + path);
		} catch (URISyntaxException e) {
            throw new RuntimeException("Unexpected error building uri for change " + change + " on index or alias " + indexNameOrAlias, e);
        }
    }

    private boolean isSuccessful(final HttpResponse response) {
        final int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 200;
    }

    private void retry(Change change, String indexNameOrAlias) {
        if (change.retries() < maxRetries) {
            change = change.retried(indexNameOrAlias);
            final DelayedImpl<Change> delayedChange = new DelayedImpl<Change>(change, change.retries() * retryDelayOnFailureInMillis);
            queue.add(delayedChange);
            LOG.debug("Retrying change {} on index {} in {} milliseconds", change, index, retryDelayOnFailureInMillis);
        } else {
            logError(change);
        }
    }

    public void logError(Change change) {
        try {
            LOG.warn("Writing failed change {} to error log {}", change, errorFile.getCanonicalPath());
            final PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(errorFile, true), "UTF-8")));
            writer.println(change.toValue());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            LOG.error("I/O error: " + e.getMessage());
        }
    }
}
