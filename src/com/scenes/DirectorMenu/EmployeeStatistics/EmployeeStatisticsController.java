package com.scenes.DirectorMenu.EmployeeStatistics;

import com.assets.services.*;
import com.scenes.Modal.Modal;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.assets.services.Consts.*;
import static com.assets.services.Consts.username;

public class EmployeeStatisticsController {
    @FXML
    TableColumn fullName, activeTime, disabilities;
    @FXML
    TableView table;
    @FXML
    ComboBox<String> positionList;

    public void execute() {
        String pos = positionList.getValue();
        if (pos == null) return;
        try {
            display(
                    Request.selectWhere(
                            new String[]{"fullname"},
                            "(employees JOIN positions ON employees.id_position=positions.id)",
                            new String[]{"name"},
                            stmt -> stmt.setString(1, pos),
                            res -> res.getString("fullname")
                    )
            );
        } catch (ConnectException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void display(List<String> fullNames) {
        if (table == null) return;
        table.getItems().clear();

        double[] activeTimeValues = getWorkHours(fullNames, true);
        double[] disabilitiesValues = getWorkHours(fullNames, false);
        List<TableValues> values = new ArrayList<>();
        for (int i = 0; i < activeTimeValues.length; i++)
            values.add(new TableValues(fullNames.get(i), activeTimeValues[i], disabilitiesValues[i]));


        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        activeTime.setCellValueFactory(new PropertyValueFactory<>("activeTime"));
        disabilities.setCellValueFactory(new PropertyValueFactory<>("disabilities"));
        table.getItems().addAll(values);
    }

    private static double[] getWorkHours(List<String> fullnames, boolean presence) {
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
            for (int j = (presence) ? 0 : 1; j < countHours - 1; j += 2)
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
