package com.scenes.DirectorMenu.Vacations;

import javafx.stage.Stage;

import static com.assets.services.Consts.openNewWindow;

public class Vacations {
    public static void show() {
        openNewWindow(new Stage(), Vacations.class, "vacations.fxml", "Отпуска");
    }
}
