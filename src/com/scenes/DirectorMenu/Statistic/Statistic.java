package com.scenes.DirectorMenu.Statistic;

import com.scenes.AdminMenu.DeleteRole.DeleteRole;
import javafx.stage.Stage;

import static com.assets.services.Consts.openNewWindow;

public class Statistic {
    public static void show() {
        openNewWindow(new Stage(), Statistic.class, "statistic.fxml", "Статистика");
    }

}
