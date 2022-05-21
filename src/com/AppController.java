package com;

import com.assets.services.Consts;
import com.assets.services.DBConnector;
import com.assets.services.Security;
import com.scenes.AdminMenu.AdminMenu;
import com.scenes.Modal.Modal;
import com.scenes.WorkerMenu.WorkerMenu;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.assets.services.Consts.*;
import static com.assets.services.Consts.username;

public class AppController {
    @FXML
    public TextField loginTextField;
    @FXML
    public PasswordField passwordField;

    @FXML
    protected void enterBtn() {
        String login = loginTextField.getText();
        String password = passwordField.getText();

        if (!login.matches(loginRegex) || !password.matches(passwordRegex)) {
            Modal.show("Ошибка", "Ошибка заполнения", "Не заполнены поля. Проверьте.", Modal.Icon.error);
            return;
        }

        String hashPassword = Security.getHash(password);

        DBConnector connector = new DBConnector(DBType, url, DB, username, Consts.password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        String query = "SELECT `roles`.`name` as `role`,`accounts`.`login`, `accounts`.`id_owner` as `id` FROM " +
                "((`accounts` INNER JOIN `accounts_roles` on `accounts`.`id`=`accounts_roles`.`id_account`) " +
                "INNER JOIN `roles` on `roles`.`id`=`accounts_roles`.`id_role` " +
                ") WHERE `accounts`.`id_owner` IS NOT NULL AND `login`=? AND `password_hash`=?";

        List<String[]> account;
        try {
            account = connector.getObjects(query, (stmt) -> {
                        stmt.setString(1, login);
                        stmt.setString(2, hashPassword);
                    },
                    (r) -> new String[]{
                            r.getString("role"),
                            r.getString("login"),
                            r.getString("id")
                    }
            );
        } catch (SQLException e) {
            e.printStackTrace();
            Modal.show("Ошибка", "Ошибка запроса", "Не удалось выполнить запрос к базе данных.", Modal.Icon.error);
            return;
        }

        if (account.isEmpty()) {
            Modal.show("Ошибка", "Ошибка аутентификации", "Не удалось найти данный аккаунт.\nНеверный логин и/или пароль", Modal.Icon.error);
            return;
        }

        App.setLogin(account.get(0)[1]);
        App.setId(Integer.parseInt(account.get(0)[2]));
        switch (account.get(0)[0]) {
            case "admin" ->
                    Consts.show((Stage) loginTextField.getScene().getWindow(), "/com/scenes/AdminMenu/adminMenu.fxml", "Меню");
            case "economist" ->
                    Consts.show((Stage) loginTextField.getScene().getWindow(), "/com/scenes/AdminMenu/adminMenu.fxml", "Меню");
            case "worker" ->
                    Consts.show((Stage) loginTextField.getScene().getWindow(), "/com/scenes/WorkerMenu/workerMenu.fxml", "Меню");
            case "humanResourcesDepartment" ->
                    Consts.show((Stage) loginTextField.getScene().getWindow(), "/com/scenes/HumanResourcesDepartmentMenu/humanResourcesDepartmentMenu.fxml", "Меню");
            case "director" ->
                    Consts.show((Stage) loginTextField.getScene().getWindow(), "/com/scenes/DirectorMenu/director.fxml", "Меню");
        }
        //  connector.getObjects();
    }
}
