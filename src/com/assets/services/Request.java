package com.assets.services;

import java.net.ConnectException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.assets.services.Consts.*;

public class Request {
    public static List<String> select(String columnName, String tableName) throws ConnectException, SQLException {
        DBConnector connector = new DBConnector(DBType, url, DB, username, Consts.password);
        if (!connector.testConnection())
            throw new ConnectException("Не удалось подключиться к базу данных");

        String query = "Select `" + columnName + "` from `" + tableName + "`";
        return connector.getObjects(query,
                null,
                r -> r.getString(columnName));
    }

    private static <T> List<T> selectWhere(String[] columns, String tableName, String[] conditionalColumns,
                                           DBConnector.Action<PreparedStatement> setValue, DBConnector.Func<ResultSet, T> transformer, boolean limited)
            throws ConnectException, SQLException {
        DBConnector connector = new DBConnector(DBType, url, DB, username, Consts.password);
        if (!connector.testConnection())
            throw new ConnectException("Не удалось подключиться к базу данных");

        String cols = String.join(", ", Arrays.stream(columns).map((String el) -> "`" + el + "`").
                toArray(String[]::new));
        String conds = conditionalColumns.length != 0 ?
                ("WHERE " + String.join("=? AND ", Arrays.stream(conditionalColumns).map((String el) -> "`" + el + "`").
                        toArray(String[]::new)) + "=?") : "";

        String query = String.format("SELECT %s FROM %s %s" + (limited ? "LIMIT 1" : ""), cols, tableName, conds);
        return connector.getObjects(query, setValue, transformer);
    }

    public static <T> List<T> selectWhere(String[] columns, String tableName, String[] conditionalColumns,
                                          DBConnector.Action<PreparedStatement> setValue, DBConnector.Func<ResultSet, T> transformer, boolean limited, boolean distinct)
            throws ConnectException, SQLException {
        DBConnector connector = new DBConnector(DBType, url, DB, username, Consts.password);
        if (!connector.testConnection())
            throw new ConnectException("Не удалось подключиться к базу данных");

        String cols = (distinct ? "DISTINCT " : "") + String.join(", ", Arrays.stream(columns).map((String el) -> "`" + el + "`").
                toArray(String[]::new));
        String conds = conditionalColumns.length != 0 ?
                ("WHERE " + String.join("=? AND ", Arrays.stream(conditionalColumns).map((String el) -> "`" + el + "`").
                        toArray(String[]::new)) + "=?") : "";

        String query = String.format("SELECT %s FROM %s %s" + (limited ? "LIMIT 1" : ""), cols, tableName, conds);
        return connector.getObjects(query, setValue, transformer);
    }

    public static <T> List<T> selectWhere(String[] columns, String tableName, String[] conditionalColumns,
                                          DBConnector.Action<PreparedStatement> setValue, DBConnector.Func<ResultSet, T> transformer)
            throws ConnectException, SQLException {
        return selectWhere(columns, tableName, conditionalColumns, setValue, transformer, false);
    }

    public static <T> List<T> selectWhereLimitOne(String[] columns, String tableName, String[] conditionalColumns,
                                                  DBConnector.Action<PreparedStatement> setValue, DBConnector.Func<ResultSet, T> transformer)
            throws ConnectException, SQLException {
        return selectWhere(columns, tableName, conditionalColumns, setValue, transformer, true);
    }

    public static boolean insert(String tableName, String[] columnsName, DBConnector.Action<PreparedStatement> setValue) throws ConnectException {
        DBConnector connector = new DBConnector(DBType, url, DB, username, Consts.password);
        if (!connector.testConnection())
            throw new ConnectException("Не удалось подключиться к базе данных");

        String cols = String.join(", ", Arrays.stream(columnsName).map((String el) -> "`" + el + "`").
                toArray(String[]::new));
        String conds = "?,".repeat(columnsName.length).replaceAll(",$", "");
        String query = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, cols, conds);
        return connector.sendQuery(query, setValue);
    }

    public static boolean delete(String tableName, String[] columnsName, DBConnector.Action<PreparedStatement> setValue) throws ConnectException {
        DBConnector connector = new DBConnector(DBType, url, DB, username, Consts.password);
        if (!connector.testConnection())
            throw new ConnectException("Не удалось подключиться к базе данных");

        String conds = columnsName.length != 0 ?
                ("WHERE " + String.join("=?, ", Arrays.stream(columnsName).map((String el) -> "`" + el + "`").
                        toArray(String[]::new)) + "=?") : "";

        String query = String.format("DELETE FROM %s %s", tableName, conds);
        return connector.sendQuery(query, setValue);
    }

    public static int count(String tableName, String[] conditionalColumns, DBConnector.Action<PreparedStatement> setValue,
                            DBConnector.Func<ResultSet, Integer> transformer) throws ConnectException {

        DBConnector connector = new DBConnector(DBType, url, DB, username, Consts.password);
        if (!connector.testConnection())
            throw new ConnectException("Не удалось подключиться к базе данных");

        String conds = conditionalColumns.length != 0 ?
                ("WHERE " + String.join("=? AND ", Arrays.stream(conditionalColumns).map((String el) -> "`" + el + "`").
                        toArray(String[]::new)) + "=?") : "";

        String query = String.format("SELECT COUNT(*) as count FROM %s %s", tableName, conds);

        try {
            return connector.getObjects(query, setValue, transformer).get(0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean update(String tableName, String columnName, String[] conditionalColumns, DBConnector.Action<PreparedStatement> setValue) throws ConnectException {
        DBConnector connector = new DBConnector(DBType, url, DB, username, Consts.password);
        if (!connector.testConnection())
            throw new ConnectException("Не удалось подключиться к базе данных");

        String conds = conditionalColumns.length != 0 ?
                ("WHERE " + String.join("=?, ", Arrays.stream(conditionalColumns).map((String el) -> "`" + el + "`").
                        toArray(String[]::new)) + "=?") : "";

        String query = String.format("UPDATE %s SET %s=? %s", tableName, columnName, conds);
        return connector.sendQuery(query, setValue);
    }

    public static <T> List<T> getFromTables(String tableName, String[] columnsName, String joinConditional, String[] conditionalColumns,
                                            DBConnector.Action<PreparedStatement> setValue, DBConnector.Func<ResultSet, T> transformer) throws ConnectException, SQLException {
        DBConnector connector = new DBConnector(DBType, url, DB, username, Consts.password);
        if (!connector.testConnection())
            throw new ConnectException("Не удалось подключиться к базу данных");

        String cols = String.join(", ", Arrays.stream(columnsName).map((String el) -> "`" + el + "`").
                toArray(String[]::new));

        String conds = conditionalColumns.length != 0 ?
                ("WHERE " + String.join("=?, ", Arrays.stream(conditionalColumns).map((String el) -> el).
                        toArray(String[]::new)) + "=?") : "";

        String query = String.format("SELECT %s FROM (%s %s) %s", cols, tableName, joinConditional, conds);
        return connector.getObjects(query, setValue, transformer);
    }
}
