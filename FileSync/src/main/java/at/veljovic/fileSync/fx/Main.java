package at.veljovic.fileSync.fx;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage stage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));

		stage.setScene(new Scene(root));
		stage.show();

		Button srcButton = (Button) root.getScene().lookup("#srcButton");

		srcButton.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("clicky");

			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
