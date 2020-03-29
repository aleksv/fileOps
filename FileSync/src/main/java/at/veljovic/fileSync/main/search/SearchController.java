package at.veljovic.fileSync.main.search;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class SearchController {

	@FXML
	private TextField fileTextfield;
	@FXML
	private CheckBox regexCheckbox;
	@FXML
	private CheckBox caseSensitiveCheckbox;

	public SearchController() {
	}

	public void onActionRegexCheckbox() {
		System.out.println(1);
	}
}
