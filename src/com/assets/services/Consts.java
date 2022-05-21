package com.assets.services;

import com.scenes.AdminMenu.AdminMenu;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Consts {
    public static final String DBType = "mysql";
    public static final String url = "localhost";
    public static final String DB = "company";
    public static final String username = "root";
    public static final String password = "";

    public static final String nameRegex = "^[А-ЯЁ][а-яё]+(-[А-ЯЁ][а-яё]+)*$";
    public static final String sentenceRegex = "^[а-яА-ЯёЁ\s.,\\-!?;:\"'()\\[\\]]+$";

    public static final String loginRegex = "^[A-Za-z0-9_]{1,31}$";
    public static final String passwordRegex = "^[A-Za-z0-9_$@%#&*^!]{5,32}$";
    public static final String roleRegex = "^[a-z]+$";
    public static final String telephoneRegex = "^\\d{12}+$";

    public static void openNewWindow(Stage stage, Class resource, String file, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(resource.getResource(file));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка открытия окна.");
        }
    }

    public static void show(Stage stage, String path, String title) {
        FXMLLoader loader = new FXMLLoader(AdminMenu.class.getResource(path));
        try {
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
            stage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
