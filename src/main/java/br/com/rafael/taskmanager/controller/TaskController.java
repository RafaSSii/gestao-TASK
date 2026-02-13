package br.com.rafael.taskmanager.controller;

import br.com.rafael.taskmanager.dao.TaskDAO;
import br.com.rafael.taskmanager.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TaskController {

    // ====== CAMPOS DA TELA ======
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField statusField;

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, String> statusColumn;

    // ====== OBJETOS ======
    private TaskDAO taskDAO = new TaskDAO();
    private ObservableList<Task> taskList = FXCollections.observableArrayList();
    private Task selectedTask;

    // ====== INICIALIZAÇÃO ======
    @FXML
    public void initialize() {

        // Configurar colunas da tabela
        titleColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getTitle()
                )
        );

        statusColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getStatus()
                )
        );

        // Quando clicar numa linha
        taskTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) -> {

                    selectedTask = newValue;

                    if (newValue != null) {
                        titleField.setText(newValue.getTitle());
                        descriptionField.setText(newValue.getDescription());
                        statusField.setText(newValue.getStatus());
                    }
                }
        );

        loadTasks();
    }

    // ====== SALVAR ======
    @FXML
    public void saveTask() {

        Task task = new Task(
                titleField.getText(),
                descriptionField.getText(),
                statusField.getText()
        );

        taskDAO.save(task);

        clearFields();
        loadTasks();
    }

    // ====== ATUALIZAR ======
    @FXML
    public void updateTask() {

        if (selectedTask != null) {

            selectedTask.setTitle(titleField.getText());
            selectedTask.setDescription(descriptionField.getText());
            selectedTask.setStatus(statusField.getText());

            taskDAO.update(selectedTask);

            clearFields();
            loadTasks();
        }
    }

    // ====== DELETAR ======
    @FXML
    public void deleteTask() {

        if (selectedTask != null) {

            taskDAO.delete(selectedTask.getId());

            clearFields();
            loadTasks();
        }
    }

    // ====== CARREGAR TAREFAS ======
    private void loadTasks() {

        taskList.clear();
        taskList.addAll(taskDAO.findAll());
        taskTable.setItems(taskList);
    }

    // ====== LIMPAR CAMPOS ======
    private void clearFields() {

        titleField.clear();
        descriptionField.clear();
        statusField.clear();
        selectedTask = null;
        taskTable.getSelectionModel().clearSelection();
    }
}
