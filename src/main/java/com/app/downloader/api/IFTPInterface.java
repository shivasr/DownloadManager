package com.app.downloader.api;

import com.app.downloader.api.exception.DownloaderException;

/**
 * This interface is used to download files from remote server.
 * 
 * @author Shivakumar Ramannavar
 *
 */
public interface IFTPInterface {
	
	/**
	 * Override Default port.
	 * 
	 * @param downloadTracker
	 */
	public void overridePort(int port);
	
	/**
	 * Register DownloadTracker Interface to let know the caller about events.
	 * 
	 * @param downloadTracker
	 */
	public void registerDownloadTracker(DownloadTracker downloadTracker);
	
	/**
	 * Method to set credentials with user and password.
	 * 
	 * @param username
	 *            Username to login to the SFTP Server
	 * @param password
	 *            Password to login to the SFTP Server
	 * @return
	 */
	public boolean login(String username, String password);
	
	/**
	 * Method to set credentials with user and path to public key.
	 * 
	 * @param username  Username to login to the Server
	 * @param pathToKey Path to public key of the user.
	 * @return true
	 */
	public boolean loginWithKey(String username, String pathToKey);
	
	/**
	 * Method to set credentials with user.
	 * 
	 * @param username  Username to login to the Server
	 * @return true
	 */
	public boolean loginWithKey(String username);

	/**
	 * Method to set credentials with user and path to PEM private key.
	 * 
	 * @param username  Username to login to the Server
	 * @param pathToKey Path to PM private key of the user.
	 * @return true
	 */
	public boolean loginWithPEMKey(String username, String keyFile);
	/**
	 * Download file from remote location to local disk.
	 * @param remoteFile path to the file in the remote server.
	 * @param localFile path to the file in the local disk.
	 * @throws DownloaderException 
	 */
	public void downloadFrom(String remoteFile, String localFile) throws DownloaderException;

}