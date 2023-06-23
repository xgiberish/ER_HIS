package com.example.finalerhis;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.*;

public class MainPage {
    @FXML
    public TitledPane patientViewPane;
    @FXML
    public TitledPane intensiveRoomsPane;
    @FXML
    public Button logoutButton;
    @FXML
    public TableView waitingRoom;
    @FXML
    private TableView<ObservableList<Object>> patientView;
    @FXML
    private TableColumn<ObservableList<Object>, Integer> idColumn;
    @FXML
    private TableColumn<ObservableList<Object>, Integer> roomColumn;

    String url = "jdbc:mysql://localhost:3306/hospital_users";
    String usernameDB = "root";
    String password = "";

    public void setStage(Stage stage) {
        // Declare the stage variable
    }
    @FXML
    public void initialize(){
        populateTable();
    }

    private void handlePopupWindowClose(WindowEvent event) {
        populateTable();
    }


    public void populateTable(){
        // Configure the columns to display the values
        idColumn.setCellValueFactory(cellData -> {
            Object[] rowData = cellData.getValue().toArray();
            if (rowData.length > 0) {
                return new SimpleObjectProperty<>((Integer) rowData[0]);
            } else {
                return new SimpleObjectProperty<>(null);
            }
        });

        roomColumn.setCellValueFactory(cellData -> {
            Object[] rowData = cellData.getValue().toArray();
            if (rowData.length > 1) {
                return new SimpleObjectProperty<>((Integer) rowData[1]);
            } else {
                return new SimpleObjectProperty<>(null);
            }
        });

        // Populate the patientView table with the room data
        fetchRoomDataFromDatabase();
    }


