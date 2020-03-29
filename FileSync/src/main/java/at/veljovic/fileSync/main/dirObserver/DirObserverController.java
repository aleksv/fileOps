package at.veljovic.fileSync.main.dirObserver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import at.veljovic.fileSync.logic.DirectoryWatcher;
import at.veljovic.fileSync.logic.DirectoryWatcher.FileWatcherListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.DirectoryChooser;

public class DirObserverController implements FileWatcherListener {
	@FXML
	private Button directoryButton;
	@FXML
	private Button startButton;
	@FXML
	private Button stopButton;
	@FXML
	private ProgressIndicator progressIndicator;
	@FXML
	private ListView<File> resultListView;
	private final ObservableList<File> resultList = FXCollections.observableArrayList();
	private DirectoryWatcher directoryWatcher;

	private Optional<File> dir = Optional.empty();

	private final Map<Node, String> defaultTexts = new HashMap<>();

	public void initialize() {
		defaultTexts.put(directoryButton, directoryButton.getText());
		resultListView.setItems(resultList);
	}

	public void onActionSetDirectory(ActionEvent e) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		dir = Optional.ofNullable(directoryChooser.showDialog(directoryButton.getScene().getWindow()));
		directoryButton.setText(dir.map(d -> d.getAbsolutePath()).orElse(defaultTexts.get(directoryButton)));
		updateControlStates();
	}

	public void updateControlStates() {
		startButton.setDisable(!dir.isPresent());
	}

	public void onActionStartButton() throws IOException, InterruptedException {
		resultList.clear();
		directoryWatcher = new DirectoryWatcher(dir.get());
		directoryWatcher.addListener(this);
		new Thread(directoryWatcher).start();
		startButton.setDisable(true);
		stopButton.setDisable(false);
		progressIndicator.setVisible(true);
	}

	public void onActionStopButton() throws Exception {
		directoryWatcher.stop();
		startButton.setDisable(false);
		stopButton.setDisable(true);
		progressIndicator.setVisible(false);
	}

	@Override
	public void onCreate(File file) {
		Platform.runLater(() -> {
			resultList.add(file);
		});
	}

	@Override
	public void onDelete(File file) {
		Platform.runLater(() -> {
			resultList.add(file);
		});
	}

	@Override
	public void onModify(File file) {
		Platform.runLater(() -> {
			resultList.add(file);
		});
	}

}
