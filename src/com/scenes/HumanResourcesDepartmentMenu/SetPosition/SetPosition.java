package com.scenes.HumanResourcesDepartmentMenu.SetPosition;

import com.scenes.HumanResourcesDepartmentMenu.Position.Position;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import static com.assets.services.Consts.openNewWindow;

public class SetPosition {
    public static void show() {
        openNewWindow(new Stage(), SetPosition.class, "setPosition.fxml", "Установить должность");
    }
}
