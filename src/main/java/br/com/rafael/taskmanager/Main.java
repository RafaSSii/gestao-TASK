package br.com.rafael.taskmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/task-view.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Gestor de Tasks");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}