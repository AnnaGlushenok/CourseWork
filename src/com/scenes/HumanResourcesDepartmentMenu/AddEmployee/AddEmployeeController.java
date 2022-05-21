package com.scenes.HumanResourcesDepartmentMenu.AddEmployee;

import com.assets.services.*;
import com.scenes.Modal.Modal;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static com.assets.services.Consts.*;

public class AddEmployeeController {
    @FXML
    private ComboBox<String> positionList, timetableList, sexList, roleList;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField surnameTextField, nameTextField, patronymicTextField, loginTextField,
            telephoneTextField, addressTextField, ageTextField;

    @FXML
    protected void fix() throws SQLException {
        String surname = surnameTextField.getText().trim();
        String name = nameTextField.getText().trim();
        String patronymic = patronymicTextField.getText().trim();
        String age = ageTextField.getText().trim();
        String login = loginTextField.getText().trim();
        String password = passwordField.getText().trim();
        String telephone = telephoneTextField.getText().trim();
        String address = addressTextField.getText().trim();

        String position = positionList.getValue();
        position = position == null ? "" : position.trim();
        String timetable = timetableList.getValue();
        timetable = timetable == null ? "" : timetable.trim();
        String sex = sexList.getValue();
        sex = sex == null ? "" : sex.trim();
        String role = roleList.getValue();
        role = role == null ? "" : role.trim();

        StringBuilder error = new StringBuilder();
        StringBuilder info = new StringBuilder();

        if (!surname.matches(nameRegex) || !name.matches(nameRegex) || !patronymic.matches(nameRegex))
            error.append("Неверно введено ФИО.\n");

        if (!age.matches("\\d{2}+"))
            error.append("Неверный возраст.\n");

        if (!telephone.matches(telephoneRegex))
            info.append("Телефон должен содержать 12 симолов вместе с кодом (375(__), но без +.\n");

        if (Objects.equals(address, ""))
            error.append("Ошибка в адресе.\n");

        if (!roleList.getItems().contains(role))
            error.append("Неверно введена роль.\n");

        if (!positionList.getItems().contains(position))
            error.append("Не выбрана должность.\n");

        if (!timetableList.getItems().contains(timetable))
            error.append("Не выбрано расписание.\n");

        if (!sexList.getItems().contains(sex))
            error.append("Не выбран пол.\n");

        if (!login.matches(loginRegex) || !password.matches(passwordRegex))
            info.append("""
                       Логин может содержать:
                    •Большие (английские) буквы;
                    •Маленькие (английские) буквы;
                    •Цифры;
                         Пароль может содержать:
                    •Большие (английские) буквы;
                    •Маленькие (английские) буквы;
                    •Цифры;
                    •Символы (_$@%#&*^!);
                    •Длина пароля должны быть от 5 до 32 символов включая.""");

        if (!error.isEmpty()) {
            Modal.show("Ошибка", "Ошибка заполнения полей.", error.toString(), Modal.Icon.error);
            return;
        }

        if (!info.isEmpty()) {
            Modal.show("Информация", "Совет по заполненеию некоторых полей",
                    info.toString(), Modal.Icon.information);
            return;
        }

        String hashPassword = Security.getHash(password);

        DBConnector connector = new DBConnector(DBType, url, DB, username, Consts.password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        String fullName = surname + " " + name + " " + patronymic;

        try {
            String finalTimetable = timetable;
            int idTimetable = Request.selectWhereLimitOne(new String[]{"id"},
                    "timetable",
                    new String[]{"name"},
                    (stmt) -> stmt.setString(1, finalTimetable),
                    (r) -> r.getInt("id")
            ).get(0);

            String finalPosition = position;
            int idPosistion = Request.selectWhereLimitOne(new String[]{"id"},
                    "positions",
                    new String[]{"name"},
                    (stmt) -> stmt.setString(1, finalPosition),
                    (r) -> r.getInt("id")
            ).get(0);

            String finalSex = sex;
            boolean response = Request.insert("employees",
                    new String[]{"id_position", "id_timetable", "fullname", "sex", "age", "telephone", "address"},
                    stmt -> {
                        stmt.setInt(1, idPosistion);
                        stmt.setInt(2, idTimetable);
                        stmt.setString(3, fullName);
                        stmt.setString(4, finalSex);
                        stmt.setString(5, age);
                        stmt.setString(6, telephone);
                        stmt.setString(7, address);
                    }
            );

            if (!response) {
                Modal.show("Ошибка", "Ошибка добавления", "Не удалось добавить заданного работника.", Modal.Icon.error);
                return;
            }

            var idOwnerList = Request.selectWhereLimitOne(new String[]{"id"},
                    "employees",
                    new String[]{"fullname"},
                    (stmt) -> stmt.setString(1, fullName),
                    (r) -> r.getInt("id"));
            if (idOwnerList.isEmpty()) {
                Modal.show("Ошибка", "Ошибка поиска", "Не удалось найти данного работника.", Modal.Icon.error);
                return;
            }
            int idOwner = idOwnerList.get(0);

            response = Request.insert("accounts",
                    new String[]{"login", "password_hash", "id_owner"},
                    stmt -> {
                        stmt.setString(1, login);
                        stmt.setString(2, hashPassword);
                        stmt.setInt(3, idOwner);
                    }
            );

            if (!response) {
                Modal.show("Ошибка", "Ошибка добавления", "Не удалось создать аккаунт сотрудника.", Modal.Icon.error);
                return;
            }

            var idAccountList = Request.selectWhereLimitOne(new String[]{"id"},
                    "accounts",
                    new String[]{"login"},
                    (stmt) -> stmt.setString(1, login),
                    (r) -> r.getInt("id"));
            if (idAccountList.isEmpty()) {
                Modal.show("Ошибка", "Ошибка поиска", "Не удалось добавить аккаунт данного работника.", Modal.Icon.error);
                return;
            }
            int idAccount = idAccountList.get(0);

            String finalRole = role;
            var idRoleList = Request.selectWhereLimitOne(new String[]{"id"},
                    "roles",
                    new String[]{"name"},
                    (stmt) -> stmt.setString(1, finalRole),
                    (r) -> r.getInt("id"));
            if (idRoleList.isEmpty()) {
                Modal.show("Ошибка", "Ошибка поиска", "Не удалось добавить аккаунт данного работника.", Modal.Icon.error);
                return;
            }
            int idRole = idRoleList.get(0);

            response = Request.insert("accounts_roles",
                    new String[]{"id_account", "id_role"},
                    stmt -> {
                        stmt.setInt(1, idAccount);
                        stmt.setInt(2, idRole);
                    }
            );
            if (!response) {
                Modal.show("Ошибка", "Ошибка добавления", "Не удалось добавить роль сотрудника.", Modal.Icon.error);
                return;
            }

            Modal.show("Успех", "Успешное добавление", "Сотрудник зарегистрирован.", Modal.Icon.success);
            clear();
        } catch (ConnectException e) {
            Modal.show("Ошибка", "Ошибка добавления", "Не получить расписание заданного работника.", Modal.Icon.error);
        }
    }

    private void clear() {
        surnameTextField.setText("");
        nameTextField.setText("");
        patronymicTextField.setText("");
        ageTextField.setText("");
        loginTextField.setText("");
        passwordField.setText("");
        telephoneTextField.setText("");
        addressTextField.setText("");
        positionList.setValue("");
        timetableList.setValue("");
        sexList.setValue("");
        roleList.setValue("");
    }

    private void setValuesInList(String tableName, ComboBox<String> list) {
        DBConnector connector = new DBConnector(DBType, url, DB, username, Consts.password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            ((Stage) surnameTextField.getScene().getWindow()).close();
            return;
        }

        List<String> vsemNedovolniiDenisValues;
        try {
            vsemNedovolniiDenisValues = Request.selectWhere(new String[]{"name"},
                    tableName,
                    new String[]{},
                    null,
                    r -> r.getString("name"));
        } catch (Exception e) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            ((Stage) surnameTextField.getScene().getWindow()).close();
            return;
        }

        list.getItems().setAll(vsemNedovolniiDenisValues);
    }

    public void initialize() {
        setValuesInList("positions", positionList);
        new AutoCompleteComboBoxListener<>(positionList);
        setValuesInList("timetable", timetableList);
        new AutoCompleteComboBoxListener<>(timetableList);
        setValuesInList("roles", roleList);
        new AutoCompleteComboBoxListener<>(roleList);
        sexList.getItems().setAll("муж", "жен");
    }
}
