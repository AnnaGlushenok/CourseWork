package com.scenes.AdminMenu.SetAccountRole;

import com.assets.services.AutoCompleteComboBoxListener;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import static com.assets.services.Consts.*;

public class SetAccountRole {
    public static void show() {
        openNewWindow(new Stage(), SetAccountRole.class, "setAccountRole.fxml", "Добавить роль");
    }
}
