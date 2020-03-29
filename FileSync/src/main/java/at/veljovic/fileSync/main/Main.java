package at.veljovic.fileSync.main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 
 * @author av
 *
 */
public class Main extends Application {

	@Override
	public void start(Stage stage) throws IOException {
		Parent root = new FXMLLoader(getClass().getResource("Main.fxml")).load();
		stage.setScene(new Scene(root));
		stage.setTitle("FileSync");
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
