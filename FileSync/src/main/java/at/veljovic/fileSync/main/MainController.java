package at.veljovic.fileSync.main;

import java.util.ArrayList;
import java.util.List;

import at.veljovic.fileSync.main.Main.ApplicationListener;
import at.veljovic.fileSync.main.dirObserver.DirObserverController;
import at.veljovic.fileSync.main.fileSync.FileSyncController;
import at.veljovic.fileSync.main.search.SearchController;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class MainController implements ApplicationListener {
	@FXML
	private MenuBar menuBar;
	@FXML
	private MenuItem closeMenuItem;

	@FXML
	private SearchController searchController;
	@FXML
	private FileSyncController fileSyncController;
	@FXML
	private DirObserverController dirObserverController;

	private final List<ApplicationListener> listeners = new ArrayList<>();

	public void initialize() {
		listeners.add(searchController);
		listeners.add(fileSyncController);
		listeners.add(dirObserverController);
	}

	public void onActionCloseMenuItem() {
		((Stage) menuBar.getScene().getWindow()).close();
	}

	@Override
	public void onShutdown() {
		listeners.forEach(l -> l.onShutdown());
	}
}
