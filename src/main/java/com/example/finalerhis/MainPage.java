package com.example.finalerhis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class MainPage {
    @FXML
    public TitledPane patientViewPane;
    public TitledPane intensiveRoomsPane;
    public Button logoutButton;
    private Stage stage; // Declare the stage variable
    String url = "jdbc:mysql://localhost:3306/hospital_users";
    String username = "root";
    String password = "";

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void openPatientInformation1(ActionEvent event) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-regular1.fxml"));
            root = loader.load();

            // Retrieve patient ID from the rooms table
            String patientID = getPatientIDFromRoomNumber(1); // Replace 1 with the actual room number

            if (patientID != null) {
                // If patient ID is present, fill out the information
                PatientRegular1 patientInfoController = loader.getController();
                patientInfoController.fillFieldsWithPatientData(patientID);
            } else {
                // If no patient ID is present, display a message
                PatientRegular1 patientInfoController = loader.getController();
                patientInfoController.displayEmptyRoomMessage();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage newStage = new Stage(); // Create a new Stage instance
        newStage.setScene(new Scene(root));
        newStage.setTitle("Regular Room 1");
        newStage.show();
    }

    private String getPatientIDFromRoomNumber(int roomNumber) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT patient_Id FROM rooms WHERE room_number = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, roomNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("patient_Id");
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @FXML
    private void openPatientInformation2(ActionEvent event) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-regular2.fxml"));
            root = loader.load();

            // Retrieve patient ID from the rooms table
            String patientID = getPatientIDFromRoomNumber(2); // Replace 1 with the actual room number

            if (patientID != null) {
                // If patient ID is present, fill out the information
                PatientRegular2 patientInfoController = loader.getController();
                patientInfoController.fillFieldsWithPatientData2(patientID);
            } else {
                // If no patient ID is present, display a message
                PatientRegular2 patientInfoController = loader.getController();
                patientInfoController.displayEmptyRoomMessage();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage newStage = new Stage(); // Create a new Stage instance
        newStage.setScene(new Scene(root));
        newStage.setTitle("Regular Room 2");
        newStage.show();    }

    @FXML
    private void openPatientInformation3(ActionEvent event) {
        openPatientInformation("patient-regular3.fxml", "Regular Room 3");
    }

    @FXML
    private void openPatientInformation4(ActionEvent event) {
        openPatientInformation("patient-regular4.fxml", "Regular Room 4");
    }

    @FXML
    private void openPatientInformationICU1(ActionEvent event) {
        openPatientInformation("patient-icu1.fxml", "Intensive Room 1");
    }

    @FXML
    private void openPatientInformationICU2(ActionEvent event) {
        openPatientInformation("patient-icu2.fxml", "Intensive Room 2");
    }

    private void openPatientInformation(String fxmlFile, String title) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage newStage = new Stage(); // Create a new Stage instance
        newStage.setScene(new Scene(root));
        newStage.setTitle(title);
        newStage.show();
    }

    @FXML
    private TitledPane regularRoomsPane;

    @FXML
    private void toggleTitledPane(ActionEvent event) {
        regularRoomsPane.setExpanded(!regularRoomsPane.isExpanded());
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Stage currentStage = (Stage) logoutButton.getScene().getWindow();
        LogoutUtils.handleLogout(event, currentStage);
    }
}