package com.scenes.HumanResourcesDepartmentMenu.DeleteEmployee;

import javafx.stage.Stage;

import static com.assets.services.Consts.openNewWindow;

public class DeleteEmployee {
    public static void show() {
        openNewWindow(new Stage(), DeleteEmployee.class, "deleteEmployee.fxml", "Удалить сотрудника");
    }
}
