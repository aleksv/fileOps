package at.veljovic.fileSync.main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * 
 * @author av
 *
 */
public class Main extends Application {

	@Override
	public void start(Stage stage) throws IOException {
		Parent root = new FXMLLoader(getClass().getResource("main.fxml")).load();
		stage.setScene(new Scene(root));
		stage.setTitle("FileOps");
		stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		stage.setMaxWidth(900);
		stage.setMinWidth(500);
		stage.setWidth(500);
		stage.setMaxHeight(900);
		stage.setMinHeight(300);
		stage.setHeight(500);

		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
