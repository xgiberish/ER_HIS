package com.example.finalerhis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    public ListView<String> laboratoryTests2;
    public Button addLab2;
    public ListView<String> xrayOrders2;
    public Tab xrayTab2;
    public Tab laboratoryTab2;
    String position = UserSession.getPosition();


    int roomNumber = 2;
    boolean isNewPatient = false;
    PatientRegular1 patientRegular1Controller;
    MainPage mainPageController;
    String url = "jdbc:mysql://localhost:3306/hospital_users";
    String username = "root";
    String password = "";

    public PatientRegular2() {
    }

    @FXML
    private void initialize(){
        idField2.setEditable(true);
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
        populateLabs();
        populateXRayOrders();
        if (position.equals("RN")) {
            // Disable the laboratory and X-ray tabs
            laboratoryTab2.setDisable(true);
            xrayTab2.setDisable(true);
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


            xrayOrders2.setItems(orders);
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
            laboratoryTests2.setItems(testNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        String name = nameField2.getText();
        diagnosisField2.clear();
        ageField2.clear();
        genderField2.clear();
        allergiesField2.clear();
        admissionDateField2.clear();
        triageComboBox2.setValue("white");
        treatmentField2.clear();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT * FROM patient_information WHERE id = ? OR full_name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            statement.setString(2, name);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Retrieve the values from the result set
                id = resultSet.getString("id");
                String diagnosis = resultSet.getString("diagnosis");
                String age = resultSet.getString("age");
                String gender = resultSet.getString("gender");
                String allergies = resultSet.getString("allergies");
                String admission_date = resultSet.getString("admission_date");
                String triage = resultSet.getString("triage");
                String treatment = resultSet.getString("treatment");

                // Set the values in the respective text fields
                idField2.setText(id);
                nameField2.setText(resultSet.getString("full_name"));
                diagnosisField2.setText(diagnosis);
                ageField2.setText(age);
                genderField2.setText(gender);
                allergiesField2.setText(allergies);
                admissionDateField2.setText(admission_date);
                triageComboBox2.setValue(triage);
                treatmentField2.setText(treatment);

                // Enable/disable appropriate buttons and fields
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
                // Display an error message if the patient is not found
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Patient Not Found");
                alert.setHeaderText(null);
                alert.setContentText("Patient with ID " + id + " or name " + name + " not found.");

                ButtonType createNewPatientButton = new ButtonType("Create New Patient");
                ButtonType cancelButton = new ButtonType("Cancel");
                alert.getButtonTypes().setAll(createNewPatientButton, cancelButton);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == createNewPatientButton) {
                    // Set fields for creating a new patient
                    idField2.setEditable(false);
                    idField2.setText(generateNewPatientID());

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

        if (isNewPatient) {
            insertPatientData(id, name, diagnosis, age, gender, allergies, triage, treatment, time, admission_date);
        } else {
            updatePatientData(id, name, diagnosis, age, gender, allergies, triage, treatment, time, admission_date);
        }
        idField2.setEditable(false);
        dischargeButton2.setDisable(false);
        nameField2.setEditable(false);
        searchButton2.setDisable(true);
        waitingRoomButton2.setDisable(true);
        admitButton2.setDisable(true);
        updateRoomsTable(roomNumber,id);
        mainPageController.fetchRoomDataFromDatabase();

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
        addMedication2.setDisable(true);
        waitingRoomButton2.setDisable(true);
        admitButton2.setDisable(true);
        updateRoomPatientId(roomNumber);

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
        triageComboBox2.setValue("white");
        treatmentField2.clear();
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
