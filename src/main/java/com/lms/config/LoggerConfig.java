package com.lms.config;

import java.io.IOException;
import java.util.logging.*;

public class LoggerConfig {
	
	// --- define logging system to write logs to a file instead of console ---
	public static void setupLogging() {
		Logger rootLogger = Logger.getLogger("");
		Handler fileHandler;
		
		try {
			fileHandler = new FileHandler("configLogs.log", true);
			fileHandler.setFormatter(new SimpleFormatter());
			rootLogger.addHandler(fileHandler);
			
			// to consider all log levels
			rootLogger.setLevel(Level.ALL);
		} catch (IOException e) {
			System.out.println("Failed to initialize logging system: " + e.getMessage());
		}
	}

}