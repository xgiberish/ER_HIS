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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class PatientRegular1 {
    public TextField timeTextField;
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

    String url = "jdbc:mysql://localhost:3306/hospital_users";
    String username = "root";
    String password = "";

    private Stage stage;
    int roomNumber = 1;

    boolean isNewPatient = false;

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

        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        timeTextField.setText(currentTime);

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

            AddMedication addMedicationController2 = fxmlLoader.getController();
            addMedicationController2.setPatientInformationController(this);

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


                nameField.setText(name);
                diagnosisField.setText(diagnosis);
                ageField.setText(age);
                genderField.setText(gender);
                allergiesField.setText(allergies);
                admissionDateField.setText(admission_date);
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
                dischargeButton.setDisable(true);
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

                    nameField.setEditable(true);
                    diagnosisField.setEditable(true);
                    ageField.setEditable(true);
                    genderField.setEditable(true);
                    allergiesField.setEditable(true);
                    triageComboBox.setEditable(true);
                    treatmentField.setEditable(true);
                    admissionDateField.setEditable(true);
                    admitButton.setDisable(false);
                    dischargeButton.setDisable(false);
                    addMedication.setDisable(false);
                    waitingRoomButton.setDisable(false);
                    isNewPatient = true;
                } else {
                    clearFields();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String generateNewPatientID() {
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
    private void insertPatientData(String id, String name, String diagnosis, String age, String gender, String allergies, String triage, String treatment, String time, LocalDate admission_date) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "INSERT INTO patient_information (id, full_name, age, gender, allergies, time, triage, diagnosis, treatment, admission_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            statement.setString(2, name);
            statement.setString(3, age);
            statement.setString(4, gender);
            statement.setString(5, allergies);
            statement.setString(6, time);
            statement.setString(7, triage);
            statement.setString(8, diagnosis);
            statement.setString(9, treatment);
            statement.setDate(10, Date.valueOf(admission_date));

            int rowsAffected = statement.executeUpdate();

            Alert alert;
            if (rowsAffected > 0) {
                alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Patient admitted successfully.");
            } else {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to admit patient.");
            }
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updatePatientData(String id, String name, String diagnosis, String age, String gender, String allergies, String triage, String treatment, String time, LocalDate admission_date) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "UPDATE patient_information SET full_name = ?, age = ?, gender = ?, allergies = ?, time = ?, triage = ?, diagnosis = ?, treatment = ?, admission_date = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, age);
            statement.setString(3, gender);
            statement.setString(4, allergies);
            statement.setString(5, time);
            statement.setString(6, triage);
            statement.setString(7, diagnosis);
            statement.setString(8, treatment);
            statement.setDate(9, Date.valueOf(admission_date));
            statement.setString(10, id);

            int rowsAffected = statement.executeUpdate();

            Alert alert;
            if (rowsAffected > 0) {
                alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Patient information updated successfully.");
            } else {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to update patient information.");
            }
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onAdmitButtonClicked() {
        String id = idField.getText();
        String name = nameField.getText();
        String diagnosis = diagnosisField.getText();
        String age = ageField.getText();
        String gender = genderField.getText();
        String allergies = allergiesField.getText();
        String triage = triageComboBox.getValue();
        String treatment = treatmentField.getText();
        String time = timeTextField.getText();
        LocalDate admission_date = LocalDate.now();
        updateRoomsTable(roomNumber, id);

        if (isNewPatient) {
            insertPatientData(id, name, diagnosis, age, gender, allergies, triage, treatment, time, admission_date);
        } else {
            updatePatientData(id, name, diagnosis, age, gender, allergies, triage, treatment, time, admission_date);
        }
        idField.setEditable(false);
        dischargeButton.setDisable(false);

    }
    private void updateRoomsTable(int roomNumber, String patientID) {
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
    private void onDischargeButtonClicked() {
        clearFields();
        dischargeButton.setDisable(true);
        updateRoomPatientId(1);

        idField.setEditable(false);
        nameField.setEditable(false);
        diagnosisField.setEditable(false);
        ageField.setEditable(false);
        genderField.setEditable(false);
        allergiesField.setEditable(false);
    }

    private void updateRoomPatientId(int roomNumber) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "UPDATE rooms SET patient_id = NULL WHERE room_number = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, roomNumber);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Discharge Success", "Patient discharged successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Discharge Failure", "Failed to discharge patient.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateTreatment(String newTreatment) {
        // Update the treatment field with the new treatment value
        treatmentField.setText(newTreatment);
    }


    @FXML
    void fillFieldsWithPatientData(String patientID) {
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


                nameField.setText(name);
                diagnosisField.setText(diagnosis);
                ageField.setText(age);
                genderField.setText(gender);
                allergiesField.setText(allergies);
                admissionDateField.setText(admission_date);
                triageComboBox.setValue(triage);
                treatmentField.setText(treatment);
                idField.setText(id);

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
                admitButton.setDisable(true);
            } else {
                clearFields();
                showAlert(AlertType.ERROR, "Patient Not Found", "Patient with ID " + patientID + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String fetchTreatmentFromDatabase() {
        String patientIdValue = getPatientID(); // Replace with the method to get the patient ID
        String treatment = "";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT treatment FROM user_information WHERE id = ?")) {

            statement.setString(1, patientIdValue);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                treatment = resultSet.getString("treatment");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return treatment;
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void displayEmptyRoomMessage() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Room Empty");
        alert.setHeaderText(null);
        alert.setContentText("The room is empty.");

        alert.showAndWait();
    }
}

