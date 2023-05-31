package com.example.finalerhis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;


import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-page.fxml"));
        Parent root = fxmlLoader.load();
        LoginController loginController = fxmlLoader.getController();

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();

        loginController.setStage(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}
