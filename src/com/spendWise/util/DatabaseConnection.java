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

    /**
     * Connects to the database using the information in the databaseInfo.properties file.
     * @return the {@code Connection} object to the database
     * @throws SQLException
     * @throws IOException
     */
    public static Connection getConnection() throws SQLException, IOException {
        var props = new Properties();

        try (InputStream in = DatabaseConnection.class.getResourceAsStream("databaseInfo.properties")) {
            props.load(in);
        }

        String drivers = props.getProperty("jdbc.drivers");

        if (drivers != null) {
            System.setProperty("jdbc.drivers", drivers);
        }

        String url = props.getProperty("jdbc.url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");

        return DriverManager.getConnection(url, username, password);
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
        return DriverManager.getConnection(url, username, password);
    }
}