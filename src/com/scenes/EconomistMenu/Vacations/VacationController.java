package com.scenes.EconomistMenu.Vacations;

import com.assets.services.DBConnector;
import com.assets.services.Request;
import com.assets.services.TableValues;
import com.scenes.Modal.Modal;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.ConnectException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.assets.services.Consts.*;

public class VacationController {
    @FXML
    private DatePicker startVacationDayPicker, endVacationDayPicker;
    @FXML
    private TableColumn fullName, startVacationDay, endVacationDay, confirm, fullNameWatch, startVacationDayWatch, endVacationDayWatch;
    @FXML
    private TableView tableConfirm, tableWatch;

    public void confirm() throws SQLException {
        ObservableList<TableValues> rows = tableConfirm.getItems();
        StringBuilder query = new StringBuilder();
        //UPDATE `vacations`
        // SET `id_employee`='[value-1]',`start_date`='[value-2]',`end_date`='[value-3]',`confirmed`='[value-4]'
        // WHERE `id_employee`=
        for (var row : rows) {
            if (row.getConfirm().isSelected()) {//CONFIRMED
                query.append(String.format("UPDATE `vacations` " +
                                "SET `confirmed`='%s' " +
                                "WHERE `id_employee`='%s' and " +
                                "`start_date`='%s' and " +
                                "`end_date`='%s';",
                        "1",
                        fullNameID.get(row.getFullName()),
                        row.getStartVacationDay(),
                        row.getEndVacationDay()));
            } else {
                query.append(String.format("DELETE FROM `vacations` WHERE `confirmed`='%s' AND " +
                                "`start_date`='%s' and " +
                                "`end_date`='%s';",
                        "0",
                        row.getStartVacationDay(),
                        row.getEndVacationDay()));
            }
        }
        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }
        Connection connection = DriverManager.getConnection(connector.fullURL() + "?allowMultiQueries=true", username, password);
        Statement state = connection.createStatement();
        state.execute(query.toString());
        connection.close();
        tableConfirm.getItems().clear();
        Modal.show("Успех", "Отпуска одобрены", "Отпуски подтверждены.", Modal.Icon.success);
    }

    public void displayWatchTable() {
        if (tableWatch == null) return;
        tableWatch.getItems().clear();

        List<TableValues> values = getWatchElements();
        fullNameWatch.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        startVacationDayWatch.setCellValueFactory(new PropertyValueFactory<>("startVacationDay"));
        endVacationDayWatch.setCellValueFactory(new PropertyValueFactory<>("endVacationDay"));

        tableWatch.getItems().addAll(values);
    }

    public void displayConfirmTable() {
        if (tableConfirm == null) return;
        tableConfirm.getItems().clear();

        List<TableValues> values = getConfirmationElements();
        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        startVacationDay.setCellValueFactory(new PropertyValueFactory<>("startVacationDay"));
        endVacationDay.setCellValueFactory(new PropertyValueFactory<>("endVacationDay"));
        confirm.setCellValueFactory(new PropertyValueFactory<>("confirm"));
        tableConfirm.getItems().addAll(values);
    }

    private Map<String, Integer> fullNameID = new HashMap<>();

    public void find() {
        displayWatchTable();
    }

    public List<TableValues> getWatchElements() {
        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return null;
        }

        LocalDate startVacationDate = this.startVacationDayPicker.getValue();
        LocalDate endVacationDate = this.endVacationDayPicker.getValue();

        if (startVacationDate == null || endVacationDate == null) {
            Modal.show("Ошибка", "Ошибка заполнения полей.", "Не выбраны даты.", Modal.Icon.error);
            return null;
        }

        try {
            String query = "SELECT `fullname`,`start_date`,`end_date` " +
                    "FROM (`vacations` JOIN `employees` on vacations.id_employee=employees.id) " +
                    "where `start_date` BETWEEN ? AND ?";
            return connector.getObjects(query,
                    stmt -> {
                        stmt.setDate(1, Date.valueOf(startVacationDate));
                        stmt.setDate(2, Date.valueOf(endVacationDate));
                    },
                    r -> new TableValues(
                            r.getString("fullname"),
                            r.getDate("start_date"),
                            r.getDate("end_date")));
        } catch (SQLException e) {
            Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return null;
        }
    }

    public List<TableValues> getConfirmationElements() {
        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return null;
        }

        try {
            String query = "SELECT `id_employee`,`fullname`,`start_date`,`end_date` " +
                    "FROM (`vacations` JOIN `employees` on vacations.id_employee=employees.id) " +
                    "where `confirmed`=?";
            return connector.getObjects(query,
                    stmt -> stmt.setInt(1, 0),
                    r -> {
                        String fullName = r.getString("fullname");
                        fullNameID.put(fullName, r.getInt("id_employee"));
                        return new TableValues(
                                fullName,
                                r.getDate("start_date"),
                                r.getDate("end_date")
                        );
                    });
        } catch (SQLException e) {
            Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return null;
        }
    }

    public void change() {
        LocalDate startAbsenceDate = startVacationDayPicker.getValue();
        endVacationDayPicker.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date.compareTo(startAbsenceDate) < 0) {
                    setDisable(true);
                    setStyle("-fx-background-color: #cccccc;");
                }
            }
        });
        endVacationDayPicker.setValue(startAbsenceDate);
    }

    public void initialize() {
        displayConfirmTable();
        tableConfirm.setPlaceholder(new Label("Нет запросов на отпуск."));
        tableWatch.setPlaceholder(new Label("Нет отпусков."));
    }
}
