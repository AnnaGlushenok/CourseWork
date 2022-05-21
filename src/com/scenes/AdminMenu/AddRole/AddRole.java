package com.scenes.AdminMenu.AddRole;

import javafx.stage.Stage;

import static com.assets.services.Consts.openNewWindow;

public class AddRole {
    public static void show() {
        openNewWindow(new Stage(), AddRole.class, "addRole.fxml", "Добавить роль");
    }
}
