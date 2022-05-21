package com.scenes.AdminMenu.DeleteRole;

import com.assets.services.AutoCompleteComboBoxListener;
import com.assets.services.DBConnector;
import com.assets.services.Request;
import com.scenes.Modal.Modal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import static com.assets.services.Consts.*;

public class DeleteRoleController {
    @FXML
    private Button deleteBtn;
    @FXML
    private ComboBox<String> rolesList;

    public void delete() {
        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        String name = rolesList.getValue();
        boolean request;
        try {
            request = Request.delete("roles",
                    new String[]{"name"},
                    stmt -> stmt.setString(1, name)
            );
        } catch (ConnectException e) {
            Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        if (!request) {
            Modal.show("Ошибка", "Ошибка удаления", "Не удалось удалить роль.", Modal.Icon.error);
            return;
        }
        Modal.show("Успех", "Успешное удаление.", "Роль удалена.", Modal.Icon.success);
        rolesList.getItems().remove(name);
    }

    @FXML
    public void initialize() {
        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        new AutoCompleteComboBoxListener<>(rolesList);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            ((Stage) deleteBtn.getScene().getWindow()).close();
            return;
        }

        try {
            List<String> roles = Request.selectWhere(new String[]{"name"},
                    "roles",
                    new String[]{},
                    null,
                    r -> r.getString("name"));
            rolesList.getItems().addAll(roles);
        } catch (ConnectException | SQLException e) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            ((Stage) deleteBtn.getScene().getWindow()).close();
        }
    }
}
