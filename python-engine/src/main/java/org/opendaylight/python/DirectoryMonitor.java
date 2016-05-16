package org.opendaylight.python;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryMonitor implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(DirectoryMonitor.class);

    protected Path watchedPath;
    protected Listener listener;

    public DirectoryMonitor(Path path, Listener listener) {
        watchedPath = path;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            for (Path p : Files.newDirectoryStream(watchedPath, "*.py")) {
                listener.add(p);
            }

            WatchService watcher = FileSystems.getDefault().newWatchService();
            WatchKey watchKey = watchedPath.register(watcher,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            for (;;) {

                // wait for key to be signaled
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException x) {
                    return;
                }

                for (WatchEvent<?> event: key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>)event;
                    Path filename = ev.context();
                    Path child = watchedPath.resolve(filename);

                    if (kind == StandardWatchEventKinds.ENTRY_DELETE
                            || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        try {
                            log.warn("Removing resource {}", filename);
                            listener.remove(child);
                        } catch (Throwable t) {
                            logError(t, "remove", filename);
                        }
                    }

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE
                            || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        try {
                            log.warn("Adding resource {}", filename);
                            listener.add(child);
                        } catch (Throwable t) {
                            logError(t, "add", filename);
                        }
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (IOException e) {
            log.error("Error initialising Listener", e);
        }
    }

    protected void logError(Throwable t, String filename, Path operation) {
        log.error("Error during resource {} for {}", operation, filename);
        log.error("Exception: ", t);
    }

    public interface Listener {
        void add(Path path);
        void remove(Path path);
    }
}
