package com.scenes.EconomistMenu.Vacations;

import javafx.stage.Stage;

import static com.assets.services.Consts.openNewWindow;

public class Vacation {
    public static void show() {
        openNewWindow(new Stage(), Vacation.class, "vacation.fxml", "Отпуска");
    }
}
