package at.veljovic.fileOps.main.search;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import at.veljovic.fileOps.logic.FileSearch;
import at.veljovic.fileOps.logic.FileSearch.FileSearchListener;
import at.veljovic.fileOps.main.Main.ApplicationListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;

public class SearchController implements FileSearchListener, ApplicationListener {

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
	@FXML
	private ContextMenu resultFileContextMenu;

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
			new Thread(search, getClass().getName()).start();
		});
	}

	public void onClickresultListView(MouseEvent evt) {
		if (evt.getClickCount() == 2) {
			File selectedFile = resultListView.getSelectionModel().getSelectedItem();
			try {
				Desktop.getDesktop().open(selectedFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else if (evt.isSecondaryButtonDown()) {
			resultFileContextMenu.show((Node) evt.getSource(), 0, 0);
		}
	}

	public void onActionOpenFileMenuItem() {
		File selectedFile = resultListView.getSelectionModel().getSelectedItem();
		try {
			Desktop.getDesktop().open(selectedFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void onActionShowInExplorerMenuItem() {
		File selectedFile = resultListView.getSelectionModel().getSelectedItem();
		try {
			Runtime.getRuntime().exec("explorer.exe /select," + selectedFile.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onActionCopyPathMenuItem() {
		File selectedFile = resultListView.getSelectionModel().getSelectedItem();
		ClipboardContent content = new ClipboardContent();
		content.putString(selectedFile.getAbsolutePath());
		Clipboard.getSystemClipboard().setContent(content);
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

	@Override
	public void onShutdown() {
		onActionCancelButton();
	}
}
