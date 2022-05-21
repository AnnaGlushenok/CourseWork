package com.scenes.HumanResourcesDepartmentMenu;

import com.scenes.HumanResourcesDepartmentMenu.AddEmployee.AddEmployee;
import com.scenes.HumanResourcesDepartmentMenu.DeleteEmployee.DeleteEmployee;
import com.scenes.HumanResourcesDepartmentMenu.Position.Position;
import com.scenes.HumanResourcesDepartmentMenu.SetPosition.SetPosition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HumanResourcesDepartmentMenuController {
    @FXML
    Button addEmployeeBtn;
    @FXML
    Button deleteEmployeeBtn;

    public void showDeleteEmployee() {
        DeleteEmployee.show();
    }

    public void showAddEmployee() {
        AddEmployee.show();
    }

    public void showAddPosition() {
        Position.show();
    }

    public void showSetPosotion() {
        SetPosition.show();
    }
}
