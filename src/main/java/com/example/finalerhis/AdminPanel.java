package com.example.finalerhis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class AdminPanel {
    public Button logoutButton2;
    public ComboBox<String> triageComboBoxAdmin;
    public TextField adminIDTextField;
    public TextField adminFullNameTextField;
    public TextField DOBTextField;
    public TextField diagnosisTextField;
    public Button searchPatient;
    public Button savePatient;
    public Button deletePatient;
    public TextField staffIDTextField;
    public TextField staffFirstName;
    public TextField staffLastName;
    public TextField positionTextField;
    public Button searchStaff;
    public Button saveStaff;
    public Button deleteStaff;
    public TextArea treatmentTextAreaadmin;
    public TextArea staffNotes;
    public TextField genderTextField;
    public TextField allergiesTextField;
    public Button clearPatient;
    public Button clearStaff;
    public TextField dobTextFieldStaff;
    String url = "jdbc:mysql://localhost:3306/hospital_users";
    String username = "root";
    String password = "";
    boolean isNewPatient = false;

    private Stage stage; // Declare the stage variable

    // Setter method to set the stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @FXML
    private void initialize(){
        adminFullNameTextField.setEditable(false);
        DOBTextField.setEditable(false);
        triageComboBoxAdmin.setEditable(false);
        diagnosisTextField.setEditable(false);
        treatmentTextAreaadmin.setEditable(false);
        genderTextField.setEditable(false);
        allergiesTextField.setEditable(false);
        savePatient.setDisable(true);
        deletePatient.setDisable(true);
        clearPatient.setDisable(true);


        staffFirstName.setEditable(false);
        staffLastName.setEditable(false);
        DOBTextField.setEditable(false);
        positionTextField.setEditable(false);
        staffNotes.setEditable(false);
        saveStaff.setDisable(true);
        deleteStaff.setDisable(true);
    }



    @FXML
    private void handleLogout(ActionEvent event) {
        Stage stage = (Stage) logoutButton2.getScene().getWindow();
        LogoutUtils.handleLogout(event, stage);
    }


    public void onSearchPatient(ActionEvent event) {
        String id = adminIDTextField.getText();
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
                String triage = resultSet.getString("triage");
                String treatment = resultSet.getString("treatment");


                adminFullNameTextField.setText(name);
                diagnosisTextField.setText(diagnosis);
                DOBTextField.setText(age);
                genderTextField.setText(gender);
                allergiesTextField.setText(allergies);
                triageComboBoxAdmin.setValue(triage);
                treatmentTextAreaadmin.setText(treatment);

                adminFullNameTextField.setEditable(true);
                DOBTextField.setEditable(true);
                triageComboBoxAdmin.setEditable(true);
                diagnosisTextField.setEditable(true);
                treatmentTextAreaadmin.setEditable(true);
                genderTextField.setEditable(true);
                allergiesTextField.setEditable(true);

                savePatient.setDisable(false);
                deletePatient.setDisable(false);
                clearPatient.setDisable(false);

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
                    adminIDTextField.setText(generateNewPatientID());

                    adminFullNameTextField.setEditable(true);
                    DOBTextField.setEditable(true);
                    triageComboBoxAdmin.setEditable(true);
                    diagnosisTextField.setEditable(true);
                    treatmentTextAreaadmin.setEditable(true);
                    allergiesTextField.setEditable(true);
                    genderTextField.setEditable(true);
                    savePatient.setDisable(false);
                    deletePatient.setDisable(false);
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
    private void insertPatientData(String id, String name, String diagnosis, String age, String gender, String allergies, String triage, String treatment) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "INSERT INTO patient_information (id, full_name, age, gender, allergies, triage, diagnosis, treatment) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            statement.setString(2, name);
            statement.setString(3, age);
            statement.setString(4, gender);
            statement.setString(5, allergies);
            statement.setString(7, triage);
            statement.setString(8, diagnosis);
            statement.setString(9, treatment);

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
    private void clearFields() {
        adminIDTextField.clear();
        DOBTextField.clear();
        adminFullNameTextField.clear();
        diagnosisTextField.clear();
        genderTextField.clear();
        allergiesTextField.clear();
        triageComboBoxAdmin.setValue(null);
        treatmentTextAreaadmin.clear();
    }
    public void onSavePatient(ActionEvent event) {
        String id = adminIDTextField.getText();
        String name = adminFullNameTextField.getText();
        String diagnosis = diagnosisTextField.getText();
        String age = DOBTextField.getText();
        String gender = genderTextField.getText();
        String allergies = allergiesTextField.getText();
        String triage = triageComboBoxAdmin.getValue();
        String treatment = treatmentTextAreaadmin.getText();

        if (isNewPatient) {
            insertPatientData(id, name, diagnosis, age, gender, allergies, triage, treatment);
        } else {
            updatePatientData(id, name, diagnosis, age, gender, allergies, triage, treatment);
        }
    }
    @FXML
    private void updatePatientData(String id, String name, String diagnosis, String age, String gender, String allergies, String triage, String treatment) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "UPDATE patient_information SET full_name = ?, age = ?, gender = ?, allergies = ?, triage = ?, diagnosis = ?, treatment = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, age);
            statement.setString(3, gender);
            statement.setString(4, allergies);
            statement.setString(5, triage);
            statement.setString(6, diagnosis);
            statement.setString(7, treatment);
            statement.setString(8, id);

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


    public void onSearchStaff(ActionEvent event) {
    }

    public void onSaveStaff(ActionEvent event) {
    }

    public void onDeleteStaff(ActionEvent event) {
    }



    public void onDeletePatient(ActionEvent event) {
        String id = adminIDTextField.getText();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this patient?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                String sql = "DELETE FROM patient_information WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, id);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Patient deleted successfully.");
                    successAlert.showAndWait();
                    clearFields();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Failed to delete patient.");
                    errorAlert.showAndWait();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearFieldsStaff(ActionEvent event) {
    }
}
