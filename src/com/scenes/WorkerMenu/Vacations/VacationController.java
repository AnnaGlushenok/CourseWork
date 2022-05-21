package com.scenes.WorkerMenu.Vacations;

import com.App;
import com.assets.services.DBConnector;
import com.assets.services.Request;
import com.scenes.Modal.Modal;
import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.net.ConnectException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static com.assets.services.Consts.*;

public class VacationController {
    @FXML
    private DatePicker vacationStartCalendar, vacationEndCalendar;

    @FXML
    protected void request() {
        LocalDate startVacationDate = vacationStartCalendar.getValue();
        LocalDate endVacationDate = vacationEndCalendar.getValue();

        if (vacationEndCalendar == null || endVacationDate == null) {
            Modal.show("Ошибка", "Ошибка заполнения", "Не выбрана дата.", Modal.Icon.error);
            return;
        }

        try {
            Request.insert("vacations",
                    new String[]{"id_employee", "start_date", "end_date"},
                    stmt -> {
                        stmt.setInt(1, App.getID());
                        stmt.setDate(2, Date.valueOf(startVacationDate));
                        stmt.setDate(3, Date.valueOf(endVacationDate));
                    });

            Modal.show("Успех", "Успешное добавление", "Отпуск записан.", Modal.Icon.success);
            ((Stage) vacationStartCalendar.getScene().getWindow()).close();
        } catch (ConnectException e) {
            Modal.show("Ошибка", "Ошибка запроса", "Не удалось запросить отпуск.", Modal.Icon.error);
        }
    }

    public long getNumberVacationDays() {
        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            return -1;
        }

        Integer countDays = 0;
        try {
            List<Integer> days = Request.selectWhere(new String[]{"number_vacation_days"},
                    "(`employees` " +
                            "JOIN `accounts` ON `accounts`.`id_owner`=`employees`.`id` " +
                            "JOIN `positions` ON `employees`.`id_position`=`positions`.`id`) ",
                    new String[]{"login"},
                    stmt -> stmt.setString(1, App.getLogin()),
                    r -> r.getInt("number_vacation_days"));
            if (days.isEmpty()) {
                return -1;
            }
            countDays = days.get(0);
        } catch (SQLException | ConnectException e) {
            e.printStackTrace();
            return -1;
        }
        return countDays.longValue();
    }

    public void change() {
        long days = getNumberVacationDays();
        if (days == -1) {
            Modal.show("Ошибка", "Ошибка получения", "Не удалось получить количество выходных дней.", Modal.Icon.error);
            return;
        }

        LocalDate startVacationDate = vacationStartCalendar.getValue();
        vacationEndCalendar.setDayCellFactory(p -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.compareTo(startVacationDate) < 0 || startVacationDate.plusDays(days).compareTo(date) <= 0
                        || list.stream().anyMatch((dates) -> dates[0].compareTo(date) <= 0 && dates[1].compareTo(date) >= 0)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #cccccc;");
                }
            }
        });
    }

    private void setDisable(DatePicker calendar) {
        calendar.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || list.stream().anyMatch((dates) -> dates[0].compareTo(date) <= 0 && dates[1].compareTo(date) >= 0)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #cccccc;");
                }
            }
        });
    }


    private static List<LocalDate[]> list;

    public void initialize() {
        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        try {
            list = Request.selectWhere(new String[]{"start_date", "end_date"},
                    "vacations",
                    new String[]{"confirmed"},
                    stmt -> stmt.setBoolean(1, true),
                    r -> new LocalDate[]{
                            new java.sql.Date(r.getDate("start_date").getTime()).toLocalDate(),
                            new java.sql.Date(r.getDate("end_date").getTime()).toLocalDate()
                    }
            );
        } catch (ConnectException | SQLException e) {
            Modal.show("Ошибка", "Ошибка поиска", "Не удалось найти отпуска.", Modal.Icon.error);
            return;
        }
        setDisable(vacationStartCalendar);
        setDisable(vacationEndCalendar);
    }
}
