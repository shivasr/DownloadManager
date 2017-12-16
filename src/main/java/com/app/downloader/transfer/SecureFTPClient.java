/**
 * 
 */
package com.app.downloader.transfer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PublicKey;

import com.app.downloader.api.IFTPInterface;
import com.app.downloader.api.exception.DownloaderException;
import com.app.downloader.api.impl.CustomTransferListener;
import com.app.downloader.logger.Logger;
import com.app.downloader.util.RemoteHostOperations;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.PathComponents;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.sftp.SFTPFileTransfer;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
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
public class SecureFTPClient implements IFTPInterface {

	/**
	 * Host name of the SFTP Server.
	 */
	private String host;

	// Credentials to login to the SFTP Server
	/**
	 * User name of the authorized user to login to the server.
	 */
	private String username;

	/**
	 * Password of the authorized user to login to the server.
	 */
	private String password;

	/**
	 * Location, on the local machine, of public key for the authorized user to
	 * login to the server.
	 */
	private String pathToPublicKey;

	/**
	 * Location, on the local machine, of PEM private key for the authorized user to
	 * login to the server.
	 */
	private String pathToPEMKeyFile;

	// Flags to denote authentication methodology
	/**
	 * Use User name/password to authenticate the user.
	 */
	private boolean useUserNamePwd = false;
	
	/**
	 * Use Public key to authenticate the user.
	 */
	private boolean usePublicKey = false;
	
	/**
	 * Use PEM Private key to authenticate the user.
	 */
	private boolean usePemKey = false;

	/**
	 * Constructor with host.
	 * 
	 * @param host
	 */
	public SecureFTPClient(String host) {
		super();
		this.host = host;

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
		
		Path path = null;
		
		try {
			ssh.addHostKeyVerifier(new NullHostKeyVerifier());
			ssh.connect(host);

			if (usePublicKey) {
				ssh.authPublickey(username, pathToPublicKey);
			} else if (usePemKey) {
				PKCS8KeyFile pemKeyFile = new PKCS8KeyFile();
				pemKeyFile.init(new File(pathToPEMKeyFile));
				ssh.auth(username, new AuthPublickey(pemKeyFile));
			} else if (useUserNamePwd)
				ssh.authPassword(username, password);
			else
				throw new DownloaderException(
						"User not logged in. Please use one of login methods to login to the server.");

			net.schmizz.sshj.connection.channel.direct.Session session;
			session = ssh.startSession();
			final SFTPClient sftp = ssh.newSFTPClient();
			try {
				
				final PathComponents pathComponents = sftp.getSFTPEngine().getPathHelper().getComponents(remoteFile);
				
				path = localFS.getTargetFile(pathComponents.getName()).getFile().toPath();
				
				sftp.getFileTransfer().setTransferListener(new CustomTransferListener());
				
				long localFSAlotted = localFS.getFile().getFreeSpace();
				FileAttributes attributes = sftp.stat(remoteFile);
				long remoteFileSize = attributes.getSize();
				
				if(remoteFileSize >= localFSAlotted) {
					throw new DownloaderException("Insufficient local storage space.");
				}
				
				sftp.getFileTransfer().download(remoteFile, localFS);

				RemoteHostOperations.listDirectory(session);
			} finally {
				sftp.close();
			}
		} catch (Exception e) {
			try {
				Logger.debug("Error while downloading. Clearing the file.", e);
				if(path != null)
					Files.deleteIfExists(path);
			} catch (IOException e1) {
				throw new DownloaderException("Unable to delete the downloaded file: " + path.getFileName());
			}
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
}
