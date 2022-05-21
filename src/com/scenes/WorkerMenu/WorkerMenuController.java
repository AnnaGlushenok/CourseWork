package com.scenes.WorkerMenu;

import com.scenes.WorkerMenu.TimeTable.Timetable;
import com.scenes.WorkerMenu.Vacations.Vacation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class WorkerMenuController {
    @FXML
    private Button vacationBtn, timetableBtn;

    public void showVacation(){
        Vacation.show();
    }

    public void showTimetable() {
        Timetable.show();
    }
}
