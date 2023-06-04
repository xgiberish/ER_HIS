package com.example.finalerhis;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    @FXML
    private ListView<String> medicationListView;

    private String jdbcUrl = "jdbc:mysql://localhost:3306/hospital_users";
    private String username = "root";
    private String password = "";
    private List<String> allMedications; // Store all medications fetched from the database

    public AddMedication() {
    }
    public void loadPatientInformation(String patientName, String patientID) {
        // Set the retrieved information to the text fields
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
        String medicationName = nameTextField.getText().trim();
        if (!medicationName.isEmpty()) {
            medicationListView.getItems().add(medicationName);
            nameTextField.clear();
        }
    }

    @FXML
    private void handleSearchMedication(KeyEvent event) {
        String searchQuery = nameTextField.getText().trim();
        List<String> filteredMedications = allMedications.stream()
                .filter(medication -> medication.toLowerCase().contains(searchQuery.toLowerCase()))
                .collect(Collectors.toList());
        medicationListView.setItems(FXCollections.observableArrayList(filteredMedications));
    }
}
