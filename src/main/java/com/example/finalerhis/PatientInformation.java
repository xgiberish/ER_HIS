package com.example.finalerhis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public class PatientInformation {
    @FXML
    private Button waitingRoomButton;
    @FXML
    private Button admitButton;
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField diagnosisField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField genderField;
    @FXML
    private TextField allergiesField;
    @FXML
    private TextField admissionDateField;
    @FXML
    private TextArea treatmentField;
    @FXML
    private Button searchButton;
    @FXML
    private Button addMedication;
    @FXML
    private Button dischargeButton;
    @FXML
    private ComboBox<String> triageComboBox;
    @FXML
    private Circle triageCircle;
    @FXML
    private RadialGradient triageGradient;

    private boolean isNewPatient = false;

    @FXML
    private void initialize() {
        idField.setEditable(true);
        nameField.setEditable(false);
        diagnosisField.setEditable(false);
        ageField.setEditable(false);
        genderField.setEditable(false);
        allergiesField.setEditable(false);
        admitButton.setDisable(true);
        addMedication.setDisable(true);
        waitingRoomButton.setDisable(true);
        admitButton.setDisable(true);
        dischargeButton.setDisable(true);

        // Add items to the triageComboBox
       // triageComboBox.getItems().addAll("white", "blue", "green", "orange", "red");
        triageComboBox.setOnAction(this::onTriageComboBoxAction);
    }
    @FXML
    private void onTriageComboBoxAction(ActionEvent event) {
        String selectedColor = triageComboBox.getValue();
        List<Stop> stops = triageGradient.getStops();

        // Update the fill color of the circle based on the selected value
        switch (selectedColor) {
            case "white" -> triageCircle.setFill(stops.get(5).getColor());
            case "blue" -> triageCircle.setFill(stops.get(4).getColor());
            case "green" -> triageCircle.setFill(stops.get(3).getColor());
            case "orange" -> triageCircle.setFill(stops.get(2).getColor());
            case "red" -> triageCircle.setFill(stops.get(1).getColor());
        }
    }



    @FXML
    private void onAddMedication(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-medication.fxml"));
            Parent root = fxmlLoader.load();

            AddMedication addMedicationController = fxmlLoader.getController();
            addMedicationController.loadPatientInformation(getPatientName(), getPatientID());

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Add Medication");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSearchButtonClicked() {
        String id = idField.getText();
        String url = "jdbc:mysql://localhost:3306/hospital_users";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT * FROM patient_information WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("full_name");
                String diagnosis = resultSet.getString("diagnosis");
                String age = resultSet.getString("age");
                String gender = resultSet.getString("gender");
                String allergies = resultSet.getString("allergies");
                String admissionDate = resultSet.getString("admission_date");
                String triage = resultSet.getString("triage");
                String treatment = resultSet.getString("treatment");


                nameField.setText(name);
                diagnosisField.setText(diagnosis);
                ageField.setText(age);
                genderField.setText(gender);
                allergiesField.setText(allergies);
                admissionDateField.setText(admissionDate);
                triageComboBox.setValue(triage);
                treatmentField.setText(treatment);

                admitButton.setDisable(false);
                addMedication.setDisable(false);
                waitingRoomButton.setDisable(false);
                nameField.setEditable(true);
                diagnosisField.setEditable(true);
                ageField.setEditable(true);
                genderField.setEditable(true);
                allergiesField.setEditable(true);
                admissionDateField.setEditable(true);
                triageComboBox.setEditable(true);
                treatmentField.setEditable(true);
                dischargeButton.setDisable(false);
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Patient Not Found");
                alert.setHeaderText(null);
                alert.setContentText("Patient with ID " + id + " not found.");

                ButtonType createNewPatientButton = new ButtonType("Create New Patient");
                ButtonType cancelButton = new ButtonType("Cancel");
                alert.getButtonTypes().setAll(createNewPatientButton, cancelButton);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == createNewPatientButton) {
                    idField.setEditable(false);
                    idField.setText(generateNewPatientID());

                    isNewPatient = true;
                    nameField.setEditable(true);
                    diagnosisField.setEditable(true);
                    ageField.setEditable(true);
                    genderField.setEditable(true);
                    allergiesField.setEditable(true);
                    admissionDateField.setEditable(true);
                    triageComboBox.setEditable(true);
                    treatmentField.setEditable(true);
                    admitButton.setDisable(false);
                    dischargeButton.setDisable(false);
                    addMedication.setDisable(false);
                    waitingRoomButton.setDisable(false);
                } else {
                    clearFields();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String generateNewPatientID() {
        String url = "jdbc:mysql://localhost:3306/hospital_users";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT id FROM patient_information ORDER BY id DESC LIMIT 1";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                int lastID = resultSet.getInt("id");
                return String.valueOf(lastID + 1);
            } else {
                return "1";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    @FXML
    private void onAdmitButtonClicked() {
        if (!isNewPatient) {
            Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmation");
            confirmDialog.setHeaderText("Create New Patient?");
            confirmDialog.setContentText("Are you sure you want to create a new patient?");

            ButtonType confirmButton = new ButtonType("Create New Patient");
            ButtonType cancelButton = new ButtonType("Cancel");

            confirmDialog.getButtonTypes().setAll(confirmButton, cancelButton);

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == confirmButton) {
                isNewPatient = true;
                nameField.setEditable(true);
                diagnosisField.setEditable(true);
                ageField.setEditable(true);
                genderField.setEditable(true);
                allergiesField.setEditable(true);
                dischargeButton.setDisable(false);
            } else {
                return;
            }
        }

        String id = idField.getText();
        String name = nameField.getText();
        String diagnosis = diagnosisField.getText();
        String age = ageField.getText();
        String gender = genderField.getText();
        String allergies = allergiesField.getText();
        String triage = triageComboBox.getValue();
        String treatment = treatmentField.getText();

        insertPatientData(id, name, diagnosis, age, gender, allergies, triage, treatment);
    }

    private void insertPatientData(String id, String name, String diagnosis, String age, String gender, String allergies, String triage, String treatment) {
        String url = "jdbc:mysql://localhost:3306/hospital_users";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "INSERT INTO patient_information (id, full_name, diagnosis, age, gender, allergies, triage, treatment) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            statement.setString(2, name);
            statement.setString(3, diagnosis);
            statement.setString(4, age);
            statement.setString(5, gender);
            statement.setString(6, allergies);
            statement.setString(7, triage);
            statement.setString(8, treatment);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Patient data inserted successfully.");
            } else {
                System.out.println("Failed to insert patient data.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDischargeButtonClicked() {
        clearFields();
        dischargeButton.setDisable(true);
    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        diagnosisField.clear();
        ageField.clear();
        genderField.clear();
        allergiesField.clear();
        admissionDateField.clear();
        triageComboBox.setValue(null);
        treatmentField.clear();
    }


    public String getPatientName() {
        return nameField.getText();
    }

    public String getPatientID() {
        return idField.getText();
    }
}
