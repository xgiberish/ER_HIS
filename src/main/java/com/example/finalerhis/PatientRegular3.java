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

public class PatientRegular3 {
    public TextField genderField3;
    public TextField diagnosisField3;
    public TextField ageField3;
    public TextField idField3;
    public TextField nameField3;
    public TextField admissionDateField3;
    public Button admitButton3;
    public Button dischargeButton3;
    public Button addMedication3;
    public Button waitingRoomButton3;
    public TextArea treatmentField3;
    public TextField timeTextField3;
    public Tab laboratoryTab3;
    public ListView<String> laboratoryTests3;
    public Button addLab3;
    public Tab xrayTab3;
    public TextField allergiesField3;
    public Circle triageCircle3;
    public RadialGradient triageGradient3;
    public ComboBox<String> triageComboBox3;
    public ListView<String> xrayOrders3;
    public int roomNumber = 3;
    public Button searchButton3;
    MainPage mainPageController;
    String position = UserSession.getPosition();

    String url = "jdbc:mysql://localhost:3306/hospital_users";
    String username = "root";
    String password = "";
    public boolean isNewPatient = false;

    @FXML
    private void initialize() {
        idField3.setEditable(true);
        nameField3.setEditable(true);
        diagnosisField3.setEditable(false);
        ageField3.setEditable(false);
        genderField3.setEditable(false);
        allergiesField3.setEditable(false);
        admitButton3.setDisable(true);
        addMedication3.setDisable(true);
        waitingRoomButton3.setDisable(true);
        admitButton3.setDisable(true);
        dischargeButton3.setDisable(true);

        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        timeTextField3.setText(currentTime);

        triageComboBox3.setOnAction(this::onTriageComboBoxAction);
        populateLabs();
        populateXRayOrders();
        if (position.equals("RN")) {
            // Disable the laboratory and X-ray tabs
            laboratoryTab3.setDisable(true);
            xrayTab3.setDisable(true);
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

            xrayOrders3.setItems(orders);
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
            laboratoryTests3.setItems(testNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onTriageComboBoxAction(ActionEvent event) {
        String selectedColor = triageComboBox3.getValue();
        List<Stop> stops = triageGradient3.getStops();

        // Update the fill color of the circle based on the selected value
        switch (selectedColor) {
            case "white" -> triageCircle3.setFill(stops.get(5).getColor());
            case "blue" -> triageCircle3.setFill(stops.get(4).getColor());
            case "green" -> triageCircle3.setFill(stops.get(3).getColor());
            case "orange" -> triageCircle3.setFill(stops.get(2).getColor());
            case "red" -> triageCircle3.setFill(stops.get(1).getColor());
        }
    }

    @FXML
    void onAddMedication3(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-medication.fxml"));
            Parent root = fxmlLoader.load();

            AddMedication addMedicationController3 = fxmlLoader.getController();
            addMedicationController3.setPatientInformationController3(this);

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
    private void onSearchButtonClicked3() {
        diagnosisField3.clear();
        ageField3.clear();
        genderField3.clear();
        allergiesField3.clear();
        admissionDateField3.clear();
        triageComboBox3.setValue("white");
        treatmentField3.clear();

        String id = idField3.getText();
        String name = nameField3.getText();


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
                idField3.setText(id);
                nameField3.setText(resultSet.getString("full_name"));
                diagnosisField3.setText(diagnosis);
                ageField3.setText(age);
                genderField3.setText(gender);
                allergiesField3.setText(allergies);
                admissionDateField3.setText(admission_date);
                triageComboBox3.setValue(triage);
                treatmentField3.setText(treatment);

                // Enable/disable appropriate buttons and fields
                admitButton3.setDisable(false);
                addMedication3.setDisable(false);
                waitingRoomButton3.setDisable(false);
                nameField3.setEditable(false);
                diagnosisField3.setEditable(true);
                ageField3.setEditable(true);
                genderField3.setEditable(true);
                allergiesField3.setEditable(true);
                admissionDateField3.setEditable(true);
                triageComboBox3.setEditable(true);
                treatmentField3.setEditable(true);
                dischargeButton3.setDisable(true);
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
                    idField3.setEditable(false);
                    idField3.setText(generateNewPatientID());

                    nameField3.setEditable(true);
                    diagnosisField3.setEditable(true);
                    ageField3.setEditable(true);
                    genderField3.setEditable(true);
                    allergiesField3.setEditable(true);
                    triageComboBox3.setEditable(true);
                    treatmentField3.setEditable(true);
                    admissionDateField3.setEditable(true);
                    admitButton3.setDisable(false);
                    dischargeButton3.setDisable(false);
                    addMedication3.setDisable(false);
                    waitingRoomButton3.setDisable(false);
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

    public void fillFieldsWithPatientData3(String patientID) {
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


                nameField3.setText(name);
                diagnosisField3.setText(diagnosis);
                ageField3.setText(age);
                genderField3.setText(gender);
                allergiesField3.setText(allergies);
                admissionDateField3.setText(admission_date);
                triageComboBox3.setValue(triage);
                treatmentField3.setText(treatment);
                idField3.setText(id);

                admitButton3.setDisable(false);
                addMedication3.setDisable(false);
                waitingRoomButton3.setDisable(false);
                nameField3.setEditable(true);
                diagnosisField3.setEditable(true);
                ageField3.setEditable(true);
                genderField3.setEditable(true);
                allergiesField3.setEditable(true);
                admissionDateField3.setEditable(true);
                triageComboBox3.setEditable(true);
                treatmentField3.setEditable(true);
                dischargeButton3.setDisable(false);
                admitButton3.setDisable(true);
            } else {
                clearFields();
                showAlert(Alert.AlertType.ERROR, "Patient Not Found", "Patient with ID " + patientID + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onAdmitButtonClicked3() {
        String id = idField3.getText();
        String name = nameField3.getText();
        String diagnosis = diagnosisField3.getText();
        String age = ageField3.getText();
        String gender = genderField3.getText();
        String allergies = allergiesField3.getText();
        String triage = triageComboBox3.getValue();
        String treatment = treatmentField3.getText();
        String time = timeTextField3.getText();
        LocalDate admission_date = LocalDate.now();

        if (isNewPatient) {
            insertPatientData(id, name, diagnosis, age, gender, allergies, triage, treatment, time, admission_date);
        } else {
            updatePatientData(id, name, diagnosis, age, gender, allergies, triage, treatment, time, admission_date);
        }
        idField3.setEditable(false);
        dischargeButton3.setDisable(false);
        nameField3.setEditable(false);
        searchButton3.setDisable(true);
        waitingRoomButton3.setDisable(true);
        admitButton3.setDisable(true);
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
    void onDischargeButtonClicked3() {
        clearFields();
        dischargeButton3.setDisable(true);
        addMedication3.setDisable(true);
        admitButton3.setDisable(true);
        waitingRoomButton3.setDisable(true);
        updateRoomPatientId(roomNumber);

        idField3.setEditable(true);
        nameField3.setEditable(true);
        diagnosisField3.setEditable(false);
        ageField3.setEditable(false);
        genderField3.setEditable(false);
        allergiesField3.setEditable(false);
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
        treatmentField3.setText(newTreatment);
    }

    private void clearFields() {
        idField3.clear();
        nameField3.clear();
        diagnosisField3.clear();
        ageField3.clear();
        genderField3.clear();
        allergiesField3.clear();
        admissionDateField3.clear();
        triageComboBox3.setValue("white");
        treatmentField3.clear();
    }

    public String getPatientName() {
        return nameField3.getText();
    }

    public String getPatientID() {
        return idField3.getText();
    }




}
