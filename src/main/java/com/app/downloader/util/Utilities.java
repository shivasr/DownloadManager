/**
 * 
 */
package com.app.downloader.util;

/**
 * @author shiva
 *
 */
public class Utilities {

	/**
	 * Check if its null or empty.
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNullOrEmpty(String value) {
		if(value == null)
			return true;
		
		if(value.isEmpty())
			return true;
		
		return false;
	}

}
