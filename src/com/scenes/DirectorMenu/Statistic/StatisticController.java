package com.scenes.DirectorMenu.Statistic;

import com.assets.services.AutoCompleteComboBoxListener;
import com.assets.services.Consts;
import com.assets.services.DBConnector;
import com.assets.services.Request;
import com.scenes.Modal.Modal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;

import java.net.ConnectException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;
import java.time.temporal.ChronoUnit.*;
import java.util.concurrent.TimeUnit;

import static com.assets.services.Consts.*;
import static com.assets.services.Consts.username;

public class StatisticController {
    @FXML
    BarChart table;

    @FXML
    public ComboBox<String> positionList;

    public void execute() {
        String position = positionList.getValue();

        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }
//SELECT count FROM `positions` WHERE `name`=
        int count;
        try {
            count = Request.count("positions",
                    new String[]{"name"},
                    stmt -> stmt.setString(1, position),
                    r -> r.getInt("count"));
        } catch (ConnectException e) {
            throw new RuntimeException(e);
        }

        if (count == 0) {
            Modal.show("Ошибка", "Ошибка ввода", "Не удалось найти роль.", Modal.Icon.error);
            return;
        }
//SELECT `fullname` FROM (`positions`
//               join `employees` on employees.id_position=positions.id)
//               where `name`=
        List<String> fullnames;
        try {
            fullnames = Request.getFromTables("positions",
                    new String[]{"fullname"},
                    "join `employees` on employees.id_position=positions.id",
                    new String[]{"name"},
                    stmt -> stmt.setString(1, position),
                    r -> r.getString("fullname"));
        } catch (ConnectException | SQLException e) {
            throw new RuntimeException(e);
        }

        if (fullnames.equals(null)) {
            Modal.show("Ошибка", "Ошибка поиска", "Не удалось найти работников.", Modal.Icon.error);
            return;
        }

        drawSeries(fullnames, table);
    }

    private static void drawSeries(List<String> fullnames, BarChart table) {
        List<XYChart.Series<String, Number>> series = new ArrayList<>();
        series.clear();
        table.getData().clear();
        int size = fullnames.size();
        for (int i = 0; i != size; i++) {
            series.add(new XYChart.Series());
        }

        double[] hours = getWorkHours(fullnames);

        for (int i = 0; i != size; i++)
            series.get(i).getData().add(new XYChart.Data(fullnames.get(i), hours[i]));

        series.forEach(s -> table.getData().add(s));
    }

    private static double[] getWorkHours(List<String> fullnames) {
        DBConnector connector = new DBConnector(DBType, url, DB, username, Consts.password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return null;
        }

        //SELECT `datetime` FROM
        // (`registration` join `employees` on `registration`.`id_employee`=`employees`.`id`)
        // where fullname = 'Жуков Денис Владимирович'
        int size = fullnames.size();
        List<List<Date>> dates = new ArrayList<>(); //сделать массив

        for (int i = 0; i < size; i++) {
            try {
                int finalI = i;
                List<Date> l = Request.getFromTables("registration",
                        new String[]{"datetime"},
                        "join `employees` on  `registration`.`id_employee`=`employees`.`id`",
                        new String[]{"fullname"},
                        stmt -> stmt.setString(1, fullnames.get(finalI)),
                        r -> new Date(r.getTimestamp("datetime").getTime()));
                dates.add(l);
            } catch (ConnectException | SQLException e) {
                throw new RuntimeException(e);
            }
        }

        size = dates.size();

        double[] sum = new double[size];
        for (int i = 0; i < size; i++) {
            List<Date> hours = dates.get(i);
            int countHours = hours.size();
            for (int j = 0; j < countHours - 1; j += 2)
                sum[i] += Math.abs(hours.get(j + 1).getTime() - hours.get(j).getTime()) / (double) (1000 * 60 * 60);
            sum[i] = Math.ceil(sum[i] * 100) / 100;
        }

        return sum;
    }

    private static void setValuesInList(String tableName, ComboBox list) {
        DBConnector connector = new DBConnector(DBType, url, DB, username, Consts.password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        List<String> vsemNedovolniiDenisValues;
        try {
            vsemNedovolniiDenisValues = Request.select("name", tableName);
        } catch (Exception e) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        list.getItems().setAll(vsemNedovolniiDenisValues);
    }

    @FXML
    public void initialize() {
        setValuesInList("positions", positionList);
        new AutoCompleteComboBoxListener<>(positionList);
    }
}
