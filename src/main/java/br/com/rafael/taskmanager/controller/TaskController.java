package br.com.rafael.taskmanager.controller;

import br.com.rafael.taskmanager.dao.TaskDAO;
import br.com.rafael.taskmanager.model.Task;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TaskController {

    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField statusField;

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, String> statusColumn;

    @FXML private Label totalLabel;
    @FXML private Label pendingLabel;
    @FXML private Label doneLabel;

    private final TaskDAO taskDAO = new TaskDAO();
    private final ObservableList<Task> taskList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        titleColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitle()));

        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus()));
        
        taskTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);

                if (task == null || empty) {
                    setStyle("");
                } else if ("Concluída".equals(task.getStatus())) {
                    setStyle("-fx-background-color: #d4edda;");
                } else {
                    setStyle("");
                }
            }
        });

        loadTasks();

        taskTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        titleField.setText(newSelection.getTitle());
                        descriptionField.setText(newSelection.getDescription());
                        statusField.setText(newSelection.getStatus());
                    }
                }
        );
    }

    @FXML
    private void saveTask() {

        Task task = new Task(
                titleField.getText(),
                descriptionField.getText(),
                statusField.getText()
        );

        taskDAO.save(task);
        clearFields();
        loadTasks();
    }

    @FXML
    private void updateTask() {

        Task selected = taskTable.getSelectionModel().getSelectedItem();

        if (selected != null) {

            selected.setTitle(titleField.getText());
            selected.setDescription(descriptionField.getText());
            selected.setStatus(statusField.getText());

            taskDAO.update(selected);
            clearFields();
            loadTasks();
        }
    }

    @FXML
    private void completeTask() {

        Task selected = taskTable.getSelectionModel().getSelectedItem();

        if (selected != null) {

            selected.setStatus("Concluída");
            taskDAO.update(selected);

            clearFields();
            loadTasks();
        }
    }

    @FXML
    private void deleteTask() {

        Task selected = taskTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            taskDAO.delete(selected.getId());
            clearFields();
            loadTasks();
        }
    }

    private void loadTasks() {
        taskList.clear();
        taskList.addAll(taskDAO.findAll());
        taskTable.setItems(taskList);
        updateDashboard();
    }

    private void updateDashboard() {
        totalLabel.setText(String.valueOf(taskDAO.countAll()));
        pendingLabel.setText(String.valueOf(taskDAO.countByStatus("Pendente")));
        doneLabel.setText(String.valueOf(taskDAO.countByStatus("Concluída")));
    }

    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
        statusField.clear();
    }
}
