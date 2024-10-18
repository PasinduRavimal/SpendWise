package com.spendWise;

import java.sql.*;

public class Dashboard {

    protected static Connection connection;

    public static void main(String[] args) throws SQLException{
        if (connection != null){
            System.out.println("Connection Established!");
            connection.close();
        }

        return;
    }
    
}
