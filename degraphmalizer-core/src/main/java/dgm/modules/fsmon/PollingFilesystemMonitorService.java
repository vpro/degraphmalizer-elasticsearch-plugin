package dgm.modules.fsmon;

import dgm.Service;

import java.util.Set;

import javax.inject.Named;

import com.google.inject.Inject;

/**
 * Combine all configuration monitors in a composite and create the poller
 *
 */
class PollingFilesystemMonitorService implements Service
{
    final PollingFilesystemMonitor poller;

    @Inject
    PollingFilesystemMonitorService(Set<FilesystemMonitor> filesystemMonitors, @Named("scriptFolder") String scriptFolder)
    {
        // construct the poller
        final CompositeFilesystemMonitor monitor = new CompositeFilesystemMonitor(filesystemMonitors);
        poller = new PollingFilesystemMonitor(scriptFolder, 200, monitor);
    }

    @Override
    public void start()
    {
        // start the poller (does nothing if it is already running)¸
        poller.start();
    }

    @Override
    public void stop()
    {
        // poller.waitForStop();
    }
}
