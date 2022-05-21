package com.scenes.HumanResourcesDepartmentMenu.DeleteEmployee;

import com.assets.services.AutoCompleteComboBoxListener;
import com.assets.services.DBConnector;
import com.assets.services.Request;
import com.scenes.Modal.Modal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.List;

import static com.assets.services.Consts.*;

public class DeleteEmployeeController {
    @FXML
    private Button deleteBtn;
    @FXML
    private TextField payrollNumber;

    public void delete() {
        String number = payrollNumber.getText().trim();

        if (!number.matches("^\\d{1,11}$")) {
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
                    stmt -> stmt.setInt(1, Integer.parseInt(number)),
                    r -> r.getInt("count"));


            if (exist == 0) {
                Modal.show("Ошибка", "Ошибка поиска.", "Не удалось найти данный табельный номер.", Modal.Icon.error);
                return;
            }

            Request.delete("employees",
                    new String[]{"id"},
                    stmt -> stmt.setInt(1, Integer.parseInt(number)));
            Modal.show("Успех", "Успешное добавление.", "Сотрудник удалён.", Modal.Icon.success);
            payrollNumber.setText("");
        } catch (ConnectException e) {
            Modal.show("Ошибка", "Ошибка подключения", "Не удалосьподключиться к базе данных.", Modal.Icon.error);
        }
    }

//    public void initialize(){
//        DBConnector connector = new DBConnector(DBType, url, DB, username, password);
//        if (!connector.testConnection()) {
//            Modal.show("Ошибка", "Ошибка подключения", "Не удалось подключиться к базе данных.", Modal.Icon.error);
//            ((Stage)roleList.getScene().getWindow()).close();
//            return;
//        }
//
////        String queryRole = "SELECT `name` FROM `roles`";
////        String queryLogin = "SELECT `login` FROM `accounts`";
//        List<String> roles;
//        List<String> logins;
//        try {
//            roles = Request.selectWhere(new String[]{"name"},
//                    "roles",
//                    new String[]{},
//                    null,
//                    r -> r.getString("name"));
//
//            logins = Request.selectWhere(new String[]{"login"},
//                    "accounts",
//                    new String[]{},
//                    null,
//                    r -> r.getString("login"));
//            if (roles.isEmpty()) {
//                Modal.show("Ошибка", "Ошибка поиска.", "Не удалось найти роли.", Modal.Icon.error);
//                ((Stage)roleList.getScene().getWindow()).close();
//                return;
//            }
//            if (logins.isEmpty()) {
//                Modal.show("Ошибка", "Ошибка поиска.", "Не удалось найти аккаунты.", Modal.Icon.error);
//                ((Stage)roleList.getScene().getWindow()).close();
//                return;
//            }
//        } catch (SQLException | ConnectException e) {
//            Modal.show("Ошибка", "Ошибка подключения.", "Не удалось подключиться к базе данных.", Modal.Icon.error);
//            ((Stage)roleList.getScene().getWindow()).close();
//            return;
//        }
//        loginList.getItems().addAll(logins);
//        roleList.getItems().addAll(roles);
//
//        new AutoCompleteComboBoxListener<>(loginList);
//        new AutoCompleteComboBoxListener<>(roleList);
//    }
}
