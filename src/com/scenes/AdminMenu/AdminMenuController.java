package com.scenes.AdminMenu;

import com.scenes.AdminMenu.AddRole.AddRole;
import com.scenes.AdminMenu.DeleteRole.DeleteRole;
import com.scenes.AdminMenu.SetAccountRole.SetAccountRole;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AdminMenuController {
    @FXML
    public Button addRole, deleteRole;

    @FXML
    public void showSetAccountRole() {
        SetAccountRole.show();
    }

    @FXML
    public void addRoleBtn() {
        AddRole.show();
    }

    @FXML
    public void deleteRoleBtn(ActionEvent actionEvent) {
        DeleteRole.show();
    }
}
