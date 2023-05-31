package com.example.finalerhis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static javafx.application.Application.launch;

public class MainPage {
    public TitledPane patientViewPane;
    public TitledPane intensiveRoomsPane;
    public Button logoutButton;
    private Stage stage; // Declare the stage variable

    // Setter method to set the stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void openPatientInformation1(ActionEvent event) {
        // Code to handle opening a bigger pane with patient information
        // You can use JavaFX APIs to create and show the additional pane with patient information
        // Retrieve the relevant patient information based on the clicked button and populate the pane
        // Show the pane using a new stage or a dialog

        // Example code to create a new stage and show the patient information pane
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("patient-regular1.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void openPatientInformation2(ActionEvent event) {
        // Code to handle opening a bigger pane with patient information
        // You can use JavaFX APIs to create and show the additional pane with patient information
        // Retrieve the relevant patient information based on the clicked button and populate the pane
        // Show the pane using a new stage or a dialog

        // Example code to create a new stage and show the patient information pane
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("patient-regular2.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    private void openPatientInformation3(ActionEvent event) {
        // Code to handle opening a bigger pane with patient information
        // You can use JavaFX APIs to create and show the additional pane with patient information
        // Retrieve the relevant patient information based on the clicked button and populate the pane
        // Show the pane using a new stage or a dialog

        // Example code to create a new stage and show the patient information pane
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("patient-regular3.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    private void openPatientInformation4(ActionEvent event) {
        // Code to handle opening a bigger pane with patient information
        // You can use JavaFX APIs to create and show the additional pane with patient information
        // Retrieve the relevant patient information based on the clicked button and populate the pane
        // Show the pane using a new stage or a dialog

        // Example code to create a new stage and show the patient information pane
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("patient-regular4.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    private void openPatientInformationICU1(ActionEvent event) {
        // Code to handle opening a bigger pane with patient information
        // You can use JavaFX APIs to create and show the additional pane with patient information
        // Retrieve the relevant patient information based on the clicked button and populate the pane
        // Show the pane using a new stage or a dialog

        // Example code to create a new stage and show the patient information pane
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("patient-icu1.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML

    private void openPatientInformationICU2(ActionEvent event) {
        // Code to handle opening a bigger pane with patient information
        // You can use JavaFX APIs to create and show the additional pane with patient information
        // Retrieve the relevant patient information based on the clicked button and populate the pane
        // Show the pane using a new stage or a dialog

        // Example code to create a new stage and show the patient information pane
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("patient-icu2.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    private TitledPane regularRoomsPane;

    @FXML
    private void toggleTitledPane(ActionEvent event) {
        regularRoomsPane.setExpanded(!regularRoomsPane.isExpanded());
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        LogoutUtils.handleLogout(event, stage);
    }



}
