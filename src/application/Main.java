// Tyler Latawiec
// Brian Huang

package application;
	

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.getIcons().add(new Image("musicicon.png"));
			primaryStage.setScene(scene);
			primaryStage.setTitle("Song Library");
			primaryStage.initStyle(StageStyle.DECORATED);
			
			
			primaryStage.show();
			
		
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
