package com.scenes.WorkerMenu.TimeTable;

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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.assets.services.Consts.*;

public class TimetableController {
    @FXML
    private DatePicker timeTableCalendar;

    public void initialize() {
        String timetable;
        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            ((Stage)timeTableCalendar.getScene().getWindow()).close();
            return;
        }
        try {
            List<String> t = Request.selectWhere(new String[]{"name"},
                    "(`timetable` " +
                            "JOIN `employees` on `employees`.`id_timetable`=`timetable`.`id` " +
                            "JOIN `accounts` on `accounts`.`id_owner`=`employees`.`id`) ",
                    new String[]{"accounts`.`login"},
                    stmt -> stmt.setString(1, App.getLogin()),
                    r -> r.getString("name"));
            if (t.isEmpty()) {
                Modal.show("Ошибка", "Ошибка поиска", "Не удалось найти расписание сотрудника.", Modal.Icon.error);
                ((Stage)timeTableCalendar.getScene().getWindow()).close();
                return;
            }
            timetable = t.get(0);
        } catch (ConnectException | SQLException e) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            ((Stage)timeTableCalendar.getScene().getWindow()).close();
            return;
        }

        int countWorkDays = Integer.parseInt(timetable.split("/")[0]);
        int countWeekend = Integer.parseInt(timetable.split("/")[1]);

        var vacations = getVacationDates();
        if (vacations == null) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            ((Stage)timeTableCalendar.getScene().getWindow()).close();
            return;
        }
        setDisable(countWorkDays, countWeekend, vacations);
    }

    public List<LocalDate[]> getVacationDates() {
        try {
            return Request.selectWhere(new String[]{"start_date", "end_date"},
                    "vacations",
                    new String[]{"id_employee", "confirmed"},
                    stmt -> {
                        stmt.setInt(1, App.getID());
                        stmt.setBoolean(2, true);
                    },
                    r -> new LocalDate[]{
                            Instant.ofEpochMilli(r.getDate("start_date").getTime())
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate(),
                            Instant.ofEpochMilli(r.getDate("end_date").getTime())
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                    }
            );
        } catch (ConnectException | SQLException e) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
        }
        return null;
    }

    private int workDay, weekendDay;

    private void setDisable(int countWorkDays, int countWeekend, List<LocalDate[]> dates) {
        workDay = countWorkDays;
        weekendDay = countWeekend;
        timeTableCalendar.setDayCellFactory(p -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                boolean vacation = dates.stream().anyMatch((d) -> d[0].compareTo(date) <= 0 && d[1].compareTo(date) >= 0);
                if (workDay > 0) {
                    setStyle("-fx-background-color: #ff4d55;");
                    workDay--;
                    if (workDay == 0)
                        weekendDay = countWeekend;
                } else {
                    setStyle("-fx-background-color: #56fc47;");
                    weekendDay--;
                    if (weekendDay == 0)
                        workDay = countWorkDays;
                }
                if (vacation) {
                    setStyle("-fx-background-color: #56fc47;");
                }
            }
        });
    }
}
