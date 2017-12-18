/**
 * 
 */
package com.app.downloader.transfer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.PublicKey;

import com.app.downloader.api.DownloadTracker;
import com.app.downloader.api.IFTPInterface;
import com.app.downloader.api.exception.DownloaderException;
import com.app.downloader.api.impl.FileTransferAdapter;
import com.app.downloader.logger.Logger;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.PathComponents;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.userauth.UserAuthException;
import net.schmizz.sshj.userauth.keyprovider.PKCS8KeyFile;
import net.schmizz.sshj.userauth.method.AuthPublickey;
import net.schmizz.sshj.xfer.FileSystemFile;

/**
 * This class is the client to the SSH FTP Server and uses below authentication
 * methods:
 * 
 * <p>
 * 1) Using username and password <br/>
 * 2) Using public key  <br/>
 * 3) Using PEM private key <br/>
 * </p>
 * 
 * @author Shivakumar Ramannavar
 *
 */
public class SecureFTPClient extends AbstractFTPClient  {

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
	 * Constructor with host.
	 * 
	 * @param host
	 */
	public SecureFTPClient(String host) {
		super(host);
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

	@Override
	public boolean loginWithKey(String username, String pathToKey) {

		this.usePublicKey = true;
		this.username = username;
		this.pathToPublicKey = pathToKey;
		return true;
	}
	
	@Override
	public boolean loginWithKey(String username) {

		this.useDefaultKey = true;
		this.username = username;
		return true;
	}

	@Override
	public boolean loginWithPEMKey(String username, String keyFile) {
		this.username = username;
		this.usePemKey = true;
		this.pathToPEMKeyFile = keyFile;
		return true;
	}

	@Override
	public void downloadFrom(String remoteFile, String localFile) throws DownloaderException {

		final SSHClient ssh = new SSHClient();
		FileSystemFile localFS = new FileSystemFile(localFile);
		
		try {
			// Authenticate User using client
			authenticateUser(ssh);

			// Download using client
			downloadUsingClient(ssh, remoteFile, localFS);
		} catch (Exception e) {
			throw new DownloaderException(
					"Issue with connection to the SFTP server.", e);
		} finally {
			try {
				ssh.disconnect();
				ssh.close();
			} catch (IOException e) {
				// Do Nothing
			}

		}

	}

	/**
	 * Download Using client
	 * @param ssh
	 * @param remoteFile
	 * @param localFS
	 * @return
	 * @throws ConnectionException
	 * @throws TransportException
	 * @throws IOException
	 * @throws DownloaderException
	 */
	private void downloadUsingClient(final SSHClient ssh, String remoteFile, FileSystemFile localFS)
			throws ConnectionException, TransportException, IOException, DownloaderException {
		Path path = null;;
		ssh.startSession();
		final SFTPClient sftp = ssh.newSFTPClient();
		try {
			
			// Step 1: Register Progress Listener
			final PathComponents pathComponents = sftp.getSFTPEngine().getPathHelper().getComponents(remoteFile);
			path = localFS.getTargetFile(pathComponents.getName()).getFile().toPath();
			
			sftp.getFileTransfer().setTransferListener(new FileTransferAdapter(downloadTracker));
			
			
			// Step 2: Validate appropriate exists in the local.
			long localFSAlotted = localFS.getFile().getFreeSpace();
			FileAttributes attributes = sftp.stat(remoteFile);
			long remoteFileSize = attributes.getSize();
			
			if(remoteFileSize >= localFSAlotted) {
				throw new DownloaderException("Insufficient local storage space.");
			}
			
			// Step 3: Download the remote file to the local.
			sftp.getFileTransfer().download(remoteFile, localFS);
		} catch(Exception e) {
			Logger.debug("Error while downloading. Clearing the file.", e);
			if(downloadTracker != null)
				downloadTracker.downloadFailed(path);
		} finally {
			sftp.close();
		}
	}

	/**
	 * @param ssh
	 * @throws IOException
	 * @throws UserAuthException
	 * @throws TransportException
	 * @throws DownloaderException
	 */
	private void authenticateUser(final SSHClient ssh)
			throws IOException, UserAuthException, TransportException, DownloaderException {
		ssh.addHostKeyVerifier(new NullHostKeyVerifier());
		
		if(getPort() == -1)
			ssh.connect(getHost());
		else
			ssh.connect(getHost(), getPort());
		
		if (usePublicKey) {
			ssh.authPublickey(username, pathToPublicKey);
		} else if (usePemKey) {
			PKCS8KeyFile pemKeyFile = new PKCS8KeyFile();
			pemKeyFile.init(new File(pathToPEMKeyFile));
			ssh.auth(username, new AuthPublickey(pemKeyFile));
		} else if (useUserNamePwd) {
			ssh.authPassword(username, password);
		} else if (useDefaultKey) {
			ssh.authPublickey(username);
		}
		else
			throw new DownloaderException(
					"User not logged in. Please use one of login methods to login to the server.");
	}

	/**
	 * 
	 * @author shiva
	 *
	 */
	public class NullHostKeyVerifier implements HostKeyVerifier {
		@Override
		public boolean verify(String arg0, int arg1, PublicKey arg2) {
			return true;
		}
	}

	@Override
	public void registerDownloadTracker(DownloadTracker downloadTracker) {
		this.downloadTracker = downloadTracker;
		
	}
}
