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
    public Button generateUser;
    String url = "jdbc:mysql://localhost:3306/hospital_users";
    String username = "root";
    String password = "";
    boolean isNewPatient = false;
    boolean isNewStaffMember = false;


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
        clearStaff.setDisable(true);
        generateUser.setDisable(true);
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
                    clearPatientsFields();
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
    private void clearPatientsFields() {
        adminIDTextField.clear();
        DOBTextField.clear();
        adminFullNameTextField.clear();
        diagnosisTextField.clear();
        genderTextField.clear();
        allergiesTextField.clear();
        triageComboBoxAdmin.setValue(null);
        treatmentTextAreaadmin.clear();

        clearPatient.setDisable(true);
        savePatient.setDisable(true);
        deletePatient.setDisable(true);
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
                    clearPatientsFields();
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
    //////Start of staff panel
    public void onSearchStaff(ActionEvent event) {
        String id = staffIDTextField.getText();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Check if the staff ID exists in the users table as a foreign key
            String checkUserSql = "SELECT * FROM users WHERE staff_id = ?";
            PreparedStatement checkUserStatement = connection.prepareStatement(checkUserSql);
            checkUserStatement.setString(1, id);
            ResultSet userResultSet = checkUserStatement.executeQuery();
            boolean userExists = userResultSet.next();

            String selectStaffSql = "SELECT * FROM hospital_staff WHERE staff_id = ?";
            PreparedStatement selectStaffStatement = connection.prepareStatement(selectStaffSql);
            selectStaffStatement.setString(1, id);
            ResultSet resultSet = selectStaffStatement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String dob = resultSet.getString("DOB");
                String position = resultSet.getString("position");
                String notes = resultSet.getString("notes");

                staffFirstName.setText(firstName);
                staffLastName.setText(lastName);
                dobTextFieldStaff.setText(dob);
                positionTextField.setText(position);
                staffNotes.setText(notes);

                staffFirstName.setEditable(true);
                staffLastName.setEditable(true);
                dobTextFieldStaff.setEditable(true);
                positionTextField.setEditable(true);
                staffNotes.setEditable(true);

                saveStaff.setDisable(false);
                deleteStaff.setDisable(false);
                clearStaff.setDisable(false);
                generateUser.setDisable(userExists); // Disable Generate User button if user exists
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Staff Member Not Found");
                alert.setHeaderText(null);
                alert.setContentText("Staff member with ID " + id + " not found.");

                ButtonType createNewStaffButton = new ButtonType("Create New Staff");
                ButtonType cancelButton = new ButtonType("Cancel");
                alert.getButtonTypes().setAll(createNewStaffButton, cancelButton);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == createNewStaffButton) {
                    clearFieldsStaff();
                } else {
                    clearFieldsStaff();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void insertStaffData(String id, String firstName, String lastName, String dob, String position, String notes) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "INSERT INTO hospital_staff (staff_id, first_name, last_name, DOB, position, notes) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, dob);
            statement.setString(5, position);
            statement.setString(6, notes);

            int rowsAffected = statement.executeUpdate();

            Alert alert;
            if (rowsAffected > 0) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Staff member created successfully.");
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to create staff member.");
            }
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSaveStaff(ActionEvent event) {
        String id = staffIDTextField.getText();
        String firstName = staffFirstName.getText();
        String lastName = staffLastName.getText();
        String dob = dobTextFieldStaff.getText();
        String position = positionTextField.getText();
        String notes = staffNotes.getText();

        if (isNewStaffMember) {
            insertStaffData(id, firstName, lastName, dob, position, notes);
        } else {
            updateStaffData(id, firstName, lastName, dob, position, notes);
        }
    }

    @FXML
    private void updateStaffData(String id, String firstName, String lastName, String dob, String position, String notes) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "UPDATE hospital_staff SET first_name = ?, last_name = ?, DOB = ?, position = ?, notes = ? WHERE staff_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, dob);
            statement.setString(4, position);
            statement.setString(5, notes);
            statement.setString(6, id);

            int rowsAffected = statement.executeUpdate();

            Alert alert;
            if (rowsAffected > 0) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Staff member information updated successfully.");
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to update staff member information.");
            }
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clearFieldsStaff() {
        staffIDTextField.clear();
        staffFirstName.clear();
        staffLastName.clear();
        dobTextFieldStaff.clear();
        positionTextField.clear();
        staffNotes.clear();
    }

    @FXML
    private void generateUser(ActionEvent event) {
        String firstName = staffFirstName.getText();
        String lastName = staffLastName.getText();
        String generatedUsername = generateUsername(firstName);
        String generatedPassword = generateStrongPassword();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "INSERT INTO users (username, password, position, admin, staff_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, generatedUsername);
            statement.setString(2, generatedPassword);
            statement.setString(3, positionTextField.getText());
            statement.setString(4, "false"); // assuming admin is always false for new staff members
            statement.setString(5, staffIDTextField.getText());

            int rowsAffected = statement.executeUpdate();

            Alert alert;
            if (rowsAffected > 0) {
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("User Created");
                alert.setHeaderText(null);
                alert.setContentText("Username: " + generatedUsername + "\nPassword: " + generatedPassword);
            } else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to create user.");
            }
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String generateUsername(String firstName) {
        // Generate a username based on the first name with an autogenerated number next to it
        // You can customize this method according to your requirements
        // Example: firstName = "John" -> username = "john123"
        int number = 1;
        String username = firstName.toLowerCase() + number;

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "SELECT username FROM users WHERE username LIKE ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                number++;
                username = firstName.toLowerCase() + number;
                statement.setString(1, username + "%");
                resultSet = statement.executeQuery();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return username;
    }

    private String generateStrongPassword() {
        String lowercaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        String specialCharacters = "!@#$%^&*()_-+=<>?";

        StringBuilder password = new StringBuilder();

        // Add a lowercase letter
        password.append(lowercaseLetters.charAt((int) (Math.random() * lowercaseLetters.length())));

        // Add an uppercase letter
        password.append(uppercaseLetters.charAt((int) (Math.random() * uppercaseLetters.length())));

        // Add a number
        password.append(numbers.charAt((int) (Math.random() * numbers.length())));

        // Add a special character
        password.append(specialCharacters.charAt((int) (Math.random() * specialCharacters.length())));

        // Generate the remaining characters
        int remainingLength = 8 - password.length(); // Adjust the length as desired
        for (int i = 0; i < remainingLength; i++) {
            String characterSet = lowercaseLetters + uppercaseLetters + numbers + specialCharacters;
            password.append(characterSet.charAt((int) (Math.random() * characterSet.length())));
        }

        // Shuffle the password characters
        char[] passwordArray = password.toString().toCharArray();
        for (int i = 0; i < password.length(); i++) {
            int j = (int) (Math.random() * (password.length() - i)) + i;
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }

    public void onDeleteStaff(ActionEvent event) {
        String id = adminIDTextField.getText();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this staff member?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                connection.setAutoCommit(false); // Disable auto-commit

                // Delete staff from staff_information table
                String deleteStaffSql = "DELETE FROM staff_information WHERE id = ?";
                PreparedStatement deleteStaffStatement = connection.prepareStatement(deleteStaffSql);
                deleteStaffStatement.setString(1, id);
                int staffRowsAffected = deleteStaffStatement.executeUpdate();

                // Delete staff from users table if they exist
                String deleteUserSql = "DELETE FROM users WHERE id = ?";
                PreparedStatement deleteUserStatement = connection.prepareStatement(deleteUserSql);
                deleteUserStatement.setString(1, id);
                int userRowsAffected = deleteUserStatement.executeUpdate();

                if (staffRowsAffected > 0 && userRowsAffected > 0) {
                    connection.commit(); // Commit the transaction
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Staff member deleted successfully.");
                    successAlert.showAndWait();
                    clearFieldsStaff();
                } else {
                    connection.rollback(); // Rollback the transaction
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Failed to delete staff member.");
                    errorAlert.showAndWait();
                }

                connection.setAutoCommit(true); // Enable auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }




    public void clearFieldsStaff(ActionEvent event) {
        staffIDTextField.clear();
        staffFirstName.clear();
        staffLastName.clear();
        dobTextFieldStaff.clear();
        positionTextField.clear();
        staffNotes.clear();

        generateUser.setDisable(true);
        saveStaff.setDisable(true);
        deleteStaff.setDisable(true);
        clearStaff.setDisable(true);

    }
}
