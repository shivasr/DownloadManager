/**
 * 
 */
package com.app.downloader;

import com.app.downloader.api.IDownloader;
import com.app.downloader.api.IFTPInterface;
import com.app.downloader.api.exception.DownloaderException;
import com.app.downloader.logger.Logger;

/**
 * @author Shivakumar Ramannavar
 *
 */
public class DownloadManager implements IDownloader {
	
	private static final String PROTOCOL_SEPERATOR = "://";

	private String keyFile;

	public DownloadManager buildDownloadManagerWithPEMKey(String pathToPEMFile) {
		this.keyFile = pathToPEMFile;
		
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.app.donwloader.transfer.IDownloader#startDownload(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public int startDownload(String username, String source, String target) {

		String protocol = source.substring(0, source.indexOf(PROTOCOL_SEPERATOR));
		source = source.substring(source.indexOf(PROTOCOL_SEPERATOR) + PROTOCOL_SEPERATOR.length());
		String host = source.substring(0, source.indexOf('/'));
		String rfile = source.substring(source.indexOf('/'));
		
		IFTPInterface ftpInterface = FTPClientFactory.createFTPClient(protocol, host);
		
		try {
			Logger.debugLine("Logging in ...");
			// ftpInterface.login(user, "password");
			
			ftpInterface.loginWithPEMKey(username, keyFile);
			Logger.debugLine("Done.");
			Logger.debugLine("Downloading the file: " + rfile + " to " + target);
			ftpInterface.downloadFrom(rfile, target);
			Logger.debugLine("Done.");
		} catch (DownloaderException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.app.donwloader.transfer.IDownloader#getProgress(int)
	 */
	@Override
	public DownloadStatus getProgress(int token) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DownloadManager downloadManager = new DownloadManager()
												.buildDownloadManagerWithPEMKey("/Users/shiva/shiva.pem");
		downloadManager.startDownload("ec2-user", "sftp://ec2-34-227-108-143.compute-1.amazonaws.com/home/ec2-user/JobZtop.zip", "/Users/shiva/Downloads/");
	}

}
