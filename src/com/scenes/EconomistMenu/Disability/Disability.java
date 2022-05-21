package com.scenes.EconomistMenu.Disability;

import javafx.stage.Stage;

import static com.assets.services.Consts.openNewWindow;

public class Disability {
    public static void show() {
        openNewWindow(new Stage(), Disability.class, "disability.fxml", "Отсутствия");
    }
}