    public void fetchRoomDataFromDatabase() {
        try (Connection connection = DriverManager.getConnection(url, usernameDB, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT room_number, patient_id FROM rooms")) {

            ObservableList<ObservableList<Object>> rowDataList = FXCollections.observableArrayList();

            while (resultSet.next()) {
                int roomNumber = resultSet.getInt("room_number");
                int patientId = resultSet.getInt("patient_id");

                ObservableList<Object> rowData = FXCollections.observableArrayList(patientId, roomNumber);
                rowDataList.add(rowData);
            }

            patientView.setItems(rowDataList);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any errors that may occur during database access
        }
    }


    @FXML
    private void openPatientInformation1(ActionEvent event) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-regular1.fxml"));
            root = loader.load();

            // Retrieve patient ID from the rooms table
            String patientID = getPatientIDFromRoomNumber(1); // Replace 1 with the actual room number

            if (patientID != null) {
                // If patient ID is present, fill out the information
                PatientRegular1 patientInfoController = loader.getController();
                patientInfoController.fillFieldsWithPatientData(patientID);
            } else {
                // If no patient ID is present, display a message
                PatientRegular1 patientInfoController = loader.getController();
                patientInfoController.displayEmptyRoomMessage();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage newStage = new Stage(); // Create a new Stage instance
        newStage.setScene(new Scene(root));
        newStage.setTitle("Regular Room 1");
        newStage.show();
        newStage.setOnCloseRequest(this::handlePopupWindowClose);
    }

    private String getPatientIDFromRoomNumber(int roomNumber) {
        try (Connection connection = DriverManager.getConnection(url, usernameDB, password)) {
            String sql = "SELECT patient_Id FROM rooms WHERE room_number = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, roomNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("patient_Id");
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @FXML
    private void openPatientInformation2(ActionEvent event) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-regular2.fxml"));
            root = loader.load();

            // Retrieve patient ID from the rooms table
            String patientID = getPatientIDFromRoomNumber(2); // Replace 1 with the actual room number

            if (patientID != null) {
                // If patient ID is present, fill out the information
                PatientRegular2 patientInfoController = loader.getController();
                patientInfoController.fillFieldsWithPatientData2(patientID);
            } else {
                // If no patient ID is present, display a message
                PatientRegular2 patientInfoController = loader.getController();
                patientInfoController.displayEmptyRoomMessage();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage newStage = new Stage(); // Create a new Stage instance
        newStage.setScene(new Scene(root));
        newStage.setTitle("Regular Room 2");
        newStage.show();
        newStage.setOnCloseRequest(this::handlePopupWindowClose);
    }

    @FXML
    private void openPatientInformation3(ActionEvent event) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-regular3.fxml"));
            root = loader.load();

            // Retrieve patient ID from the rooms table
            String patientID = getPatientIDFromRoomNumber(3); // Replace 1 with the actual room number

            if (patientID != null) {
                // If patient ID is present, fill out the information
                PatientRegular3 patientInfoController = loader.getController();
                patientInfoController.fillFieldsWithPatientData3(patientID);
            } else {
                // If no patient ID is present, display a message
                PatientRegular3 patientInfoController = loader.getController();
                patientInfoController.displayEmptyRoomMessage();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage newStage = new Stage(); // Create a new Stage instance
        newStage.setScene(new Scene(root));
        newStage.setTitle("Regular Room 3");
        newStage.show();
        newStage.setOnCloseRequest(this::handlePopupWindowClose);
    }

    @FXML
    private void openPatientInformation4(ActionEvent event) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-regular4.fxml"));
            root = loader.load();

            // Retrieve patient ID from the rooms table
            String patientID = getPatientIDFromRoomNumber(4); // Replace 1 with the actual room number

            if (patientID != null) {
                // If patient ID is present, fill out the information
                PatientRegular4 patientInfoController = loader.getController();
                patientInfoController.fillFieldsWithPatientData4(patientID);
            } else {
                // If no patient ID is present, display a message
                PatientRegular4 patientInfoController = loader.getController();
                patientInfoController.displayEmptyRoomMessage();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage newStage = new Stage(); // Create a new Stage instance
        newStage.setScene(new Scene(root));
        newStage.setTitle("Regular Room 4");
        newStage.show();
        newStage.setOnCloseRequest(this::handlePopupWindowClose);
    }

    @FXML
    private void openPatientInformationICU1(ActionEvent event) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-icu1.fxml"));
            root = loader.load();

            // Retrieve patient ID from the rooms table
            String patientID = getPatientIDFromRoomNumber(5); // Replace 1 with the actual room number

            if (patientID != null) {
                // If patient ID is present, fill out the information
                PatientIcu1 patientInfoController = loader.getController();
                patientInfoController.fillFieldsWithPatientData5(patientID);
            } else {
                // If no patient ID is present, display a message
                PatientIcu1 patientInfoController = loader.getController();
                patientInfoController.displayEmptyRoomMessage();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage newStage = new Stage(); // Create a new Stage instance
        newStage.setScene(new Scene(root));
        newStage.setTitle("Intensive Room 1");
        newStage.show();
        newStage.setOnCloseRequest(this::handlePopupWindowClose);
    }

    @FXML
    private void openPatientInformationICU2(ActionEvent event) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("patient-icu2.fxml"));
            root = loader.load();

            // Retrieve patient ID from the rooms table
            String patientID = getPatientIDFromRoomNumber(6); // Replace 1 with the actual room number

            if (patientID != null) {
                // If patient ID is present, fill out the information
                PatientIcu2 patientInfoController = loader.getController();
                patientInfoController.fillFieldsWithPatientData6(patientID);
            } else {
                // If no patient ID is present, display a message
                PatientIcu2 patientInfoController = loader.getController();
                patientInfoController.displayEmptyRoomMessage();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage newStage = new Stage(); // Create a new Stage instance
        newStage.setScene(new Scene(root));
        newStage.setTitle("Intensive Room 2");
        newStage.show();
        newStage.setOnCloseRequest(this::handlePopupWindowClose);
    }
    

    @FXML
    private TitledPane regularRoomsPane;

    @FXML
    private void toggleTitledPane(ActionEvent event) {
        regularRoomsPane.setExpanded(!regularRoomsPane.isExpanded());
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Stage currentStage = (Stage) logoutButton.getScene().getWindow();
        LogoutUtils.handleLogout(event, currentStage);
    }
}