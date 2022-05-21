package com.scenes.EconomistMenu.Disability;

import com.assets.services.DBConnector;
import com.assets.services.Request;
import com.assets.services.TableValues;
import com.scenes.Modal.Modal;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.ConnectException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static com.assets.services.Consts.*;

public class DisabilityController {
    @FXML
    private TableColumn fullName, reason, startDisabilityDay, endDisabilityDay;
    @FXML
    private TableView table;
    @FXML
    private TextField surnameTextField, nameTextField, patronymicTextField, absenceReasonTextField;
    @FXML
    private DatePicker startDateDatePicker, endDateDatePicker, startDateDatePickerWatch, endDateDatePickerWatch;

    public void fix() {
        String surname = this.surnameTextField.getText().trim();
        String name = this.nameTextField.getText().trim();
        String patronymic = this.patronymicTextField.getText().trim();
        String absenceReason = this.absenceReasonTextField.getText().trim();
        LocalDate startAbsenceDate = this.startDateDatePicker.getValue();
        LocalDate endAbsenceDate = this.endDateDatePicker.getValue();

        StringBuilder error = new StringBuilder();
        if (!surname.matches(nameRegex) || !name.matches(nameRegex) || !patronymic.matches(nameRegex))
            error.append("Неверно введено ФИО.\n");

        if (!absenceReason.matches(sentenceRegex))
            error.append("Неверно введена причина отсутствия.\n");

        if (startAbsenceDate == null || endAbsenceDate == null)
            error.append("Не выбраны даты отсутствия.\n");

        if (!error.isEmpty()) {
            Modal.show("Ошибка", "Ошибка заполнения полей.", error.toString(), Modal.Icon.error);
            return;
        }

        String fullName = surname + " " + name + " " + patronymic;

        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return;
        }

        // String query = "SELECT `id` FROM `employees` WHERE `fullname`=? LIMIT 1";
        int id;
        try {
            List<Integer> idList = Request.selectWhereLimitOne(new String[]{"id"},
                    "employees",
                    new String[]{"fullname"},
                    stmt -> stmt.setString(1, fullName),
                    r -> r.getInt("id"));

            if (idList.isEmpty()) {
                Modal.show("Ошибка", "Ошибка поиска.", "Не удалось найти id сотрудника.", Modal.Icon.error);
                return;
            }
            id = idList.get(0);

            boolean response = Request.insert("disabilities",
                    new String[]{"id_employee", "reason", "start_date", "end_date"},
                    stmt -> {
                        stmt.setInt(1, id);
                        stmt.setString(2, absenceReason);
                        stmt.setDate(3, Date.valueOf(startAbsenceDate));
                        stmt.setDate(4, Date.valueOf(endAbsenceDate));
                    });
            if (response)
                Modal.show("Успех", "Успешное добавление", "Сотрудник и причина его отсутствия записаны.", Modal.Icon.success);
            else
                Modal.show("Ошибка", "Ошибка добавления", "Не удалось добавить причину отсутствия заданного работника.", Modal.Icon.error);
        } catch (SQLException | ConnectException e) {
            Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
        }
    }

    public void changeWrite() {
        change(startDateDatePicker, endDateDatePicker);
    }

    public void changeWatch() {
        change(startDateDatePickerWatch, endDateDatePickerWatch);
    }

    public void change(DatePicker startDatePicker, DatePicker endDatePicker) {
        LocalDate startAbsenceDate = startDatePicker.getValue();
        endDatePicker.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date.compareTo(startAbsenceDate) < 0) {
                    setDisable(true);
                    setStyle("-fx-background-color: #cccccc;");
                }
            }
        });
        endDatePicker.setValue(startAbsenceDate);
    }

    public void find() {
        display();
    }

    public void display() {
        if (table == null) return;
        table.getItems().clear();

        List<TableValues> values = getElements();
        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        reason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        startDisabilityDay.setCellValueFactory(new PropertyValueFactory<>("startDisabilityDay"));
        endDisabilityDay.setCellValueFactory(new PropertyValueFactory<>("endDisabilityDay"));
        table.getItems().addAll(values);
    }

    public List<TableValues> getElements() {
        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return null;
        }

        LocalDate startAbsenceDate = this.startDateDatePickerWatch.getValue();
        LocalDate endAbsenceDate = this.endDateDatePickerWatch.getValue();

        if (startAbsenceDate == null || endAbsenceDate == null) {
            Modal.show("Ошибка", "Ошибка заполнения полей.", "Не выбраны даты.", Modal.Icon.error);
            return null;
        }

        try {
            String query = "SELECT `fullname`,`reason`,`start_date`,`end_date` " +
                    "FROM (`disabilities` JOIN `employees` on disabilities.id_employee=employees.id) " +
                    "where `start_date` BETWEEN ? AND ?";
            return connector.getObjects(query,
                    stmt -> {
                        stmt.setDate(1, Date.valueOf(startAbsenceDate));
                        stmt.setDate(2, Date.valueOf(endAbsenceDate));
                    },
                    r -> TableValues.createDisabilityValues(r.getString("fullname"),
                            r.getString("reason"),
                            r.getDate("start_date"),
                            r.getDate("end_date")));
        } catch (SQLException e) {
            Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return null;
        }
    }

    public void initialize() {
        table.setPlaceholder(new Label("Не выбраны даты."));
    }
}
