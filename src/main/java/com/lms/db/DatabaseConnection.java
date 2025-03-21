package com.lms.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.exceptions.LMSException;

public class DatabaseConnection {

	private static final String url = "jdbc:sqlite:lmsDB.db";
	//private static Connection conn = null;
	
	public static Connection getConnection() throws LMSException {
		try {
            return DriverManager.getConnection(url);  // Always return a new connection
        } catch (SQLException e) {
            throw new LMSException("Failed to connect to the database", e);
        }
	}
}
