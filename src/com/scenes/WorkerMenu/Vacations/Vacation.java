package com.scenes.WorkerMenu.Vacations;

import javafx.stage.Stage;

import static com.assets.services.Consts.*;

public class Vacation {
    public static void show() {
        openNewWindow(new Stage(),Vacation.class,"vacation.fxml", "Отпуски");
    }
}