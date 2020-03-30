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

	private MainController mainController;

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
		Parent root = fxmlLoader.load();
		stage.setScene(new Scene(root));
		stage.setTitle("FileOps");
		stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		stage.setWidth(500);
		stage.setMinWidth(500);
		stage.setHeight(500);
		stage.setMinHeight(500);
		stage.show();

		mainController = fxmlLoader.getController();

	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void stop() throws Exception {
		mainController.onShutdown();
	}

	public interface ApplicationListener {
		void onShutdown();
	}
}
