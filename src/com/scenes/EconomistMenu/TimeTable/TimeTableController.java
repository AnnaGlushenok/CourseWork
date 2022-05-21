package com.scenes.EconomistMenu.TimeTable;

import com.App;
import com.assets.services.AutoCompleteComboBoxListener;
import com.assets.services.DBConnector;
import com.assets.services.Request;
import com.scenes.Modal.Modal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.ConnectException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static com.assets.services.Consts.*;

public class TimeTableController {
    @FXML
    private TextField workDays, weekendDays, personnelNumber, fullname;
    @FXML
    private ComboBox<String> timetableList;
    @FXML
    private DatePicker timetablePicker;

    public void add() {
        String countWorkDays = workDays.getText();
        String countWeekend = weekendDays.getText();

        if (countWeekend.equals("") && countWorkDays.equals("")) {
            Modal.show("Ошибка", "Ошибка заполнения полей.", "Не заполнены поля.", Modal.Icon.error);
            return;
        }

        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        String timetable = String.join("/", countWorkDays, countWeekend);

        boolean request;
        try {
            request = Request.insert("timetable",
                    new String[]{"name"},
                    stmt -> stmt.setString(1, timetable));
            if (request) {
                Modal.show("Успех", "Успешное выполнение.", "Новое расписание добавлено.", Modal.Icon.success);
                workDays.setText("");
                weekendDays.setText("");
            }
        } catch (ConnectException e) {
            Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
        }
    }

    public void set() {
        String personnelNumber = this.personnelNumber.getText().trim();
        String timetable = this.timetableList.getValue();

        if (!personnelNumber.matches("^\\d{1,11}$")) {
            Modal.show("Ошибка", "Ошибка заполнения полей.", "Неверно заполнено поле", Modal.Icon.error);
            return;
        }

        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        int exist;
        try {
            exist = Request.count("employees",
                    new String[]{"id"},
                    stmt -> stmt.setInt(1, Integer.parseInt(personnelNumber)),
                    r -> r.getInt("count"));

            if (exist == 0) {
                Modal.show("Ошибка", "Ошибка поиска.", "Не удалось найти данный табельный номер.", Modal.Icon.error);
                return;
            }

            List<Integer> idList = Request.selectWhereLimitOne(new String[]{"id"},
                    "timetable",
                    new String[]{"name"},
                    stmt -> stmt.setString(1, timetable),
                    r -> r.getInt("id"));
            if (idList.isEmpty()) {
                Modal.show("Ошибка", "Ошибка поиска", "Не удалось найти расписание.", Modal.Icon.error);
                return;
            }
            int id = idList.get(0);

            boolean request = Request.update("employees",
                    "id_timetable",
                    new String[]{"id"},
                    stmt -> {
                        stmt.setInt(1, id);
                        stmt.setString(2, personnelNumber);
                    });
            if (request) {
                Modal.show("Успех", "Успешное установление.", "Расписание сотрудника установлено.", Modal.Icon.success);
                this.personnelNumber.setText("");
                this.timetableList.setValue("");
            } else
                Modal.show("Ошибка", "Ошибка добавления", "Не удалось установаить роль.", Modal.Icon.error);
        } catch (ConnectException |SQLException e) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
        }
    }

    public void find() {
        String fullname = this.fullname.getText();

        if (!fullname.matches(sentenceRegex)) {
            Modal.show("Ошибка", "Ошибка ввода", "Не верно введено ФИО.", Modal.Icon.error);
            return;
        }

        try {
            //SELECT `name` FROM
            // (`timetable` join `employees` on timetable.id=employees.id_timetable) WHERE fullname=?
            List<String> timetableRequest = Request.selectWhereLimitOne(new String[]{"name"},
                    "`timetable` join `employees` on timetable.id=employees.id_timetable",
                    new String[]{"fullname"},
                    stmt -> stmt.setString(1, fullname),
                    r -> r.getString("name"));
            if (timetableRequest.isEmpty()) {
                Modal.show("Ошибка", "Ошибка поиска.", "Не удалось найти расписание сотрудника.", Modal.Icon.error);
                return;
            }
           String timetable = timetableRequest.get(0);
            int countWorkDays = Integer.parseInt(timetable.split("/")[0]);
            int countWeekend = Integer.parseInt(timetable.split("/")[1]);

            setDisable(countWorkDays, countWeekend);
        } catch (SQLException | ConnectException e) {
            Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
        }
    }

    private int workDay, weekendDay;

    private void setDisable(int countWorkDays, int countWeekend) {
        workDay = countWorkDays;
        weekendDay = countWeekend;
        timetablePicker.setDayCellFactory(p -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (workDay > 0) {
                    setStyle("-fx-background-color: #ffa1a5;");
                    workDay--;
                    if (workDay == 0)
                        weekendDay = countWeekend;
                } else {
                    setStyle("-fx-background-color: #a1ffa2;");
                    weekendDay--;
                    if (weekendDay == 0)
                        workDay = countWorkDays;
                }
            }
        });
    }

    public void initialize() {
        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        new AutoCompleteComboBoxListener<>(timetableList);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            ((Stage) timetableList.getScene().getWindow()).close();
            return;
        }

        try {
            List<String> timetables = Request.selectWhere(new String[]{"name"},
                    "timetable",
                    new String[]{},
                    null,
                    r -> r.getString("name"));
            timetableList.getItems().addAll(timetables);
        } catch (ConnectException | SQLException e) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            ((Stage) timetableList.getScene().getWindow()).close();
        }
    }
}
