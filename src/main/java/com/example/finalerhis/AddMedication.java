package com.example.finalerhis;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddMedication {
    @FXML
    private TextField nameTextField;

    @FXML
    private TextField patientName;

    @FXML
    private TextField patientID;

    @FXML
    private Button addMedicationButton;

    private PatientRegular1 patientInformationController;

    private PatientRegular2 patientInformation2Controller;

    private PatientRegular3 patientInformation3Controller;

    private PatientRegular4 patientInformation4Controller;

    private PatientIcu1 patientInformation5Controller;

    private PatientIcu2 patientInformation6Controller;


    @FXML
    private ListView<String> medicationListView;

    private String jdbcUrl = "jdbc:mysql://localhost:3306/hospital_users";
    private String username = "root";
    private String password = "";
    private List<String> allMedications;

    public AddMedication() {
    }

    public void loadPatientInformation(String patientName, String patientID) {
        this.patientName.setText(patientName);
        this.patientID.setText(patientID);
    }

    @FXML
    public void initialize() {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             Statement statement = connection.createStatement()) {

            String sqlQuery = "SELECT id, medication FROM medication_list";
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            allMedications = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String content = resultSet.getString("medication");
                allMedications.add(id + ": " + content);
            }

            medicationListView.setItems(FXCollections.observableArrayList(allMedications));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddMedication() {
        String selectedMedication = medicationListView.getSelectionModel().getSelectedItem();
        if (selectedMedication != null) {
            String patientIdValue = patientID.getText().trim();

            try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
                 PreparedStatement statement = connection.prepareStatement(
                         "UPDATE patient_information SET treatment = CONCAT(IFNULL(treatment, ''), ?) WHERE id = ?")) {

                String[] medicationParts = selectedMedication.split(":");
                int medicationId = Integer.parseInt(medicationParts[0].trim());
                String medicationName = medicationParts[1].trim();

                statement.setString(1, "\n" + medicationId + ": " + medicationName);
                statement.setString(2, patientIdValue);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Medication Added", "Medication added successfully!");
                    if (patientInformationController != null) {
                        patientInformationController.updateTreatment(fetchTreatmentFromDatabase());
                    }
                    if (patientInformation2Controller != null) {
                        patientInformation2Controller.updateTreatment(fetchTreatmentFromDatabase());
                    }
                    if (patientInformation3Controller !=null){
                        patientInformation3Controller.updateTreatment(fetchTreatmentFromDatabase());
                    }
                    if (patientInformation4Controller !=null){
                        patientInformation4Controller.updateTreatment(fetchTreatmentFromDatabase());
                    }
                    if (patientInformation5Controller !=null){
                        patientInformation5Controller.updateTreatment(fetchTreatmentFromDatabase());
                    }
                    if (patientInformation6Controller !=null){
                        patientInformation6Controller.updateTreatment(fetchTreatmentFromDatabase());
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Medication Addition Failed", "Failed to add medication!");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private String fetchTreatmentFromDatabase() {
        String patientIdValue = patientID.getText().trim();
        String treatment = "";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT treatment FROM patient_information WHERE id = ?")) {

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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleSearchMedication(KeyEvent event) {
        String searchQuery = nameTextField.getText().trim();
        List<String> filteredMedications = allMedications.stream()
                .filter(medication -> medication.toLowerCase().contains(searchQuery.toLowerCase()))
                .collect(Collectors.toList());
        medicationListView.setItems(FXCollections.observableArrayList(filteredMedications));
    }
    public void setPatientInformationController(PatientRegular1 patientInformationController) {
        this.patientInformationController = patientInformationController;
    }

    public void setPatientInformationController2(PatientRegular2 patientInformation2Controller) {
        this.patientInformation2Controller = patientInformation2Controller;
    }

    public void setPatientInformationController3(PatientRegular3 patientInformation3Controller) {
        this.patientInformation3Controller = patientInformation3Controller;
    }
    public void setPatientInformationController4(PatientRegular4 patientInformation4Controller) {
        this.patientInformation4Controller = patientInformation4Controller;
    }

    public void setPatientInformationController5(PatientIcu1 patientInformation5Controller) {
        this.patientInformation5Controller = patientInformation5Controller;
    }

    public void setPatientInformationController6(PatientIcu2 patientInformation6Controller) {
        this.patientInformation6Controller = patientInformation6Controller;
    }


}
