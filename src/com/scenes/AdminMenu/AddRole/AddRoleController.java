package com.scenes.AdminMenu.AddRole;

import com.assets.services.DBConnector;
import com.assets.services.Request;
import com.scenes.Modal.Modal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.ConnectException;
import java.sql.SQLException;

import static com.assets.services.Consts.*;

public class AddRoleController {
    @FXML
    private TextField roleTextField;

    public void add() {
        String roleName = roleTextField.getText().trim();

        if (!roleName.matches(roleRegex)) {
            Modal.show("Ошибка", "Ошибка заполнения полей.", "Не верно введена роль", Modal.Icon.error);
            return;
        }

        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        int id;
        try {
            id = Request.count(
                    "roles",
                    new String[]{"name"},
                    stmt -> stmt.setString(1, roleName),
                    r -> r.getInt("count"));
        } catch (ConnectException e) {
            Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        if (id != 0) {
            Modal.show("Ошибка", "Ошибка ввода роли", "Заданная роль уже существует.", Modal.Icon.information);
            return;
        }

        boolean request;
        try {
            request = Request.insert("roles",
                    new String[]{"name"},
                    stmt -> stmt.setString(1, roleName)
            );
        } catch (ConnectException e) {
            Modal.show("Ошибка", "Ошибка установки роли", "Не удалось установить роль.", Modal.Icon.information);
            return;
        }

        if (!request) {
            Modal.show("Ошибка", "Ошибка добавления", "Не удалось добавить роль.", Modal.Icon.error);
            return;
        }
        Modal.show("Успех", "Успешное добавление", "Новая роль добавлена.", Modal.Icon.success);
        roleTextField.setText("");
    }
}
