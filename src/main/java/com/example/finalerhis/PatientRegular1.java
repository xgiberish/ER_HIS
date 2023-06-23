package com.example.finalerhis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    public ListView<String> laboratoryTests;
    public Button addLab;

    public ListView<String> xrayOrders;
    public Tab xrayTab;
    public Tab laboratoryTab;
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

    int roomNumber = 1;
    MainPage mainPageController;

    boolean isNewPatient = false;
    String position = UserSession.getPosition();


    public PatientRegular1() {
    }


    @FXML
    private void initialize() {
        idField.setEditable(true);
        nameField.setEditable(true);
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
        populateLabs();
        populateXRayOrders();
        if (position.equals("RN")) {
            // Disable the laboratory and X-ray tabs
            laboratoryTab.setDisable(true);
            xrayTab.setDisable(true);
        }

    }
    private void populateXRayOrders() {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT id, order_name FROM x_ray";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            ObservableList<String> orders = FXCollections.observableArrayList();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String orderName = resultSet.getString("order_name");
                String order = id + ": " + orderName;
                orders.add(order);
            }

            xrayOrders.setItems(orders);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void populateLabs(){

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Create a query to retrieve the id and test_name from the blood_tests table
            String sql = "SELECT id, test_name FROM blood_test";

            // Prepare the statement
            PreparedStatement statement = connection.prepareStatement(sql);

            // Execute the query and retrieve the result set
            ResultSet resultSet = statement.executeQuery();

            // Create an ObservableList to store the data for the ListView
            ObservableList<String> testNames = FXCollections.observableArrayList();

            // Iterate through the result set and add id and test_name values to the ObservableList
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String testName = resultSet.getString("test_name");
                String listItem = id + " - " + testName;
                testNames.add(listItem);
            }

            // Set the items of the ListView to the ObservableList
            laboratoryTests.setItems(testNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    void onAddMedication(ActionEvent event) {
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
        diagnosisField.clear();
        ageField.clear();
        genderField.clear();
        allergiesField.clear();
        admissionDateField.clear();
        triageComboBox.setValue("white");
        treatmentField.clear();

        String id = idField.getText();
        String name = nameField.getText();


        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT * FROM patient_information WHERE id = ? OR full_name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            statement.setString(2, name);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Existing patient found, retrieve values from the result set
                id = resultSet.getString("id");
                String diagnosis = resultSet.getString("diagnosis");
                String age = resultSet.getString("age");
                String gender = resultSet.getString("gender");
                String allergies = resultSet.getString("allergies");
                String admission_date = resultSet.getString("admission_date");
                String triage = resultSet.getString("triage");
                String treatment = resultSet.getString("treatment");

                // Set the values in the respective text fields
                idField.setText(id);
                nameField.setText(resultSet.getString("full_name"));
                diagnosisField.setText(diagnosis);
                ageField.setText(age);
                genderField.setText(gender);
                allergiesField.setText(allergies);
                admissionDateField.setText(admission_date);
                triageComboBox.setValue(triage);
                treatmentField.setText(treatment);

                // Enable/disable appropriate buttons and fields
                admitButton.setDisable(false);
                addMedication.setDisable(false);
                waitingRoomButton.setDisable(false);
                nameField.setEditable(false);
                diagnosisField.setEditable(true);
                ageField.setEditable(true);
                genderField.setEditable(true);
                allergiesField.setEditable(true);
                admissionDateField.setEditable(true);
                triageComboBox.setEditable(true);
                treatmentField.setEditable(true);
                dischargeButton.setDisable(true);
            } else {
                // Patient not found, offer to create a new patient
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Patient Not Found");
                alert.setHeaderText(null);
                alert.setContentText("Patient with ID " + id + " or name " + name + " not found.");

                ButtonType createNewPatientButton = new ButtonType("Create New Patient");
                ButtonType cancelButton = new ButtonType("Cancel");
                alert.getButtonTypes().setAll(createNewPatientButton, cancelButton);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == createNewPatientButton) {
                    // Clear the fields and set them for creating a new patient
                    clearFields();
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
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public String generateNewPatientID() {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT MAX(id) AS max_id FROM patient_information";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                int lastID = resultSet.getInt("max_id");
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
    void insertPatientData(String id, String name, String diagnosis, String age, String gender, String allergies, String triage, String treatment, String time, LocalDate admission_date) {
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
    void updatePatientData(String id, String name, String diagnosis, String age, String gender, String allergies, String triage, String treatment, String time, LocalDate admission_date) {
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


        if (isNewPatient) {
            insertPatientData(id, name, diagnosis, age, gender, allergies, triage, treatment, time, admission_date);
        } else {
            updatePatientData(id, name, diagnosis, age, gender, allergies, triage, treatment, time, admission_date);
        }
        idField.setEditable(false);
        dischargeButton.setDisable(false);
        nameField.setEditable(false);
        searchButton.setDisable(true);
        waitingRoomButton.setDisable(true);
        admitButton.setDisable(true);
        updateRoomsTable(roomNumber, id);
        mainPageController.fetchRoomDataFromDatabase();

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
    void onDischargeButtonClicked() {
        clearFields();
        dischargeButton.setDisable(true);
        addMedication.setDisable(true);
        admitButton.setDisable(true);
        waitingRoomButton.setDisable(true);
        updateRoomPatientId(roomNumber);

        idField.setEditable(true);
        nameField.setEditable(true);
        diagnosisField.setEditable(false);
        ageField.setEditable(false);
        genderField.setEditable(false);
        allergiesField.setEditable(false);
    }

    void updateRoomPatientId(int roomNumber) {
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



    private void clearFields() {
        idField.clear();
        nameField.clear();
        diagnosisField.clear();
        ageField.clear();
        genderField.clear();
        allergiesField.clear();
        admissionDateField.clear();
        triageComboBox.setValue("white");
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

