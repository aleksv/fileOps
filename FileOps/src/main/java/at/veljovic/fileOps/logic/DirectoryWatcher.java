package at.veljovic.fileOps.logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

			Files.walkFileTree(dir.toPath(), new FileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					dir.register(
							watchService,
							StandardWatchEventKinds.ENTRY_CREATE,
							StandardWatchEventKinds.ENTRY_DELETE,
							StandardWatchEventKinds.ENTRY_MODIFY);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path path, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
			});

			while (!isStopped.get()) {
				Optional<WatchKey> key = Optional.ofNullable(watchService.poll());
				if (key.isPresent()) {
					for (WatchEvent<?> event : key.map(k -> k.pollEvents()).get()) {
						File file = ((Path) event.context()).toFile();
						listeners.forEach(l -> {
							if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
								l.onCreate(file);
							} else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
								l.onDelete(file);
							} else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
								l.onModify(file);
							}
						});

					}
					key.ifPresent(k -> k.reset());
				}
			}

			watchService.close();
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