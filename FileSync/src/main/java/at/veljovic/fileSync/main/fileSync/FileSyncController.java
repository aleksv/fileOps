package at.veljovic.fileSync.main.fileSync;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import at.veljovic.fileSync.logic.FileSync;
import at.veljovic.fileSync.logic.FileSync.ProgressListener;
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
public class FileSyncController implements ProgressListener {
	@FXML
	private Button srcButton;
	@FXML
	private Button destButton;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Button startButton;
	@FXML
	private Label progressLabel;

	private Optional<File> srcDir = Optional.empty();
	private Optional<File> destDir = Optional.empty();

	private final Map<Node, String> defaultTexts = new HashMap<>();

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
