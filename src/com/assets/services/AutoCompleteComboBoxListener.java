package com.assets.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

    private ComboBox comboBox;
    private StringBuilder sb;
    private ObservableList<T> data;
    private boolean moveCaretToPos = false;
    private int caretPos;

    public AutoCompleteComboBoxListener(final ComboBox comboBox) {
        this.comboBox = comboBox;
        sb = new StringBuilder();
        data = comboBox.getItems();
        this.comboBox.setOnKeyPressed(t -> comboBox.hide());
        this.comboBox.setOnKeyReleased(AutoCompleteComboBoxListener.this);
    }

    @Override
    public void handle(KeyEvent event) {
        var key = event.getCode();
        switch (key) {
            case UP -> {
                caretPos = -1;
                moveCaret(comboBox.getEditor().getText().length());
                return;
            }
            case DOWN -> {
                if (!comboBox.isShowing())
                    comboBox.show();

                caretPos = -1;
                moveCaret(comboBox.getEditor().getText().length());
                return;
            }
            case BACK_SPACE, DELETE -> {
                moveCaretToPos = true;
                caretPos = comboBox.getEditor().getCaretPosition();
            }
        }

        if (key == KeyCode.RIGHT || key == KeyCode.LEFT || key == KeyCode.HOME
                || key == KeyCode.END || key == KeyCode.TAB || key == KeyCode.CONTROL
                || key == KeyCode.SHIFT || key == KeyCode.ENTER || key == KeyCode.CAPS
                || event.isControlDown() || event.isShiftDown()) {
            return;
        }

        ObservableList list = FXCollections.observableArrayList();
        for (int i = 0; i < data.size(); i++)
            if (data.get(i).toString().toLowerCase().startsWith(
                    AutoCompleteComboBoxListener.this.comboBox.getEditor().getText().toLowerCase()))
                list.add(data.get(i));


        String t = comboBox.getEditor().getText();

        comboBox.setItems(list);
        comboBox.getEditor().setText(t);
        if (!moveCaretToPos)
            caretPos = -1;

        moveCaret(t.length());
        if (!list.isEmpty())
            comboBox.show();
    }

    private void moveCaret(int textLength) {
        if (caretPos == -1) comboBox.getEditor().positionCaret(textLength);
        else comboBox.getEditor().positionCaret(caretPos);
        moveCaretToPos = false;
    }
}