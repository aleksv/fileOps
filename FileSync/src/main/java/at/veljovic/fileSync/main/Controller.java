package at.veljovic.fileSync.main;

import java.io.File;
import java.util.Optional;

import at.veljovic.fileSync.model.FileSync;
import at.veljovic.fileSync.model.FileSync.ProgressListener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;

/**
 * 
 * @author av
 *
 */
public class Controller implements ProgressListener {
	@FXML
	private Button srcButton;
	@FXML
	private Label srcLabel;
	@FXML
	private Button destButton;
	@FXML
	private Label destLabel;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Button startButton;
	@FXML
	private Label progressLabel;

	private Optional<File> srcDir = Optional.empty();
	private Optional<File> destDir = Optional.empty();

	public Controller() {
	}

	public void initialize() {
	}

	@FXML
	public void setFile(ActionEvent e) {
		Button targetButton = (Button) e.getSource();
		DirectoryChooser directoryChooser = new DirectoryChooser();
		Optional<File> dir = Optional.ofNullable(directoryChooser.showDialog(targetButton.getScene().getWindow()));
		Label label;
		if (targetButton == srcButton) {
			label = srcLabel;
			srcDir = dir;
		} else if (targetButton == destButton) {
			label = destLabel;
			destDir = dir;
		} else {
			throw new IllegalArgumentException();
		}
		label.setText(dir.map(d -> d.getAbsolutePath()).orElse(""));
		updateControlStates();
	}

	public void updateControlStates() {
		startButton.setDisable(!srcDir.isPresent() || !destDir.isPresent());
	}

	@FXML
	public void start() {
		FileSync fileSync = new FileSync(srcDir.get(), destDir.get());
		fileSync.addProgressListener(this);
		new Thread(fileSync).start();
	}

	@Override
	public void getProgress(double d, File file) {
		Platform.runLater(() -> {
			progressBar.setProgress(d);
			progressLabel.setText(file.getName());
		});
	}

	@Override
	public void done() {
		Platform.runLater(() -> {
			progressBar.setProgress(0);
			progressLabel.setText("");
		});
	}
}
