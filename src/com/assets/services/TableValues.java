package com.assets.services;

import javafx.scene.control.CheckBox;

import java.sql.Date;

public class TableValues {
    private String fullName, position, startVacationDay, endVacationDay;
    private String reason, startDisabilityDay, endDisabilityDay;

    public String getFullName() {
        return fullName;
    }

    public String getPosition() {
        return position;
    }

    public String getStartVacationDay() {
        return startVacationDay;
    }

    public String getEndVacationDay() {
        return endVacationDay;
    }

    public String getReason() {
        return reason;
    }

    public String getStartDisabilityDay() {
        return startDisabilityDay;
    }

    public String getEndDisabilityDay() {
        return endDisabilityDay;
    }

    public TableValues(String fullName, String position, Date startVacationDay, Date endVacationDay) {
        this.fullName = fullName;
        this.position = position;
        this.startVacationDay = startVacationDay.toString();
        this.endVacationDay = endVacationDay.toString();
    }

    private double activeTime, disabilities;

    public Double getActiveTime() {
        return activeTime;
    }

    public Double getDisabilities() {
        return disabilities;
    }

    public TableValues(String fullName, double activeTime, double disabilities) {
        this.fullName = fullName;
        this.activeTime = activeTime;
        this.disabilities = disabilities;
    }

    private CheckBox confirm;

    public CheckBox getConfirm() {
        return confirm;
    }

    public TableValues(String fullName, Date startVacationDay, Date endVacationDay) {
        this.fullName = fullName;
        this.startVacationDay = startVacationDay.toString();
        this.endVacationDay = endVacationDay.toString();
        this.confirm = new CheckBox();
        this.confirm.setSelected(false);
    }

    private TableValues() {

    }

    public static TableValues createDisabilityValues(String fullName, String reason, Date startDisabilityDay, Date endDisabilityDay) {
        TableValues table = new TableValues();
        table.fullName = fullName;
        table.reason = reason;
        table.startDisabilityDay = startDisabilityDay.toString();
        table.endDisabilityDay = endDisabilityDay.toString();
        return table;
    }
}