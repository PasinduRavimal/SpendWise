package com.spendWise.util;

import java.io.*;
import java.util.*;

import com.spendWise.config.Config;

import java.sql.*;

/**
 * Contains the database connection information and methods to connect to the database.
 * @version 1.0
 * @author Pasindu Ravimal
 */
public class DatabaseConnection {

    private DatabaseConnection(){}

    private volatile static Connection connection;
    private volatile static Statement statement;

    /**
     * Connects to the database using the information in the databaseInfo.properties file.
     * @return the {@code Connection} object to the database
     * @throws SQLException
     * @throws IOException
     */
    public static synchronized Connection getConnection() throws SQLException, IOException {
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
    public static synchronized Connection getConnection(String url, String username, String password) throws SQLException {
        
        if (connection != null && connection.getMetaData().getURL().equals(url)) {
            return connection;
        } else if (connection != null) {
            connection.close();
        }

        connection = DriverManager.getConnection(url, username, password);

        return connection;
    }

    public static synchronized Statement getStatement() throws UnsupportedOperationException, SQLException {
        if (connection != null && statement != null && !statement.isClosed()){
            if (statement.getConnection().equals(connection))
                return statement;

            statement.close();
            statement = connection.createStatement();

            return statement;

        } else if (connection != null) {
            statement = connection.createStatement();
            return statement;

        } else {
            throw new UnsupportedOperationException("No connetion has been established");
        }
    }

    public static boolean isTablesExist() throws SQLException, UnsupportedOperationException {
        if (connection == null){
            throw new UnsupportedOperationException("Database is not connected");
        }

        getStatement();

        try{
            statement.executeQuery("SELECT * FROM user");
        } catch (SQLException e){
            return false;
        } finally {
            statement.close();
        }

        return true;
    }

    public static void setupDatabase() throws SQLException {
        getStatement();

        statement.addBatch("CREATE TABLE `spendwise`.`user` (" +
                        "  `username` VARCHAR(30) NOT NULL," +
                        "  `displayname` VARCHAR(50) NOT NULL," +
                        "  `password` VARCHAR(30) NOT NULL," +
                        "  PRIMARY KEY (`username`));");
        
        statement.addBatch("CREATE TABLE `spendwise`.`accounttypes` (`accountID` INT NOT NULL AUTO_INCREMENT , `accountName` VARCHAR(50) NOT NULL , PRIMARY KEY (`accountID`), UNIQUE `accountName_UNIQUE` (`accountName`)) ENGINE = InnoDB;");

        statement.addBatch("CREATE TABLE `spendwise`.`transaction` (`transactionID` INT NOT NULL AUTO_INCREMENT , `username` VARCHAR(50) NOT NULL , `debitAccountID` INT NOT NULL , `creditAccountID` INT NOT NULL , `transactionTime` DATETIME NOT NULL , `amount` DOUBLE NOT NULL , `description` VARCHAR(255) , PRIMARY KEY (`transactionID`)) ENGINE = InnoDB; ");

        statement.addBatch("ALTER TABLE `transaction` ADD INDEX(`debitAccountID`, `creditAccountID`);");

        statement.addBatch("ALTER TABLE `transaction` ADD CONSTRAINT `FK_username_user` FOREIGN KEY (`username`) REFERENCES `user`(`username`) ON DELETE RESTRICT ON UPDATE RESTRICT;");

        statement.addBatch("ALTER TABLE `transaction` ADD CONSTRAINT `FK_creditAccountID_account` FOREIGN KEY (`creditAccountID`) REFERENCES `accounttypes`(`accountID`) ON DELETE RESTRICT ON UPDATE RESTRICT;");

        statement.addBatch("ALTER TABLE `transaction` ADD CONSTRAINT `FK_debitAccountID_account` FOREIGN KEY (`debitAccountID`) REFERENCES `accounttypes`(`accountID`) ON DELETE RESTRICT ON UPDATE RESTRICT;");

        statement.executeBatch();

        statement.close();
    }

    // TODO: To be removed
    public static void addDescriptionColumn() throws SQLException{
        try {
            DatabaseConnection.getStatement().executeQuery("SELECT description FROM transaction");
        } catch (Exception e) {
            DatabaseConnection.getStatement().executeUpdate("ALTER TABLE transaction ADD COLUMN description VARCHAR(255);");
        } finally {
            statement.close();
        }
    }
}