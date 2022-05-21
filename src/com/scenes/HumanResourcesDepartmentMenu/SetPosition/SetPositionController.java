package com.scenes.HumanResourcesDepartmentMenu.SetPosition;

import com.assets.services.AutoCompleteComboBoxListener;
import com.assets.services.DBConnector;
import com.assets.services.Request;
import com.scenes.Modal.Modal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import static com.assets.services.Consts.*;

public class SetPositionController {
    @FXML
    private ComboBox<String> personnelNumberList, positionList;

    public void set() {
        String personnelNumber = personnelNumberList.getValue();
        String position = positionList.getValue();

        personnelNumber = personnelNumber == null ? "" : personnelNumber.trim();
        position = position == null ? "" : position.trim();

        StringBuilder error = new StringBuilder();
        if (!personnelNumber.matches("\\d{1,5}"))
            error.append("Неверно введён табельный номер.\n");
        if (!position.matches(sentenceRegex))
            error.append("Неверно введена должность.\n");

        if (!error.isEmpty()) {
            Modal.show("Ошибка", "Ошибка ввода", error.toString(), Modal.Icon.error);
            return;
        }

        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        String finalPersonnelNumber = personnelNumber;
        String finalPosition = position;
        try {
            List<Integer> idList = Request.selectWhereLimitOne(new String[]{"id"},
                    "positions",
                    new String[]{"name"},
                    stmt -> stmt.setString(1, finalPosition),
                    r -> r.getInt("id"));

            if (idList.isEmpty()) {
                Modal.show("Ошибка", "Ошибка поиска", "Не удалось найти роль.", Modal.Icon.error);
                return;
            }

            boolean request = Request.update("employees",
                    "id_position",
                    new String[]{"id"},
                    stmt -> {
                        stmt.setInt(1, idList.get(0));
                        stmt.setInt(2, Integer.parseInt(finalPersonnelNumber));
                    });

            if (!request) {
                Modal.show("Ошибка", "Ошибка выполнения запроса", "Не удалось установить роль.", Modal.Icon.error);
                return;
            }
            Modal.show("Успех", "Должность установлена", "Установление должности прошло успешно.", Modal.Icon.success);
            personnelNumberList.setValue(null);
            positionList.setValue(null);
        } catch (ConnectException | SQLException e) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
        }
    }

    public void initialize() {
        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            ((Stage) personnelNumberList.getScene().getWindow()).close();
            return;
        }

        List<String> personnelNumbers;
        List<String> positions;
        try {
            personnelNumbers = Request.select("id", "employees");
            positions = Request.select("name", "positions");

            if (personnelNumbers.isEmpty()) {
                Modal.show("Ошибка", "Ошибка поиска.", "Не удалось найти табельные номера сотрудников.", Modal.Icon.error);
                ((Stage) personnelNumberList.getScene().getWindow()).close();
                return;
            }
            if (positions.isEmpty()) {
                Modal.show("Ошибка", "Ошибка поиска.", "Не удалось найти должности.", Modal.Icon.error);
                ((Stage) positionList.getScene().getWindow()).close();
                return;
            }
        } catch (SQLException | ConnectException e) {
            Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            ((Stage) positionList.getScene().getWindow()).close();
            return;
        }
        personnelNumberList.getItems().addAll(personnelNumbers);
        positionList.getItems().addAll(positions);

        new AutoCompleteComboBoxListener<>(personnelNumberList);
        new AutoCompleteComboBoxListener<>(positionList);
    }
}
