package at.veljovic.fileOps.logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.FileUtils;

/**
 * 
 * @author av
 *
 */
public class FileSync implements Runnable {

	private final File sourceDir;
	private final File destDir;
	private final List<ProgressListener> listeners = new ArrayList<>();
	private final AtomicBoolean isStopped = new AtomicBoolean(false);

	public FileSync(File sourceDir, File destDir) {
		this.sourceDir = sourceDir;
		this.destDir = destDir;
	}

	public void addProgressListener(ProgressListener l) {
		listeners.add(l);
	}

	@Override
	public void run() {
		try {
			Map<File, File> filesToCopy = determineFilesToCopy();
			double size = filesToCopy.size();
			AtomicReference<Double> currPos = new AtomicReference<>(0d);
			filesToCopy.forEach((srcFile, destFile) -> {
				listeners.forEach(l -> l.getProgress(currPos.get() / size, srcFile));
				copy(srcFile, destFile);
				currPos.set(currPos.get() + 1);
				if (isStopped.get()) {
					throw new StoppedException();
				}
			});
		} catch (StoppedException expected) {

		}
		listeners.forEach(l -> l.done());
		listeners.clear();
	}

	private Map<File, File> determineFilesToCopy() {
		final Map<File, File> filesToCopy = new HashMap<>();
		FileUtils.iterateFiles(sourceDir, null, true).forEachRemaining(srcFile -> {
			File destFile = new File(
					srcFile.getAbsolutePath().replace(sourceDir.getAbsolutePath(), destDir.getAbsolutePath() + "\\"));
			if (isStopped.get()) {
				throw new StoppedException();
			} else if (isFileChanged(srcFile, destFile)) {
				filesToCopy.put(srcFile, destFile);
			}
		});
		return filesToCopy;
	}

	private static void copy(File srcFile, File destFile) {
		try {
			FileUtils.copyFile(srcFile, destFile);
			System.out.println("copied " + srcFile.getAbsolutePath());
		} catch (IOException e) {

		}
	}

	private boolean isFileChanged(File srcFile, File destFile) {
		return !destFile.exists() || destFile.length() != srcFile.length()
				|| destFile.lastModified() != srcFile.lastModified();
	}

	public void stop() {
		isStopped.set(true);
	}

	/**
	 * 
	 * @author av
	 *
	 */
	public interface ProgressListener {

		void getProgress(double d, File file);

		void done();
	}

	/**
	 * 
	 * @author av
	 *
	 */
	private class StoppedException extends RuntimeException {
		private static final long serialVersionUID = 1L;

	}
}
