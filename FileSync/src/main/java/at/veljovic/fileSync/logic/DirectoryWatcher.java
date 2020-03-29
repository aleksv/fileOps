package at.veljovic.fileSync.logic;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DirectoryWatcher implements Runnable {

	private File dir;
	private WatchService watchService;
	private final List<FileWatcherListener> listeners = new ArrayList<>();
	private AtomicBoolean isStopped = new AtomicBoolean(false);

	public DirectoryWatcher(File dir) {
		this.dir = dir;
	}

	@Override
	public void run() {
		try {
			watchService = FileSystems.getDefault().newWatchService();

			Path path = dir.toPath();

			path.register(
					watchService,
					StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY);

			WatchKey key;
			while ((key = watchService.take()) != null) {
				for (WatchEvent<?> event : key.pollEvents()) {
					File file = ((Path) event.context()).toFile();
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
						listeners.forEach(l -> l.onCreate(file));
					} else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
						listeners.forEach(l -> l.onDelete(file));
					} else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
						listeners.forEach(l -> l.onModify(file));
					}
				}
				key.reset();

				if (isStopped.get()) {
					break;
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		listeners.clear();
	}

	public void stop() {
		isStopped.set(true);
	}

	public void addListener(FileWatcherListener listener) {
		listeners.add(listener);
	}

	public interface FileWatcherListener {
		void onCreate(File file);

		void onDelete(File file);

		void onModify(File file);

	}

}