package com.example.finalerhis;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class PatientRegular2 {
    public TextField genderField2;
    public TextField diagnosisField2;
    public TextField ageField2;
    public TextField idField2;
    public TextField nameField2;
    public TextField admissionDateField2;
    public Button admitButton2;
    public Button addMedication2;
    public Button searchButton2;
    public Button dischargeButton2;
    public Button waitingRoomButton2;
    public TextArea treatmentField2;
    public TextField timeTextField2;
    public TextField allergiesField2;
    public Circle triageCircle;
    public RadialGradient triageGradient;
    public ComboBox<String> triageComboBox2;

    int roomNumber = 2;
    boolean isNewPatient = false;
    PatientRegular1 patientRegular1Controller;
    String url = "jdbc:mysql://localhost:3306/hospital_users";
    String username = "root";
    String password = "";


    @FXML
    private void initialize(){
        idField2.setEditable(true);
        nameField2.setEditable(false);
        diagnosisField2.setEditable(false);
        ageField2.setEditable(false);
        genderField2.setEditable(false);
        allergiesField2.setEditable(false);
        admitButton2.setDisable(true);
        addMedication2.setDisable(true);
        waitingRoomButton2.setDisable(true);
        admitButton2.setDisable(true);
        dischargeButton2.setDisable(true);

        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        timeTextField2.setText(currentTime);

        triageComboBox2.setOnAction(this::onTriageComboBoxAction);
    }

    private void onTriageComboBoxAction(Event event) {
        String selectedColor = triageComboBox2.getValue();
        List<Stop> stops = triageGradient.getStops();

        switch (selectedColor) {
            case "white" -> triageCircle.setFill(stops.get(5).getColor());
            case "blue" -> triageCircle.setFill(stops.get(4).getColor());
            case "green" -> triageCircle.setFill(stops.get(3).getColor());
            case "orange" -> triageCircle.setFill(stops.get(2).getColor());
            case "red" -> triageCircle.setFill(stops.get(1).getColor());
        }
    }
    @FXML
    void onAddMedication2(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-medication.fxml"));
            Parent root = fxmlLoader.load();
            AddMedication addMedicationController2 = fxmlLoader.getController();
            addMedicationController2.setPatientInformationController2(this);

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
    private void onSearchButtonClicked2() {
        String id = idField2.getText();
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
                String admission_date = resultSet.getString("admission_date");
                String triage = resultSet.getString("triage");
                String treatment = resultSet.getString("treatment");


                nameField2.setText(name);
                diagnosisField2.setText(diagnosis);
                ageField2.setText(age);
                genderField2.setText(gender);
                allergiesField2.setText(allergies);
                admissionDateField2.setText(admission_date);
                triageComboBox2.setValue(triage);
                treatmentField2.setText(treatment);

                admitButton2.setDisable(false);
                addMedication2.setDisable(false);
                waitingRoomButton2.setDisable(false);
                nameField2.setEditable(true);
                diagnosisField2.setEditable(true);
                ageField2.setEditable(true);
                genderField2.setEditable(true);
                allergiesField2.setEditable(true);
                admissionDateField2.setEditable(true);
                triageComboBox2.setEditable(true);
                treatmentField2.setEditable(true);
                dischargeButton2.setDisable(true);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Patient Not Found");
                alert.setHeaderText(null);
                alert.setContentText("Patient with ID " + id + " not found.");

                ButtonType createNewPatientButton = new ButtonType("Create New Patient");
                ButtonType cancelButton = new ButtonType("Cancel");
                alert.getButtonTypes().setAll(createNewPatientButton, cancelButton);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == createNewPatientButton) {
                    idField2.setEditable(false);
                    idField2.setText(patientRegular1Controller.getPatientID());

                    nameField2.setEditable(true);
                    diagnosisField2.setEditable(true);
                    ageField2.setEditable(true);
                    genderField2.setEditable(true);
                    allergiesField2.setEditable(true);
                    triageComboBox2.setEditable(true);
                    treatmentField2.setEditable(true);
                    admissionDateField2.setEditable(true);
                    admitButton2.setDisable(false);
                    dischargeButton2.setDisable(false);
                    addMedication2.setDisable(false);
                    waitingRoomButton2.setDisable(false);
                    isNewPatient = true;
                } else {
                    clearFields();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void onAdmitButtonClicked2(ActionEvent event) {
        String id = idField2.getText();
        String name = nameField2.getText();
        String diagnosis = diagnosisField2.getText();
        String age = ageField2.getText();
        String gender = genderField2.getText();
        String allergies = allergiesField2.getText();
        String triage = triageComboBox2.getValue();
        String treatment = treatmentField2.getText();
        String time = timeTextField2.getText();
        LocalDate admission_date = LocalDate.now();
        updateRoomsTable(roomNumber,id);

        if (isNewPatient) {
            patientRegular1Controller.insertPatientData(id, name, diagnosis, age, gender, allergies, triage, treatment, time, admission_date);
        } else {
            patientRegular1Controller.updatePatientData(id, name, diagnosis, age, gender, allergies, triage, treatment, time, admission_date);
        }
        idField2.setEditable(false);
        dischargeButton2.setDisable(false);
    }
    void updateRoomsTable(int roomNumber, String patientID) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sqlCheckRoom = "SELECT COUNT(*) FROM rooms WHERE room_number = ?";
            PreparedStatement checkRoomStatement = connection.prepareStatement(sqlCheckRoom);
            checkRoomStatement.setInt(1, roomNumber);
            ResultSet resultSet = checkRoomStatement.executeQuery();
            resultSet.next();
            int roomCount = resultSet.getInt(1);

            if (roomCount > 0) {
                String sqlUpdateRoom = "UPDATE rooms SET patient_id = ? WHERE room_number = ?";
                PreparedStatement updateStatement = connection.prepareStatement(sqlUpdateRoom);
                updateStatement.setString(1, patientID);
                updateStatement.setInt(2, roomNumber);
                int rowsAffected = updateStatement.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Admission Success", "Patient admitted successfully.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Admission Failure", "Failed to admit patient.");
                }
            } else {
                String sqlCreateRoom = "INSERT INTO rooms (room_number, patient_id) VALUES (?, ?)";
                PreparedStatement createStatement = connection.prepareStatement(sqlCreateRoom);
                createStatement.setInt(1, roomNumber);
                createStatement.setString(2, patientID);
                int rowsAffected = createStatement.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Room Creation Success", "Room created and patient admitted successfully.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Room Creation Failure", "Failed to create room and admit patient.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @FXML
    void fillFieldsWithPatientData2(String patientID) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT * FROM patient_information WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, patientID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("full_name");
                String diagnosis = resultSet.getString("diagnosis");
                String age = resultSet.getString("age");
                String gender = resultSet.getString("gender");
                String allergies = resultSet.getString("allergies");
                String admission_date = resultSet.getString("admission_date");
                String triage = resultSet.getString("triage");
                String treatment = resultSet.getString("treatment");
                String id = resultSet.getString("id");


                nameField2.setText(name);
                diagnosisField2.setText(diagnosis);
                ageField2.setText(age);
                genderField2.setText(gender);
                allergiesField2.setText(allergies);
                admissionDateField2.setText(admission_date);
                triageComboBox2.setValue(triage);
                treatmentField2.setText(treatment);
                idField2.setText(id);

                admitButton2.setDisable(false);
                addMedication2.setDisable(false);
                waitingRoomButton2.setDisable(false);
                nameField2.setEditable(true);
                diagnosisField2.setEditable(true);
                ageField2.setEditable(true);
                genderField2.setEditable(true);
                allergiesField2.setEditable(true);
                admissionDateField2.setEditable(true);
                triageComboBox2.setEditable(true);
                treatmentField2.setEditable(true);
                dischargeButton2.setDisable(false);
                admitButton2.setDisable(true);
            } else {
                clearFields();
                showAlert(Alert.AlertType.ERROR, "Patient Not Found", "Patient with ID " + patientID + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onDischargeButtonClicked2(ActionEvent event) {
        clearFields();
        dischargeButton2.setDisable(true);
        patientRegular1Controller.updateRoomPatientId(roomNumber);

        idField2.setEditable(false);
        nameField2.setEditable(false);
        diagnosisField2.setEditable(false);
        ageField2.setEditable(false);
        genderField2.setEditable(false);
        allergiesField2.setEditable(false);
    }

    private void clearFields() {
        idField2.clear();
        nameField2.clear();
        diagnosisField2.clear();
        ageField2.clear();
        genderField2.clear();
        allergiesField2.clear();
        admissionDateField2.clear();
        triageComboBox2.setValue(null);
        treatmentField2.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void displayEmptyRoomMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Room Empty");
        alert.setHeaderText(null);
        alert.setContentText("The room is empty.");

        alert.showAndWait();
    }
    public void updateTreatment(String newTreatment) {
        // Update the treatment field with the new treatment value
        treatmentField2.setText(newTreatment);
    }
    public String getPatientName() {
        return nameField2.getText();
    }

    public String getPatientID() {
        return idField2.getText();
    }
}
