package com.scenes.AdminMenu.SetAccountRole;

import com.App;
import com.assets.services.AutoCompleteComboBoxListener;
import com.assets.services.DBConnector;
import com.assets.services.Request;
import com.scenes.Modal.Modal;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import static com.assets.services.Consts.*;

public class SetAccountRoleController {
    @FXML
    private ComboBox<String> loginList, roleList;

    @FXML
    public void set() {
        String login = loginList.getValue();
        String role = roleList.getValue();

        login = login == null ? "" : login.trim();
        role = role == null ? "" : role.trim();

        if (!login.matches(loginRegex) || !role.matches(roleRegex)) {
            Modal.show("Ошибка", "Ошибка заполнения полей.", "Неверно заполнены поля.", Modal.Icon.error);
            return;
        }

        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        //получу id аккаунта
        List<Integer> accountId;
        try {
            String finalLogin = login;
            accountId = Request.selectWhere(new String[]{"id"},
                    "accounts",
                    new String[]{"login"},
                    stmt -> stmt.setString(1, finalLogin),
                    r -> r.getInt("id"));
        } catch (ConnectException | SQLException e) {
            Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        List<Integer> roleId;
        try {
            String finalRole = role;
            roleId = Request.selectWhereLimitOne(new String[]{"id"},
                    "roles",
                    new String[]{"name"},
                    stmt -> stmt.setString(1, finalRole),
                    r -> r.getInt("id"));
        } catch (ConnectException | SQLException e) {
            Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        if (accountId.isEmpty() || roleId.isEmpty()) {
            Modal.show("Ошибка", "Ошибка выполнения запроса", "Не удалось найти аккаунт и/или роль.", Modal.Icon.error);
            return;
        }

        int count;
        try {
            count = Request.count(
                    "accounts_roles",
                    new String[]{"id_account"},
                    stmt -> stmt.setInt(1, accountId.get(0)),
                    r -> r.getInt("count"));
        } catch (ConnectException e) {
            Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        boolean request;
        if (count == 1) {
            try {
                request = Request.update("accounts_roles",
                        "id_role",
                        new String[]{"id_account"},
                        stmt -> {
                            stmt.setInt(1, roleId.get(0));
                            stmt.setInt(2, accountId.get(0));
                        });
            } catch (ConnectException e) {
                Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
                return;
            }
        } else {
            try {
                request = Request.insert("accounts_roles",
                        new String[]{"id_account", "id_role"},
                        stmt -> {
                            stmt.setInt(1, accountId.get(0));
                            stmt.setInt(2, roleId.get(0));
                        }
                );
            } catch (ConnectException e) {
                Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
                return;
            }

        }
        if (!request) {
            Modal.show("Ошибка", "Ошибка выполнения запроса", "Не удалось установить роль.", Modal.Icon.error);
            return;
        }
        Modal.show("Успех", "Роль установлена", "Установление роли прошло успешно.", Modal.Icon.success);
        roleList.setValue(null);
        loginList.setValue(null);
    }

    public void initialize() {
        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            ((Stage)roleList.getScene().getWindow()).close();
            return;
        }

//        String queryRole = "SELECT `name` FROM `roles`";
//        String queryLogin = "SELECT `login` FROM `accounts`";
        List<String> roles;
        List<String> logins;
        try {
            roles = Request.selectWhere(new String[]{"name"},
                    "roles",
                    new String[]{},
                    null,
                    r -> r.getString("name"));

            logins = Request.selectWhere(new String[]{"login"},
                    "accounts",
                    new String[]{},
                    null,
                    r -> r.getString("login"));
            if (roles.isEmpty()) {
                Modal.show("Ошибка", "Ошибка поиска.", "Не удалось найти роли.", Modal.Icon.error);
                ((Stage)roleList.getScene().getWindow()).close();
                return;
            }
            if (logins.isEmpty()) {
                Modal.show("Ошибка", "Ошибка поиска.", "Не удалось найти аккаунты.", Modal.Icon.error);
                ((Stage)roleList.getScene().getWindow()).close();
                return;
            }
        } catch (SQLException | ConnectException e) {
            Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            ((Stage)roleList.getScene().getWindow()).close();
            return;
        }
        loginList.getItems().addAll(logins);
        roleList.getItems().addAll(roles);

        new AutoCompleteComboBoxListener<>(loginList);
        new AutoCompleteComboBoxListener<>(roleList);
    }
}

