package at.veljovic.fileSync.main.fileSync;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import at.veljovic.fileSync.logic.FileSync;
import at.veljovic.fileSync.logic.FileSync.ProgressListener;
import at.veljovic.fileSync.main.Main.ApplicationListener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;

/**
 * 
 * @author av
 *
 */
public class FileSyncController implements ProgressListener, ApplicationListener {
	@FXML
	private Button srcButton;
	@FXML
	private Button destButton;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Button startButton;
	@FXML
	private Button cancelButton;
	@FXML
	private Label progressLabel;

	private Optional<File> srcDir = Optional.empty();
	private Optional<File> destDir = Optional.empty();

	private final Map<Node, String> defaultTexts = new HashMap<>();
	private Optional<FileSync> fileSync = Optional.empty();

	public FileSyncController() {
	}

	public void initialize() {
		defaultTexts.put(srcButton, srcButton.getText());
		defaultTexts.put(destButton, destButton.getText());
	}

	@FXML
	public void setFile(ActionEvent e) {
		Button targetButton = (Button) e.getSource();
		DirectoryChooser directoryChooser = new DirectoryChooser();
		Optional<File> dir = Optional.ofNullable(directoryChooser.showDialog(targetButton.getScene().getWindow()));
		targetButton.setText(dir.map(d -> d.getAbsolutePath()).orElse(defaultTexts.get(targetButton)));

		if (targetButton == srcButton) {
			srcDir = dir;
		} else if (targetButton == destButton) {
			destDir = dir;
		} else {
			throw new IllegalArgumentException();
		}

		updateControlStates();
	}

	public void updateControlStates() {
		startButton.setDisable(!srcDir.isPresent() || !destDir.isPresent());
	}

	@FXML
	public void onActionStartButton() {
		fileSync = Optional.of(new FileSync(srcDir.get(), destDir.get()));
		fileSync.get().addProgressListener(this);

		cancelButton.setDisable(false);
		startButton.setDisable(true);
		progressBar.setVisible(true);
		new Thread(fileSync.get(), getClass().getName()).start();
	}

	@Override
	public void getProgress(double d, File file) {
		Platform.runLater(() -> {

			progressBar.setProgress(d);
			progressLabel.setText(file.getAbsolutePath());
		});
	}

	@Override
	public void done() {
		Platform.runLater(() -> {
			progressBar.setProgress(0);
			progressLabel.setText("");
			cancelButton.setDisable(true);
			startButton.setDisable(false);
			progressBar.setVisible(false);
		});
	}

	public void onActionCancelButton() {
		fileSync.ifPresent(f -> f.stop());
	}

	@Override
	public void onShutdown() {
		fileSync.ifPresent(f -> f.stop());
	}
}
