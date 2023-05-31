package com.example.finalerhis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminPanel {
    public Button logoutButton2;

    private Stage stage; // Declare the stage variable

    // Setter method to set the stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }


    @FXML
    private void handleLogout(ActionEvent event) {
        Stage stage = (Stage) logoutButton2.getScene().getWindow();
        LogoutUtils.handleLogout(event, stage);
    }


}
