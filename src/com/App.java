package com;

import com.assets.services.Turnstile;
import com.scenes.AdminMenu.AdminMenu;
import com.scenes.DirectorMenu.DirectorMenu;
import com.scenes.EconomistMenu.EconomistMenu;
import com.scenes.HumanResourcesDepartmentMenu.HumanResourcesDepartmentMenu;
import com.scenes.WorkerMenu.WorkerMenu;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    private static String login;
    private static int id;

    public static String getLogin() {
        return login;
    }

    public static int getID() {
        return id;
    }

    public static void setLogin(String login) {
        App.login = login;
    }

    public static void setId(int id) {
        App.id = id;
    }

    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader loader = new FXMLLoader(EconomistMenu.class.getResource("economistMenu.fxml"));
        //FXMLLoader loader = new FXMLLoader(HumanResourcesDepartmentMenu.class.getResource("humanResourcesDepartmentMenu.fxml"));
        FXMLLoader loader = new FXMLLoader(DirectorMenu.class.getResource("directorMenu.fxml"));
        //FXMLLoader loader = new FXMLLoader(WorkerMenu.class.getResource("workerMenu.fxml"));
        //FXMLLoader loader = new FXMLLoader(AdminMenu.class.getResource("adminMenu.fxml"));
        //FXMLLoader loader = new FXMLLoader(App.class.getResource("App.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("App.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Авторизация");
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/assets/images/modal-icons/information.png")));
        stage.show();
    }

    public static void main(String[] args) {
        Turnstile turnstile = new Turnstile();
        //turnstile.start();
        launch(args);
    }
}