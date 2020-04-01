package at.veljovic.fileOps.main.dirObserver;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import at.veljovic.fileOps.logic.DirectoryWatcher;
import at.veljovic.fileOps.logic.DirectoryWatcher.FileWatcherListener;
import at.veljovic.fileOps.main.Main.ApplicationListener;
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

public class DirObserverController implements FileWatcherListener, ApplicationListener {
	@FXML
	private Button directoryButton;
	@FXML
	private Button startButton;
	@FXML
	private Button stopButton;
	@FXML
	private ProgressIndicator progressIndicator;
	@FXML
	private ListView<String> resultListView;
	private final ObservableList<String> resultList = FXCollections.observableArrayList();
	private Optional<DirectoryWatcher> directoryWatcher = Optional.empty();

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
		directoryWatcher = Optional.of(new DirectoryWatcher(dir.get()));
		directoryWatcher.get().addListener(this);
		new Thread(directoryWatcher.get(), getClass().getName()).start();
		startButton.setDisable(true);
		stopButton.setDisable(false);
		progressIndicator.setVisible(true);
	}

	public void onActionStopButton() throws Exception {
		directoryWatcher.get().stop();
		startButton.setDisable(false);
		stopButton.setDisable(true);
		progressIndicator.setVisible(false);
	}

	@Override
	public void onCreate(File file) {
		addFileDetails(FileOp.C, file);
	}

	private void addFileDetails(FileOp fo, File file) {
		File t = new File(file.getAbsolutePath());
		Platform.runLater(() -> {
			resultList.add("[" + fo + "] [" + new Date() + "] " + t.getAbsolutePath());
		});
	}

	@Override
	public void onDelete(File file) {
		addFileDetails(FileOp.D, file);
	}

	@Override
	public void onModify(File file) {
		addFileDetails(FileOp.M, file);
	}

	private enum FileOp {
		C, D, M;
	}

	@Override
	public void onShutdown() {
		directoryWatcher.ifPresent(d -> d.stop());
	}
}
