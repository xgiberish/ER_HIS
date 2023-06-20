package com.example.finalerhis;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    public Text password;
    public Button login;
    @FXML
    private TextArea usernameTextArea;
    @FXML
    private PasswordField passwordField;

    private Stage stage;
    String url = "jdbc:mysql://localhost:3306/hospital_users";
    String dbUsername = "root";
    String dbPassword = "";

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void handleLogin() {
        String username = usernameTextArea.getText();
        String password = passwordField.getText();


        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            // Prepare the SQL statement
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Successful login
                System.out.println("Login successful!");
                // Check if the user is an admin
                boolean isAdmin = resultSet.getBoolean("admin");
                String userPosition = resultSet.getString("position");

                // Set user position in UserSession
                UserSession.setPosition(userPosition);

                FXMLLoader fxmlLoader;
                if (isAdmin) {
                    // Redirect to admin-panel.fxml
                    fxmlLoader = new FXMLLoader(getClass().getResource("admin-panel.fxml"));
                } else {
                    // Redirect to main-page.fxml
                    fxmlLoader = new FXMLLoader(getClass().getResource("main-page.fxml"));
                }
                stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
            } else {
                // Invalid credentials
                showAlert();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String getUserPosition(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String position = null;

        try {
            // Establish a database connection
            conn = DriverManager.getConnection(url, dbUsername,dbPassword);

            // Prepare the SQL statement
            String sql = "SELECT position FROM users WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            // Execute the query
            rs = stmt.executeQuery();

            // Retrieve the position from the result set
            if (rs.next()) {
                position = rs.getString("position");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the database resources
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return position;
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid credentials");
        alert.setHeaderText(null);
        alert.setContentText("Invalid username or password!");
        alert.showAndWait();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        LogoutUtils.handleLogout(event, stage);
    }


}
