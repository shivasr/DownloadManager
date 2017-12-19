/**
 * 
 */
package com.app.downloader.transfer;

import com.app.downloader.api.DownloadTracker;
import com.app.downloader.api.IFTPInterface;
import com.app.downloader.api.impl.DownloadTrackerImpl;

/**
 * @author shiva
 *
 */
public abstract class AbstractFTPClient 
						implements IFTPInterface{
	
	/**
	 * DownloadTracker Interface
	 */
	DownloadTracker downloadTracker;
	
	/**
	 * Host name of the SFTP Server.
	 */
	private String host;

	// Credentials to login to the SFTP Server
	/**
	 * User name of the authorized user to login to the server.
	 */
	protected String username;

	/**
	 * Password of the authorized user to login to the server.
	 */
	protected String password;

	/**
	 * Location, on the local machine, of public key for the authorized user to
	 * login to the server.
	 */
	protected String pathToPublicKey;

	/**
	 * Location, on the local machine, of PEM private key for the authorized user to
	 * login to the server.
	 */
	protected String pathToPEMKeyFile;

	// Flags to denote authentication methodology
	/**
	 * Use User name/password to authenticate the user.
	 */
	protected boolean useUserNamePwd = false;
	
	/**
	 * Use Public key to authenticate the user.
	 */
	protected boolean usePublicKey = false;
	
	
	/**
	 * Use Default Public key to authenticate the user.
	 */
	protected boolean useDefaultKey = false;
	
	/**
	 * Use PEM Private key to authenticate the user.
	 */
	protected boolean usePemKey = false;
	
	/**
	 * Port of the FTP Service.
	 */
	protected int port = -1;


	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the useUserNamePwd
	 */
	public boolean isUseUserNamePwd() {
		return useUserNamePwd;
	}

	/**
	 * @param useUserNamePwd the useUserNamePwd to set
	 */
	public void setUseUserNamePwd(boolean useUserNamePwd) {
		this.useUserNamePwd = useUserNamePwd;
	}

	/**
	 * 
	 */
	public AbstractFTPClient(String host) {
		this.host = host;
		this.downloadTracker = new DownloadTrackerImpl(System.currentTimeMillis());
	}

	/**
	 * Method to set credentials with user and password.
	 * 
	 * @param username
	 *            Username to login to the SFTP Server
	 * @param password
	 *            Password to login to the SFTP Server
	 * @return
	 */
	@Override
	public boolean login(String username, String password) {
		this.username = username;
		this.password = password;
		this.useUserNamePwd = true;
		return true;
	}
	
	/**
	 * Method to set credentials with user and path to public key.
	 * 
	 * @param username  Username to login to the Server
	 * @param pathToKey Path to public key of the user.
	 * @return true
	 */
	@Override
	public boolean loginWithKey(String username, String pathToKey) {
		throw new UnsupportedOperationException("Login with public key not supported for this FTP protocol.");
	}

	/**
	 * Method to set credentials with user and path to public key.
	 * 
	 * @param username  Username to login to the Server
	 * @param pathToKey Path to public key of the user.
	 * @return true
	 */
	@Override
	public boolean loginWithKey(String username) {
		throw new UnsupportedOperationException("Login with public key not supported for this FTP protocol.");
	}
	
	/**
	 * Method to set credentials with user and path to PEM private key.
	 * 
	 * @param username  Username to login to the Server
	 * @param pathToKey Path to PM private key of the user.
	 * @return true
	 */
	@Override
	public boolean loginWithPEMKey(String username, String keyFile) {
		throw new UnsupportedOperationException("Login with PEM private key not supported for this FTP protocol.");
	}
	
	@Override
	public void overridePort(int port) {
		setPort(port);
	}
	
	@Override
	public void registerDownloadTracker(DownloadTracker downloadTracker) {
		this.downloadTracker = downloadTracker;

	}
	
	/**
	 * @return the downloadTracker
	 */
	public DownloadTracker getDownloadTracker() {
		return downloadTracker;
	}

	/**
	 * @param downloadTracker the downloadTracker to set
	 */
	public void setDownloadTracker(DownloadTracker downloadTracker) {
		this.downloadTracker = downloadTracker;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

}
