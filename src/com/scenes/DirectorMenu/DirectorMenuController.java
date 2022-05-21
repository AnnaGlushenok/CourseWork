package com.scenes.DirectorMenu;

import com.scenes.DirectorMenu.EmployeeStatistics.EmployeeStatistics;
import com.scenes.DirectorMenu.Statistic.Statistic;
import com.scenes.DirectorMenu.Vacations.Vacations;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DirectorMenuController {
    @FXML
    public Button positionsBtn, vacationsBtn, statisticBtn;

    public void showPositions() {
        EmployeeStatistics.show();
    }

    public void showVacations() {
        Vacations.show();
    }

    public void showStatistic() {
        Statistic.show();
    }
}
