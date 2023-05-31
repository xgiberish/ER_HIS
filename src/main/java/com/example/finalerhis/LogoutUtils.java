package com.example.finalerhis;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LogoutUtils {

    public static void handleLogout(ActionEvent event, Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LogoutUtils.class.getResource("login-page.fxml"));
            Parent root = fxmlLoader.load();
            LoginController loginController = fxmlLoader.getController();
            loginController.setStage(stage);

            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Login Page");
        } catch (IOException e) {
            // Handle any IO exception that may occur while loading the login page
            e.printStackTrace();
        }
    }
}
