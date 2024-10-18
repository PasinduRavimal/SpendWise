package com.spendWise.util;

import java.io.*;
import java.util.*;
import java.sql.*;

/**
 * Contains the database connection information and methods to connect to the database.
 * @version 1.0
 * @author Pasindu Ravimal
 */
public class DatabaseConnection {

    private DatabaseConnection(){}

    public static Connection connection;

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

        try (InputStream in = DatabaseConnection.class.getResourceAsStream("config/databaseInfo.properties")) {
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

    public static void setupDatabase() throws SQLException{
        Statement statement = connection.createStatement();

        statement.addBatch("query");

        // TODO: Add statements to create the tables
    }
}