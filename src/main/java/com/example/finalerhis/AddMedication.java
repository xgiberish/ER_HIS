package com.example.finalerhis;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddMedication {
    @FXML
    private TextField nameTextField;
    @FXML
    private Button addMedicationButton;
    @FXML
    private ListView<String> medicationListView;

    private String jdbcUrl = "jdbc:mysql://localhost:3306/hospital_users";
    private String username = "root";
    private String password = "";

    public AddMedication() {
    }

    @FXML
    public void initialize() {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             Statement statement = connection.createStatement()) {

            String sqlQuery = "SELECT id, content FROM medication_list";
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            List<String> medicationList = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String content = resultSet.getString("content");
                medicationList.add(id + ": " + content);
            }

            medicationListView.setItems(FXCollections.observableArrayList(medicationList));
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
}
