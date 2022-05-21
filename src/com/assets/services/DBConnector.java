package com.assets.services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class DBConnector {
    private static String standardDBType;
    private static String standardURL;
    private static String standardUsername;
    private static String standardPassword;

    //jdbc:mysql://url/dbname
    private final String DBType;
    private String url;
    private String DB;
    private final String username;
    private final String password;

    public String fullURL() {
        return String.format("jdbc:%s://%s/%s", DBType, url, DB);
    }

    public DBConnector(String DBType, String url, String DB, String username, String password) {
        if (DBType == null || DBType.length() < 2)
            throw new IllegalArgumentException("Wrong name of database type.");
        if (url == null || url.length() < 5)
            throw new IllegalArgumentException("Wrong url.");
        if (DB == null || DB.length() == 0)
            throw new IllegalArgumentException("Wrong database name.");

        this.DBType = DBType;
        this.url = url;
        this.DB = DB;
        this.username = username;
        this.password = password;
    }

    public boolean testConnection() {
        return sendQuery("SELECT 1", null);
    }

    public boolean sendQuery(String query, Action<PreparedStatement> setValues) {
        try {
            Connection connection = DriverManager.getConnection(fullURL(), username, password);
            PreparedStatement state = connection.prepareStatement(query);
            if (setValues != null)
                setValues.run(state);
            state.execute();
            connection.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

//    public <T> List<T[]> getRows(String query, Action<PreparedStatement> setValues, Func<ResultSet, T[]> transformer) throws SQLException {
//        Connection connection = DriverManager.getConnection(fullURL(), username, password);
//        PreparedStatement state = connection.prepareStatement(query);
//        try {
//            if (setValues != null)
//                setValues.run(state);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        ResultSet result = state.executeQuery();
//
//        List<T[]> list = new ArrayList<>();
//        while (result.next())
//            list.add(transformer.run(result));
//
//        connection.close();
//        return list;
//    }

    public <T> List<T> getObjects(String query, Action<PreparedStatement> setValues, Func<ResultSet, T> transformer) throws SQLException {
        Connection connection = DriverManager.getConnection(fullURL(), username, password);
        PreparedStatement state = connection.prepareStatement(query);
        try {
            if (setValues != null)
                setValues.run(state);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet result = state.executeQuery();

        List<T> list = new ArrayList<>();
        while (result.next())
            list.add(transformer.run(result));

        connection.close();
        return list;
    }


    @FunctionalInterface
    public interface Func<T, R> {
        R run(T t) throws SQLException;
    }

    @FunctionalInterface
    public interface Action<T> {
        void run(T t) throws SQLException;
    }
}