package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MyApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        primaryStage.setTitle("Receptek");
        primaryStage.setResizable(false);
        Scene r = new Scene(root);
        primaryStage.setScene(r);
        r.getStylesheets().add
                (getClass().getResource("/css/home.css").toExternalForm());
        primaryStage.show();
    }

}
