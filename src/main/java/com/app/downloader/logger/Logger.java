package com.app.downloader.logger;

/**
 * 
 * @author Shivakumar Ramannavar
 */
public class Logger {
	
	public static void debug(String message) {
		System.out.print("[DEBUG]" + message);
	}
	
	public static void debugLine(String message) {
		System.out.println("[DEBUG]" + message);
	}

	public static void debug(String message, Exception e) {
		System.out.println("[DEBUG]" + message);
		System.out.println("[DEBUG]Error Message: " + e.getMessage());
	}
}
