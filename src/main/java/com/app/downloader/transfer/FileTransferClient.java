/**
 * 
 */
package com.app.downloader.transfer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.Optional;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.app.downloader.ErrorMessages;
import com.app.downloader.api.exception.DownloaderException;
import com.app.downloader.logger.Logger;
import com.app.downloader.util.LocalHostOperations;
import com.app.downloader.util.Utilities;

/**
 * @author shiva
 *
 */
public class FileTransferClient extends AbstractFTPClient {

	
	FTPClient ftp = null;
	
	/**
	 * 
	 */
	public FileTransferClient(String host) {
		super(host);
		ftp = new FTPClient();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.app.downloader.api.IFTPInterface#downloadFrom(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void downloadFrom(String remoteFile, String localFile) throws DownloaderException {

		Optional.ofNullable(remoteFile).orElseThrow(() -> new DownloaderException(ErrorMessages.SOURCE_URL_IS_NULL));
		Optional.ofNullable(localFile).orElseThrow(() -> new DownloaderException(ErrorMessages.ERROR_DOWNLOAD_LOCATION_IS_NULL));
		
		localFile = (Utilities.isNullOrEmpty(localFile))? "." : localFile;  
		
		String fullpath = "";
		ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		try {
			
			if(getPort() != -1)
				ftp.connect(getHost(), getPort());
			else
				ftp.connect(getHost());

			ftp.login(getUsername(), getPassword());
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.enterLocalPassiveMode();
		} catch (SocketException e) {
			throw new DownloaderException(ErrorMessages.ERROR_CONNECTING_TO_FTP_SERVER, e);
		} catch (IOException e) {
			throw new DownloaderException(ErrorMessages.ERROR_CONNECTING_TO_FTP_SERVER, e);
		}
		try {

			if(!localFile.endsWith("/"))
				localFile = localFile + "/";
			
			fullpath = localFile + remoteFile.substring(remoteFile.lastIndexOf("/"));
			LocalHostOperations.checkTargetFileCreation(fullpath);
			FileOutputStream fos = new FileOutputStream(fullpath);
			
			// Step 2: Download the remote file to the local.
			this.ftp.retrieveFile(remoteFile, fos);
			
		} catch (IOException e) {
			Logger.debug("Error while downloading. Clearing the file.", e);
			if (downloadTracker != null)
				downloadTracker.downloadFailed(new File(fullpath).toPath());
			throw new DownloaderException("Error while downloading. Clearing the file.", e);
		}
	}

	public FTPClient getFtp() {
		return ftp;
	}

	public void setFtp(FTPClient ftp) {
		this.ftp = ftp;
	}
}
