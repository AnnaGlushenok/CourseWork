package com.scenes.HumanResourcesDepartmentMenu.AddEmployee;

import javafx.stage.Stage;

import static com.assets.services.Consts.openNewWindow;

public class AddEmployee {
    public static void show() {
        openNewWindow(new Stage(), AddEmployee.class, "addEmployee.fxml", "Добавить сотрудника");
    }
}
