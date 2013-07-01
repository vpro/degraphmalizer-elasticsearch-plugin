package dgm.configuration;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

public class Configurations {
    final static private Logger LOG = LoggerFactory.getLogger(Configurations.class);

    /**
     * Find all TypeConfigs with specified source index and source type
     */
    public static Iterable<TypeConfig> configsFor(Configuration cfg, String srcIndex, String srcType) {
        StringBuilder logMessage = null;

        if (LOG.isDebugEnabled()) {
            logMessage = new StringBuilder("Matching request for /");
            logMessage.append(srcIndex).append("/").append(srcType);
            logMessage.append(" to [");
        }

        final List<TypeConfig> configs = new ArrayList<TypeConfig>();

        // find all matching configs
        for (IndexConfig i : cfg.indices().values())
            for (TypeConfig t : i.types().values())
                if (srcIndex.equals(t.sourceIndex()) && (srcType == null || srcType.equals(t.sourceType()))) {
                    if ((logMessage != null)) {
                        logMessage.append(" /").append(t.targetIndex());
                        logMessage.append("/").append(t.targetType());
                        logMessage.append(", ");
                    }

                    configs.add(t);
                }

        if (logMessage != null)
            LOG.debug(logMessage.append("]").toString());

        return configs;
    }

    public static Predicate<URL> IS_DIRECTORY = new Predicate<URL>() {
        @Override
        public boolean apply(URL url) {
            if (url.getProtocol().equals("file")) {
                File file = new File(url.getFile());
                return file.exists() && file.isDirectory() && file.canRead();
            } else {
                try {
                    return new URL(url.toString() + "/INDEX").openConnection().getDoInput();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                    return false;
                }
            }
        }
    };

    public static URL[] list(final URL url, final Predicate<URL> filter) throws IOException {
        return list(url, filter, true);

    }

    private static URL[] list(final URL url, final Predicate<URL> filter, boolean enterDirectories) throws IOException {
        List<URL> result = new ArrayList<URL>();
        URL f = url;
        if (enterDirectories && f.getProtocol().equals("file") && new File(f.getPath()).isDirectory()) {
            File dir = new File(f.getPath());
            LOG.info("Reading directory {}", dir);

            for (String fileName : new File(f.getPath()).list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    try {
                        File file = new File(dir, name);
                        return file.canRead() && filter.apply(file.toURI().toURL());
                    } catch (MalformedURLException e) {
                        LOG.error(e.getMessage(), e);
                        return false;
                    }


                }
            })) {
                File file = new File(dir, fileName);
                result.addAll(Arrays.asList(list(file.toURI().toURL(), filter, false)));
            }
        } else if (f.getPath().replaceAll(".*/", "").equals("INDEX")) {
            LOG.info("Reading index file {}", f);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(f.openStream()));

                String line = reader.readLine();
                while (line != null) {
                    line = line.trim();
                    if (line.startsWith("#") || line.length() == 0) {
                        continue;
                    }
                    URL u = new URL(f.toString().substring(0, f.toString().length() - "INDEX".length()) + "/" + line);
                    result.addAll(Arrays.asList(list(u, filter)));
                    line = reader.readLine();
                }
            } finally {
                IOUtils.closeQuietly(reader);
            }
        } else {
            if (filter != null) {
                if (filter.apply(f)) {
                    result.add(f);
                } else {
                    //throw new ConfigurationException(f + " does not apply  to " + filter);
                }
            }
        }
        return result.toArray(new URL[result.size()]);
    }
}
