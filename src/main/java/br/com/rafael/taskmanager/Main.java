package br.com.rafael.taskmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/task-view.fxml")
        );

        Parent root = loader.load();

        Scene scene = new Scene(root);

        // 🔥 Força carregamento do CSS (100% garantido)
        scene.getStylesheets().add(
                getClass().getResource("/view/style.css").toExternalForm()
        );

        stage.setTitle("Gestor de Tasks");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
