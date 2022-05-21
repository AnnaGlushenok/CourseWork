package com.scenes.DirectorMenu.Vacations;

import com.assets.services.DBConnector;
import com.assets.services.Request;
import com.assets.services.TableValues;
import com.scenes.Modal.Modal;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import static com.assets.services.Consts.*;

public class VacationsController {
    @FXML
    private TableColumn fullName, position, startVacationDay, endVacationDay;
    @FXML
    private TabPane tab;
    @FXML
    private TableView table;
    private int selectedPage;

    public void display(Event event) {

        if (table == null) return;
        table.getItems().clear();
        Tab tab = (Tab) event.getTarget();
        selectedPage = this.tab.getTabs().indexOf(tab) + 1;

        if (selectedPage == 0)
            return;

        List<TableValues> values = getElements();
        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        startVacationDay.setCellValueFactory(new PropertyValueFactory<>("startVacationDay"));
        position.setCellValueFactory(new PropertyValueFactory<>("position"));
        endVacationDay.setCellValueFactory(new PropertyValueFactory<>("endVacationDay"));
        table.getItems().addAll(values);
    }

    public List<TableValues> getElements() {
        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
        if (!connector.testConnection()) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return null;
        }

        try {
            return Request.selectWhere(new String[]{"fullname", "name", "start_date", "end_date"},
                    "(vacations JOIN employees on employees.id=vacations.id_employee\n" +
                            "JOIN positions on positions.id=employees.id_position)",
                    new String[]{"month(`start_date`)"},
                    stmt -> stmt.setInt(1, selectedPage),
                    r -> new TableValues(
                            r.getString("fullname"),
                            r.getString("name"),
                            r.getDate("start_date"),
                            r.getDate("end_date")
                    ));
        } catch (ConnectException | SQLException e) {
            Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
            return null;
        }
    }

    public void initialize() {
        table.setPlaceholder(new Label("Нет отдыхающих сотрудников в этом месяце."));
    }

}
