/**
 * 
 */
package com.app.downloader.api.exception;

/**
 * This class is used to handle application exception.
 * 
 * @author Shivakumar Ramannavar
 */
public class DownloaderException extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor to initialize exception with message.
	 * 
	 * @param message Error message of the exception.
	 */
	public DownloaderException(String message) {
		super(message);
	}

	/**
	 * Constructor to initialize exception with message and exception object.
	 * 
	 * @param message Error message of the exception.
	 * @param cause Exception
	 */
	public DownloaderException(String message, Throwable cause) {
		super(message, cause);
	}
}
