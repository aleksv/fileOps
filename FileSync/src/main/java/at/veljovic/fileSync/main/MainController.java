package at.veljovic.fileSync.main;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class MainController {
	@FXML
	private MenuBar menuBar;
	@FXML
	private MenuItem closeMenuItem;

	public void onActionCloseMenuItem() {
		((Stage) menuBar.getScene().getWindow()).close();
	}
}
