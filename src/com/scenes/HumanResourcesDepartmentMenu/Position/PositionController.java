package com.scenes.HumanResourcesDepartmentMenu.Position;

import com.assets.services.DBConnector;
import com.assets.services.Request;
import com.scenes.Modal.Modal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.net.ConnectException;

import static com.assets.services.Consts.*;

public class PositionController {
    @FXML
    private TextField name, countVacationDays, salary, hourlyQuota;

    public void add() {
        String name = this.name.getText().trim();
        String countVacationDays = this.countVacationDays.getText().trim();
        String salary = this.salary.getText().trim();
        String hourlyQuota = this.hourlyQuota.getText().trim();

        StringBuilder error = new StringBuilder();
        if (!name.matches(sentenceRegex))
            error.append("Неверно введена должность.\n");

        if (!countVacationDays.matches("\\d{1,2}"))
            error.append("Неверно введено количество отпускных дней.\n");

        if (!salary.matches("\\d{2,6}.\\d{1,2}"))
            error.append("Неверно введена заработная плата.\n");

        if (!hourlyQuota.matches("\\d{2,3}"))
            error.append("Неверно введена месячная норма.\n");

        if (!error.isEmpty()) {
            Modal.show("Ошибка", "Ошибка заполнения", error.toString(), Modal.Icon.error);
            return;
        }

        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        try {
            boolean request = Request.insert("positions",
                    new String[]{"number_vacation_days", "name", "salary", "monthly_rate"},
                    stmt -> {
                        stmt.setString(1, countVacationDays);
                        stmt.setString(2, name);
                        stmt.setString(3, salary);
                        stmt.setString(4, hourlyQuota);
                    });

            if (!request) {
                Modal.show("Ошибка", "Ошибка подключения", "Не удалосьподключиться к базе данных.", Modal.Icon.error);
                return;
            }
            Modal.show("Успех", "Успешное добавление", "Новая должность добавлена.", Modal.Icon.success);
            this.name.setText("");
            this.countVacationDays.setText("");
            this.salary.setText("");
            this.hourlyQuota.setText("");
        } catch (ConnectException e) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалосьподключиться к базе данных.", Modal.Icon.error);
        }
    }
}
