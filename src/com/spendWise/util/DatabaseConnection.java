package com.spendWise.util;

import java.io.*;
import java.util.*;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import com.spendWise.config.Config;

import java.sql.*;

/**
 * Contains the database connection information and methods to connect to the database.
 * @version 1.0
 * @author Pasindu Ravimal
 */
public class DatabaseConnection {

    private DatabaseConnection(){}

    private static Connection connection;
    private static Statement statement;

    /**
     * Connects to the database using the information in the databaseInfo.properties file.
     * @return the {@code Connection} object to the database
     * @throws SQLException
     * @throws IOException
     */
    public static Connection getConnection() throws SQLException, IOException {
        if (connection != null) {
            return connection;
        }

        var props = new Properties();

        try (InputStream in = Config.class.getResourceAsStream("databaseInfo.properties")) {
            props.load(in);
        }

        String drivers = props.getProperty("jdbc.drivers");

        if (drivers != null) {
            System.setProperty("jdbc.drivers", drivers);
        }

        String url = props.getProperty("jdbc.url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");

        connection = DriverManager.getConnection(url, username, password);
        return connection;
    }

    /**
     * Connects to the database using the given information.
     * @param url URL to the database
     * @param username username to the database
     * @param password password to the database
     * @return the {@code Connection} object to the database
     * @throws SQLException
     */
    public static Connection getConnection(String url, String username, String password) throws SQLException {
        
        if (connection != null && connection.getMetaData().getURL().equals(url)) {
            return connection;
        } else if (connection != null) {
            connection.close();
        }

        connection = DriverManager.getConnection(url, username, password);

        return connection;
    }

    public static Statement getStatement() throws SQLException {
        if (connection != null && statement != null){
            return statement;
        } else if (connection != null) {
            statement = connection.createStatement();
            return statement;
        } else {
            throw new SQLException("No connetion has been established");
        }
    }

    public static boolean isTablesExist() throws SQLException, UnsupportedOperationException{
        if (connection == null){
            throw new UnsupportedOperationException("Database is not connected");
        }

        getStatement();

        try{
            statement.executeQuery("SELECT * FROM USERS");
        } catch (SQLException e){
            return false;
        }

        return false;
    }

    public static void setupDatabase() throws SQLException{
        // TODO: Add statements to create the tables
    }
}