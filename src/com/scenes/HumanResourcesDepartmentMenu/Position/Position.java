package com.scenes.HumanResourcesDepartmentMenu.Position;

import javafx.stage.Stage;

import static com.assets.services.Consts.openNewWindow;

public class Position {
    public static void show() {
        openNewWindow(new Stage(), Position.class, "position.fxml", "Добавить дожность");
    }
}
