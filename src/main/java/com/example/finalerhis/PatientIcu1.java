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

public class PatientIcu1 {
    public TextField genderField5;
    public TextField diagnosisField5;
    public TextField ageField5;
    public TextField idField5;
    public TextField nameField5;
    public TextField admissionDateField5;
    public Button admitButton5;
    public Button addMedication5;
    public Button searchButton5;
    public Button dischargeButton5;
    public Button waitingRoomButton5;
    public TextArea treatmentField5;
    public TextField timeTextField5;
    public Tab laboratoryTab5;
    public ListView<String> laboratoryTests5;
    public Button addLab5;
    public Tab xrayTab5;
    public TextField allergiesField5;
    public Circle triageCircle5;
    public RadialGradient triageGradient5;
    public ComboBox<String> triageComboBox5;
    public ListView<String> xrayOrders5;
    public int roomNumber = 5;
    MainPage mainPageController;

    String position = UserSession.getPosition();

    String url = "jdbc:mysql://localhost:3306/hospital_users";
    String username = "root";
    String password = "";
    public boolean isNewPatient = false;

    @FXML
    private void initialize() {
        idField5.setEditable(true);
        nameField5.setEditable(true);
        diagnosisField5.setEditable(false);
        ageField5.setEditable(false);
        genderField5.setEditable(false);
        allergiesField5.setEditable(false);
        admitButton5.setDisable(true);
        addMedication5.setDisable(true);
        waitingRoomButton5.setDisable(true);
        admitButton5.setDisable(true);
        dischargeButton5.setDisable(true);

        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        timeTextField5.setText(currentTime);

        triageComboBox5.setOnAction(this::onTriageComboBoxAction);
        populateLabs();
        populateXRayOrders();
        if (position.equals("RN")) {
            // Disable the laboratory and X-ray tabs
            laboratoryTab5.setDisable(true);
            xrayTab5.setDisable(true);
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

            xrayOrders5.setItems(orders);
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
            laboratoryTests5.setItems(testNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onTriageComboBoxAction(ActionEvent event) {
        String selectedColor = triageComboBox5.getValue();
        List<Stop> stops = triageGradient5.getStops();

        // Update the fill color of the circle based on the selected value
        switch (selectedColor) {
            case "white" -> triageCircle5.setFill(stops.get(5).getColor());
            case "blue" -> triageCircle5.setFill(stops.get(4).getColor());
            case "green" -> triageCircle5.setFill(stops.get(3).getColor());
            case "orange" -> triageCircle5.setFill(stops.get(2).getColor());
            case "red" -> triageCircle5.setFill(stops.get(1).getColor());
        }
    }

    @FXML
    void onAddMedication5(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-medication.fxml"));
            Parent root = fxmlLoader.load();

            AddMedication addMedicationController5 = fxmlLoader.getController();
            addMedicationController5.setPatientInformationController5(this);

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
    public void updateTreatment(String newTreatment) {
        // Update the treatment field with the new treatment value
        treatmentField5.setText(newTreatment);
    }
    @FXML
    private void onSearchButtonClicked5() {
        diagnosisField5.clear();
        ageField5.clear();
        genderField5.clear();
        allergiesField5.clear();
        admissionDateField5.clear();
        triageComboBox5.setValue("white");
        treatmentField5.clear();

        String id = idField5.getText();
        String name = nameField5.getText();


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
                idField5.setText(id);
                nameField5.setText(resultSet.getString("full_name"));
                diagnosisField5.setText(diagnosis);
                ageField5.setText(age);
                genderField5.setText(gender);
                allergiesField5.setText(allergies);
                admissionDateField5.setText(admission_date);
                triageComboBox5.setValue(triage);
                treatmentField5.setText(treatment);

                // Enable/disable appropriate buttons and fields
                admitButton5.setDisable(false);
                addMedication5.setDisable(false);
                waitingRoomButton5.setDisable(false);
                nameField5.setEditable(false);
                diagnosisField5.setEditable(true);
                ageField5.setEditable(true);
                genderField5.setEditable(true);
                allergiesField5.setEditable(true);
                admissionDateField5.setEditable(true);
                triageComboBox5.setEditable(true);
                treatmentField5.setEditable(true);
                dischargeButton5.setDisable(true);
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
                    idField5.setEditable(false);
                    idField5.setText(generateNewPatientID());

                    nameField5.setEditable(true);
                    diagnosisField5.setEditable(true);
                    ageField5.setEditable(true);
                    genderField5.setEditable(true);
                    allergiesField5.setEditable(true);
                    triageComboBox5.setEditable(true);
                    treatmentField5.setEditable(true);
                    admissionDateField5.setEditable(true);
                    admitButton5.setDisable(false);
                    dischargeButton5.setDisable(false);
                    addMedication5.setDisable(false);
                    waitingRoomButton5.setDisable(false);
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

    public void fillFieldsWithPatientData5(String patientID) {
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


                nameField5.setText(name);
                diagnosisField5.setText(diagnosis);
                ageField5.setText(age);
                genderField5.setText(gender);
                allergiesField5.setText(allergies);
                admissionDateField5.setText(admission_date);
                triageComboBox5.setValue(triage);
                treatmentField5.setText(treatment);
                idField5.setText(id);

                admitButton5.setDisable(false);
                addMedication5.setDisable(false);
                waitingRoomButton5.setDisable(false);
                nameField5.setEditable(true);
                diagnosisField5.setEditable(true);
                ageField5.setEditable(true);
                genderField5.setEditable(true);
                allergiesField5.setEditable(true);
                admissionDateField5.setEditable(true);
                triageComboBox5.setEditable(true);
                treatmentField5.setEditable(true);
                dischargeButton5.setDisable(false);
                admitButton5.setDisable(true);
            } else {
                clearFields();
                showAlert(Alert.AlertType.ERROR, "Patient Not Found", "Patient with ID " + patientID + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onAdmitButtonClicked5() {
        String id = idField5.getText();
        String name = nameField5.getText();
        String diagnosis = diagnosisField5.getText();
        String age = ageField5.getText();
        String gender = genderField5.getText();
        String allergies = allergiesField5.getText();
        String triage = triageComboBox5.getValue();
        String treatment = treatmentField5.getText();
        String time = timeTextField5.getText();
        LocalDate admission_date = LocalDate.now();

        if (isNewPatient) {
            insertPatientData(id, name, diagnosis, age, gender, allergies, triage, treatment, time, admission_date);
        } else {
            updatePatientData(id, name, diagnosis, age, gender, allergies, triage, treatment, time, admission_date);
        }
        idField5.setEditable(false);
        dischargeButton5.setDisable(false);
        nameField5.setEditable(false);
        searchButton5.setDisable(true);
        waitingRoomButton5.setDisable(true);
        admitButton5.setDisable(true);
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
    void onDischargeButtonClicked5() {
        clearFields();
        dischargeButton5.setDisable(true);
        addMedication5.setDisable(true);
        admitButton5.setDisable(true);
        waitingRoomButton5.setDisable(true);
        updateRoomPatientId(roomNumber);

        idField5.setEditable(true);
        nameField5.setEditable(true);
        diagnosisField5.setEditable(false);
        ageField5.setEditable(false);
        genderField5.setEditable(false);
        allergiesField5.setEditable(false);
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


    private void clearFields() {
        idField5.clear();
        nameField5.clear();
        diagnosisField5.clear();
        ageField5.clear();
        genderField5.clear();
        allergiesField5.clear();
        admissionDateField5.clear();
        triageComboBox5.setValue("white");
        treatmentField5.clear();
    }

    public String getPatientName() {
        return nameField5.getText();
    }

    public String getPatientID() {
        return idField5.getText();
    }


}
