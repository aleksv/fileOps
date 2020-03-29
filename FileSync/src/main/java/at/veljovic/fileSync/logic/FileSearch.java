package at.veljovic.fileSync.logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * 
 * @author av
 *
 */
public class FileSearch implements Runnable {

	private final File dir;
	private final String filename;
	private final List<FileSearchListener> listeners = Collections.synchronizedList(new ArrayList<>());
	private AtomicBoolean isStopped = new AtomicBoolean(false);
	private final boolean isRegex;
	private boolean isCaseSensitive;
	private Pattern regexPattern;

	public FileSearch(File dir, String filename) {
		this(dir, filename, false, false);
	}

	public FileSearch(File dir, String filename, boolean isRegex) {
		this(dir, filename, false, false);
	}

	public FileSearch(File dir, String filename, boolean isRegex, boolean isCaseSensitive) {
		this.dir = dir;
		this.filename = filename;
		this.isRegex = isRegex;
		this.isCaseSensitive = isCaseSensitive;

		if (isRegex) {
			regexPattern = Pattern.compile(filename, !isCaseSensitive ? Pattern.CASE_INSENSITIVE : 0);
		}
	}

	@Override
	public void run() {
		listeners.forEach(l -> l.start());

		try {

			Files.walkFileTree(dir.toPath(), new FileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					return getVisitResult();
				}

				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
					File file = path.toFile();
					if (isFilenameMatch(file)) {
						listeners.forEach(l -> l.onFileFound(file));
					}
					return getVisitResult();
				}

				@Override
				public FileVisitResult visitFileFailed(Path path, IOException exc) throws IOException {
					listeners.forEach(l -> l.onFileFail(path.toFile()));
					return getVisitResult();
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					return getVisitResult();
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		listeners.forEach(l -> l.done());
		listeners.clear();
	}

	private boolean isFilenameMatch(File file) {
		if (isRegex) {
			return regexPattern.matcher(file.getName()).matches();
		} else if (isCaseSensitive) {
			return file.getName().equals(filename);
		}
		return file.getName().equalsIgnoreCase(filename);
	}

	public void addListener(FileSearchListener listener) {
		listeners.add(listener);
	}

	public void stop() {
		isStopped.set(true);
	}

	private FileVisitResult getVisitResult() {
		return Optional.of(isStopped.get())
				.map(stopped -> stopped ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE).get();
	}

	/**
	 * 
	 * @author av
	 *
	 */
	public interface FileSearchListener {
		void start();

		void onFileFound(File file);

		void onFileFail(File file);

		void done();
	}

}
