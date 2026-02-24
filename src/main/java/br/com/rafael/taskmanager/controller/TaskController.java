package br.com.rafael.taskmanager.controller;

import br.com.rafael.taskmanager.dao.TaskDAO;
import br.com.rafael.taskmanager.model.Task;
import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;

public class TaskController {

    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<String> priorityCombo;

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, String> dateColumn;

    @FXML private Label totalLabel;
    @FXML private Label pendingLabel;
    @FXML private Label doneLabel;

    @FXML private StackPane rootPane;

    private final TaskDAO taskDAO = new TaskDAO();
    private final ObservableList<Task> taskList = FXCollections.observableArrayList();

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {

        priorityCombo.setItems(
                FXCollections.observableArrayList("Alta", "Média", "Baixa")
        );
        priorityCombo.setValue("Média");

        // 🔥 LINHA ESMAECIDA QUANDO CONCLUÍDA
        taskTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);

                if (task == null || empty) {
                    setStyle("");
                } else if ("Concluída".equals(task.getStatus())) {
                    setStyle("-fx-opacity: 0.6;");
                } else {
                    setStyle("");
                }
            }
        });

        // 🔥 TÍTULO COM ANIMAÇÃO DE RISCO
        titleColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitle()));

        titleColumn.setCellFactory(column -> new TableCell<>() {

            private final Text text = new Text();

            @Override
            protected void updateItem(String title, boolean empty) {
                super.updateItem(title, empty);

                if (empty || title == null) {
                    setGraphic(null);
                } else {

                    Task task = getTableView().getItems().get(getIndex());
                    text.setText(title);

                    if ("Concluída".equals(task.getStatus())) {
                        text.setStrikethrough(true);
                        text.setFill(Color.GRAY);
                    } else {
                        text.setStrikethrough(false);
                        text.setFill(Color.BLACK);
                    }

                    setGraphic(text);
                }
            }
        });

        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus()));

        // PRIORIDADE
        priorityColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPriority()));

        priorityColumn.setCellFactory(column -> new TableCell<>() {

            private final HBox box = new HBox(8);
            private final Circle circle = new Circle(6);
            private final Label label = new Label();

            {
                box.getChildren().addAll(circle, label);
            }

            @Override
            protected void updateItem(String priority, boolean empty) {
                super.updateItem(priority, empty);

                if (empty || priority == null) {
                    setGraphic(null);
                } else {

                    label.setText(priority);

                    switch (priority) {
                        case "Alta" -> circle.setFill(Color.RED);
                        case "Média" -> circle.setFill(Color.ORANGE);
                        case "Baixa" -> circle.setFill(Color.GREEN);
                    }

                    setGraphic(box);
                }
            }
        });

        dateColumn.setCellValueFactory(data -> {
            if (data.getValue().getCreatedAt() != null) {
                return new SimpleStringProperty(
                        data.getValue().getCreatedAt().format(formatter)
                );
            } else {
                return new SimpleStringProperty("-");
            }
        });

        loadTasks();
        updateDashboard();
    }

    @FXML
    private void saveTask() {

        if (titleField.getText().isBlank()) return;

        Task task = new Task(
                titleField.getText(),
                descriptionField.getText(),
                "Pendente",
                priorityCombo.getValue()
        );

        taskDAO.save(task);
        clearFields();
        loadTasks();
        updateDashboard();
    }

    @FXML
    private void completeTask() {

        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        int index = taskTable.getSelectionModel().getSelectedIndex();
        TableRow<Task> row = getRowByIndex(index);

        if (row != null) {

            // 🔥 ANIMAÇÃO SUAVE
            FadeTransition fade = new FadeTransition(Duration.millis(250), row);
            fade.setToValue(0.6);

            ScaleTransition scale = new ScaleTransition(Duration.millis(200), row);
            scale.setToX(1.02);
            scale.setToY(1.02);
            scale.setAutoReverse(true);
            scale.setCycleCount(2);

            ParallelTransition animation =
                    new ParallelTransition(fade, scale);

            animation.setOnFinished(e -> {
                selected.setStatus("Concluída");
                taskDAO.update(selected);
                loadTasks();
                updateDashboard();
            });

            animation.play();
        }
    }

    @FXML
    private void deleteTask() {

        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        taskDAO.delete(selected.getId());
        loadTasks();
        updateDashboard();
        showSnackbar("Tarefa deletada com sucesso!");
    }

    private TableRow<Task> getRowByIndex(int index) {
        for (Node node : taskTable.lookupAll(".table-row-cell")) {
            if (node instanceof TableRow<?> row) {
                if (row.getIndex() == index) {
                    return (TableRow<Task>) row;
                }
            }
        }
        return null;
    }

    private void showSnackbar(String message) {

        Label snackbar = new Label(message);
        snackbar.setStyle("""
                -fx-background-color: #323232;
                -fx-text-fill: white;
                -fx-padding: 10 20;
                -fx-background-radius: 20;
                """);

        snackbar.setTranslateY(100);
        rootPane.getChildren().add(snackbar);

        TranslateTransition slideUp =
                new TranslateTransition(Duration.millis(300), snackbar);
        slideUp.setToY(0);

        PauseTransition stay =
                new PauseTransition(Duration.seconds(2));

        FadeTransition fadeOut =
                new FadeTransition(Duration.millis(400), snackbar);
        fadeOut.setToValue(0);

        SequentialTransition seq =
                new SequentialTransition(slideUp, stay, fadeOut);

        seq.setOnFinished(e -> rootPane.getChildren().remove(snackbar));

        seq.play();
    }

    private void loadTasks() {
        taskList.clear();
        taskList.addAll(taskDAO.findAll());
        taskTable.setItems(taskList);
    }

    private void updateDashboard() {
        totalLabel.setText(String.valueOf(taskDAO.countAll()));
        pendingLabel.setText(String.valueOf(taskDAO.countByStatus("Pendente")));
        doneLabel.setText(String.valueOf(taskDAO.countByStatus("Concluída")));
    }

    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
        priorityCombo.setValue("Média");
    }
}
