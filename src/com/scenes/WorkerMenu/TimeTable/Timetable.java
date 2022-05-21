package com.scenes.WorkerMenu.TimeTable;

import javafx.stage.Stage;

import static com.assets.services.Consts.openNewWindow;

public class Timetable {
    public static void show() {
        openNewWindow(new Stage(), Timetable.class, "timetable.fxml", "Расписание");
    }
}
