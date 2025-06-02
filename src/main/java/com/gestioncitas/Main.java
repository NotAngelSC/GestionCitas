package com.gestioncitas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el FXML de login
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));

        // Crear la escena y agregar hoja de estilos (si existe)
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        // Configurar el Stage principal
        primaryStage.setTitle("GestionCitas - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

