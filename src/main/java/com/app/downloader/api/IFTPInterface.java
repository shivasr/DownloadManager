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
	 * Register DownloadTracker Interface to let know the caller about events.
	 * 
	 * @param downloadTracker
	 */
	public void registerDownloadTracker(DownloadTracker downloadTracker);
	
	/**
	 * Login with public key to the remote server.
	 * 
	 * @param username user name of the authorized user.
	 * @param password password of the authorized user.
	 * @return true if login is successfully.
	 */
	public boolean login(String username, String password);
	
	/**
	 * Login with public key to the remote server.
	 * 
	 * @param username user name of the authorized user.
	 * @param pathToKey location to public key.
	 * @return true if login is successfully.
	 */
	public boolean loginWithKey(String username, String pathToKey);
	
	/**
	 * Login with PEM Private key to the remote server.
	 * 
	 * @param username user name of the authorized user.
	 * @param keyFile location to private key.
	 * @return true if login is successfully.
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