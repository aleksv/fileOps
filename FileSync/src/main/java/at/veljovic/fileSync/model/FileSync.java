package at.veljovic.fileSync.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class FileSync implements Runnable {

	private File sourceDir;
	private File destDir;

	public FileSync(File sourceDir, File destDir) {
		this.sourceDir = sourceDir;
		this.destDir = destDir;
	}

	@Override
	public void run() {
		determineFilesToCopy().forEach(FileSync::copy);
	}

	private Map<File, File> determineFilesToCopy() {
		final Map<File, File> filesToCopy = new HashMap<>();
		FileUtils.iterateFiles(sourceDir, null, true).forEachRemaining(srcFile -> {
			File destFile = new File(
					srcFile.getAbsolutePath().replace(sourceDir.getAbsolutePath(), destDir.getAbsolutePath()));

			if (isFileChanged(srcFile, destFile)) {
				filesToCopy.put(srcFile, destFile);
			} else {
				System.out.println("skip " + srcFile.getName());
			}
		});
		return filesToCopy;
	}

	private static void copy(File srcFile, File destFile) {
		try {
			FileUtils.copyFile(srcFile, destFile);
			System.out.println("copied " + srcFile.getName());
		} catch (IOException e) {

		}
	}

	private boolean isFileChanged(File srcFile, File destFile) {
		return !destFile.exists() || destFile.length() != srcFile.length()
				|| destFile.lastModified() != srcFile.lastModified();
	}

}
