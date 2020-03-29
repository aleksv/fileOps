package at.veljovic.fileSync.main.search;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import at.veljovic.fileSync.logic.FileSearch;
import at.veljovic.fileSync.logic.FileSearch.FileSearchListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

public class SearchController implements FileSearchListener {

	@FXML
	private TextField fileTextfield;
	@FXML
	private CheckBox regexCheckbox;
	@FXML
	private CheckBox caseSensitiveCheckbox;
	@FXML
	private Button searchButton;
	@FXML
	private Button cancelButton;
	@FXML
	private ProgressIndicator progressIndicator;
	@FXML
	private ListView<File> resultListView;
	private ObservableList<File> resultList = FXCollections.observableArrayList();

	private List<FileSearch> searchables = new ArrayList<>();

	public SearchController() {
	}

	public void onActionRegexCheckbox() {
		System.out.println(1);
	}

	public void onActionSearchButton() {
		System.out.println("onActionSearchButton");
		String filename = Optional.ofNullable(fileTextfield.getText()).orElse("");

		searchables.clear();
		Arrays.asList(File.listRoots()).forEach(drive -> {
			searchables.add(
					new FileSearch(drive, filename, regexCheckbox.isSelected(), caseSensitiveCheckbox.isSelected()));
		});
		searchables.forEach(search -> {
			search.addListener(this);
			new Thread(search).start();
		});
	}

	public void onActionCancelButton() {
		System.out.println("onActionCancelButton");
		searchables.forEach(search -> search.stop());
		searchables.clear();
	}

	@Override
	public void start() {
		Platform.runLater(() -> {
			resultList.clear();
			resultListView.setItems(resultList);
			cancelButton.setDisable(false);
			searchButton.setDisable(true);
			progressIndicator.setVisible(true);
		});
	}

	@Override
	public void onFileFound(File file) {
		Platform.runLater(() -> {
			resultList.add(file);
		});
	}

	@Override
	public void done() {
		Platform.runLater(() -> {
			cancelButton.setDisable(true);
			searchButton.setDisable(false);
			progressIndicator.setVisible(false);
		});
	}

	@Override
	public void onFileFail(File file) {
		Platform.runLater(() -> {

		});
	}
}
