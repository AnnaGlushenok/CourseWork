package com.scenes.EconomistMenu.TimeTable;

import com.scenes.EconomistMenu.Vacations.Vacation;
import javafx.stage.Stage;

import static com.assets.services.Consts.openNewWindow;

public class TimeTable {
    public static void show() {
        openNewWindow(new Stage(), TimeTable.class, "timeTable.fxml", "Расписание");
    }
}
