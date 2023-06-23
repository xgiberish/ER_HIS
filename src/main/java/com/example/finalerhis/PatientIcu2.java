package com.example.finalerhis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

public class PatientIcu2 {
    public TextField genderField6;
    public TextField diagnosisField6;
    public TextField ageField6;
    public TextField idField6;
    public TextField nameField6;
    public TextField admissionDateField6;
    public Button admitButton6;
    public Button addMedication6;
    public Button searchButton6;
    public Button dischargeButton6;
    public Button waitingRoomButton6;
    public TextArea treatmentField6;
    public TextField timeTextField6;
    public Tab laboratoryTab6;
    public ListView<String> laboratoryTests6;
    public Button addLab6;
    public Tab xrayTab6;
    public ListView<String> xrayOrders6;
    public TextField allergiesField6;
    public Circle triageCircle6;
    public RadialGradient triageGradient6;
    public ComboBox<String> triageComboBox6;
    String position = UserSession.getPosition();
    public int roomNumber = 6;


    String url = "jdbc:mysql://localhost:3306/hospital_users";
    String username = "root";
    String password = "";
    public boolean isNewPatient = false;
    MainPage mainPageController;

    @FXML
    private void initialize() {
        idField6.setEditable(true);
        nameField6.setEditable(true);
        diagnosisField6.setEditable(false);
        ageField6.setEditable(false);
        genderField6.setEditable(false);
        allergiesField6.setEditable(false);
        admitButton6.setDisable(true);
        addMedication6.setDisable(true);
        waitingRoomButton6.setDisable(true);
        admitButton6.setDisable(true);
        dischargeButton6.setDisable(true);

        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        timeTextField6.setText(currentTime);

        triageComboBox6.setOnAction(this::onTriageComboBoxAction);
        populateLabs();
        populateXRayOrders();
        if (position.equals("RN")) {
            // Disable the laboratory and X-ray tabs
            laboratoryTab6.setDisable(true);
            xrayTab6.setDisable(true);
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

            xrayOrders6.setItems(orders);
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
            laboratoryTests6.setItems(testNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onTriageComboBoxAction(ActionEvent event) {
        String selectedColor = triageComboBox6.getValue();
        List<Stop> stops = triageGradient6.getStops();

        // Update the fill color of the circle based on the selected value
        switch (selectedColor) {
            case "white" -> triageCircle6.setFill(stops.get(5).getColor());
            case "blue" -> triageCircle6.setFill(stops.get(4).getColor());
            case "green" -> triageCircle6.setFill(stops.get(3).getColor());
            case "orange" -> triageCircle6.setFill(stops.get(2).getColor());
            case "red" -> triageCircle6.setFill(stops.get(1).getColor());
        }
    }

    @FXML
    void onAddMedication6(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-medication.fxml"));
            Parent root = fxmlLoader.load();

            AddMedication addMedicationController6 = fxmlLoader.getController();
            addMedicationController6.setPatientInformationController6(this);

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
    private void onSearchButtonClicked6() {
        diagnosisField6.clear();
        ageField6.clear();
        genderField6.clear();
        allergiesField6.clear();
        admissionDateField6.clear();
        triageComboBox6.setValue("white");
        treatmentField6.clear();

        String id = idField6.getText();
        String name = nameField6.getText();


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
                idField6.setText(id);
                nameField6.setText(resultSet.getString("full_name"));
                diagnosisField6.setText(diagnosis);
                ageField6.setText(age);
                genderField6.setText(gender);
                allergiesField6.setText(allergies);
                admissionDateField6.setText(admission_date);
                triageComboBox6.setValue(triage);
                treatmentField6.setText(treatment);

                // Enable/disable appropriate buttons and fields
                admitButton6.setDisable(false);
                addMedication6.setDisable(false);
                waitingRoomButton6.setDisable(false);
                nameField6.setEditable(false);
                diagnosisField6.setEditable(true);
                ageField6.setEditable(true);
                genderField6.setEditable(true);
                allergiesField6.setEditable(true);
                admissionDateField6.setEditable(true);
                triageComboBox6.setEditable(true);
                treatmentField6.setEditable(true);
                dischargeButton6.setDisable(true);
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
                    idField6.setEditable(false);
                    idField6.setText(generateNewPatientID());

                    nameField6.setEditable(true);
                    diagnosisField6.setEditable(true);
                    ageField6.setEditable(true);
                    genderField6.setEditable(true);
                    allergiesField6.setEditable(true);
                    triageComboBox6.setEditable(true);
                    treatmentField6.setEditable(true);
                    admissionDateField6.setEditable(true);
                    admitButton6.setDisable(false);
                    dischargeButton6.setDisable(false);
                    addMedication6.setDisable(false);
                    waitingRoomButton6.setDisable(false);
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
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Patient admitted successfully.");
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
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
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Patient information updated successfully.");
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to update patient information.");
            }
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void displayEmptyRoomMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Room Empty");
        alert.setHeaderText(null);
        alert.setContentText("The room is empty.");

        alert.showAndWait();
    }

    public void fillFieldsWithPatientData6(String patientID) {
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


                nameField6.setText(name);
                diagnosisField6.setText(diagnosis);
                ageField6.setText(age);
                genderField6.setText(gender);
                allergiesField6.setText(allergies);
                admissionDateField6.setText(admission_date);
                triageComboBox6.setValue(triage);
                treatmentField6.setText(treatment);
                idField6.setText(id);

                admitButton6.setDisable(false);
                addMedication6.setDisable(false);
                waitingRoomButton6.setDisable(false);
                nameField6.setEditable(true);
                diagnosisField6.setEditable(true);
                ageField6.setEditable(true);
                genderField6.setEditable(true);
                allergiesField6.setEditable(true);
                admissionDateField6.setEditable(true);
                triageComboBox6.setEditable(true);
                treatmentField6.setEditable(true);
                dischargeButton6.setDisable(false);
                admitButton6.setDisable(true);
            } else {
                clearFields();
                showAlert(Alert.AlertType.ERROR, "Patient Not Found", "Patient with ID " + patientID + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onAdmitButtonClicked6() {
        String id = idField6.getText();
        String name = nameField6.getText();
        String diagnosis = diagnosisField6.getText();
        String age = ageField6.getText();
        String gender = genderField6.getText();
        String allergies = allergiesField6.getText();
        String triage = triageComboBox6.getValue();
        String treatment = treatmentField6.getText();
        String time = timeTextField6.getText();
        LocalDate admission_date = LocalDate.now();

        if (isNewPatient) {
            insertPatientData(id, name, diagnosis, age, gender, allergies, triage, treatment, time, admission_date);
        } else {
            updatePatientData(id, name, diagnosis, age, gender, allergies, triage, treatment, time, admission_date);
        }
        idField6.setEditable(false);
        dischargeButton6.setDisable(false);
        nameField6.setEditable(false);
        searchButton6.setDisable(true);
        waitingRoomButton6.setDisable(true);
        admitButton6.setDisable(true);
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void onDischargeButtonClicked6() {
        clearFields();
        dischargeButton6.setDisable(true);
        addMedication6.setDisable(true);
        admitButton6.setDisable(true);
        waitingRoomButton6.setDisable(true);
        updateRoomPatientId(roomNumber);

        idField6.setEditable(true);
        nameField6.setEditable(true);
        diagnosisField6.setEditable(false);
        ageField6.setEditable(false);
        genderField6.setEditable(false);
        allergiesField6.setEditable(false);
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
        treatmentField6.setText(newTreatment);
    }

    private void clearFields() {
        idField6.clear();
        nameField6.clear();
        diagnosisField6.clear();
        ageField6.clear();
        genderField6.clear();
        allergiesField6.clear();
        admissionDateField6.clear();
        triageComboBox6.setValue("white");
        treatmentField6.clear();
    }

    public String getPatientName() {
        return nameField6.getText();
    }

    public String getPatientID() {
        return idField6.getText();
    }




}
