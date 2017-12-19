package com.app.downloader.logger;

/**
 * 
 * @author Shivakumar Ramannavar
 */
public class Logger {
	
	public static LogLevel logLevel = LogLevel.INFO;
	
	public enum LogLevel {
		INFO, DEBUG
	}
	public static void debug(String message) {
		if(logLevel == LogLevel.DEBUG)
			System.out.print("[DEBUG]" + message);
	}
	
	public static void debugLine(String message) {
		if(logLevel == LogLevel.DEBUG)
			System.out.println("[DEBUG]" + message);
	}
	
	public static void infoLine(String message) {
			System.out.println(message);
	}

	public static void debug(String message, Exception e) {
			System.out.println("[DEBUG]" + message + ". Error Message: " + e.getMessage());
	}
}
