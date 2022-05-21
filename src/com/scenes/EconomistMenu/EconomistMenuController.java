package com.scenes.EconomistMenu;

import com.scenes.EconomistMenu.Disability.Disability;
import com.scenes.HumanResourcesDepartmentMenu.Position.Position;
import com.scenes.EconomistMenu.TimeTable.TimeTable;
import com.scenes.EconomistMenu.Vacations.Vacation;

public class EconomistMenuController {
    public void showDisability() {
        Disability.show();
    }

    public void showTimeTable() {
        TimeTable.show();
    }

    public void showVacation() {
        Vacation.show();
    }
}
