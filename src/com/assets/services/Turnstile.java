package com.assets.services;

import java.net.ConnectException;
import java.sql.Array;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class Turnstile extends Thread {
    private List<Integer> ids;
    private final DBConnector connector;

    public Turnstile() {
        connector = new DBConnector(Consts.DBType, Consts.url, Consts.DB, Consts.username, Consts.password);
        try {
            ids = connector.getObjects("SELECT `id` from `employees`", null, (r) -> r.getInt("id"));
        } catch (SQLException e) {
            System.out.println("Employee's id not found");
        }
    }

    @Override
    public void run() {
        if (ids == null || ids.size() == 0) {
            interrupt();
            return;
        }

        Random rand = new Random();
        while (true) {
            int index = rand.nextInt(0, ids.size());
            int chosenId = ids.get(index);

            try {
                Request.insert("registration",
                        new String[]{"id_employee", "datetime"},
                        stmt -> {
                            stmt.setInt(1, chosenId);
                            stmt.setString(2,LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        });
            } catch (ConnectException e) {
                throw new RuntimeException(e);
            }


            try {
                Thread.sleep(rand.nextInt(1000, 600000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}