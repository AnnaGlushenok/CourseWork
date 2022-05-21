package com.scenes.AdminMenu.DeleteRole;

import javafx.stage.Stage;

import static com.assets.services.Consts.openNewWindow;

public class DeleteRole {
    public static void show() {
        openNewWindow(new Stage(), DeleteRole.class, "deleteRole.fxml", "Удалить роль");
    }
}
