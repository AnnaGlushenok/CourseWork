package com.scenes.DirectorMenu.EmployeeStatistics;

import com.scenes.DirectorMenu.Statistic.Statistic;
import javafx.stage.Stage;

import static com.assets.services.Consts.openNewWindow;

public class EmployeeStatistics {
    public static void show() {
        openNewWindow(new Stage(), EmployeeStatistics.class, "employeeStatistics.fxml", "Статистика");
    }
}
