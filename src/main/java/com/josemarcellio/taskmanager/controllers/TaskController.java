package com.josemarcellio.taskmanager.controllers;

import com.josemarcellio.taskmanager.utils.DatabaseHelper;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskController {
    @FXML
    private ListView<String> taskList;
    @FXML
    private TextField taskInput;

    public TaskController() {
        DatabaseHelper.createNewDatabase();
    }

    @FXML
    public void initialize() {
        loadTasks();
    }

    @FXML
    public void addTask() {
        String task = taskInput.getText();
        if (!task.isEmpty()) {
            taskList.getItems().add(task);
            taskInput.clear();
            saveTaskToDatabase(task);
        }
    }

    @FXML
    public void removeTask() {
        int selectedTaskIndex = taskList.getSelectionModel().getSelectedIndex();
        if (selectedTaskIndex >= 0) {
            String task = taskList.getItems().get(selectedTaskIndex);
            taskList.getItems().remove(selectedTaskIndex);
            deleteTaskFromDatabase(task);
        }
    }

    private void saveTaskToDatabase(String task) {
        String sql = "INSERT INTO tasks(name) VALUES(?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteTaskFromDatabase(String task) {
        String sql = "DELETE FROM tasks WHERE name = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadTasks() {
        String sql = "SELECT name FROM tasks";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                taskList.getItems().add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
