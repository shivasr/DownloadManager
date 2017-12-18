/**
 * 
 */
package com.app.downloader.transfer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import com.app.downloader.api.exception.DownloaderException;
import com.app.downloader.domain.HttpFileAttributes;
import com.app.downloader.logger.Logger;
import com.app.downloader.util.LocalHostOperations;
import com.app.downloader.util.RemoteHostOperations;

/**
 * @author shiva
 *
 */
public class HTTPFileTransferClient extends AbstractFTPClient {

	/**
	 * @param host
	 */
	public HTTPFileTransferClient(String host) {
		super(host);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.app.downloader.api.IFTPInterface#downloadFrom(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void downloadFrom(String remoteFile, String localFile) throws DownloaderException {
		String fullpath = "";
		FileOutputStream fos = null;
		try {

			String remoteFileName = new URL(remoteFile).getFile();
			int firstIndexOfSlash = remoteFileName.indexOf("/");
			remoteFileName = remoteFileName.substring(firstIndexOfSlash + 1)
							.replaceAll("/", "_")
							.replaceAll(":", "_");

			Optional.ofNullable(remoteFileName).orElseThrow(
					() -> new DownloaderException("Mallformed remote URL %s, it does not include file name"));

			fullpath = localFile + "/" + remoteFileName;
			fos = new FileOutputStream(fullpath);

			// Step 1: Validate appropriate exists in the local.
			HttpFileAttributes attr = RemoteHostOperations.getFileAttributes(remoteFile);
			
			
			long localFSAlotted = new File(localFile).getFreeSpace();
			if (attr.getSize() >= localFSAlotted) {
				throw new DownloaderException("Insufficient local storage space.");
			}

			LocalHostOperations.checkTargetFileCreation(fullpath);

			if (downloadTracker != null)
				downloadTracker.beforeStartOfDownload(attr.getSize());

			// Step 2: Download the remote file to the local.
			downloadUsingStream(remoteFile, fullpath);
		} catch (IOException e) {
			Logger.debug("\nError while downloading. Clearing the file.", e);
			if (downloadTracker != null)
				downloadTracker.downloadFailed(new File(fullpath).toPath());
		} finally {
			try {
				if(fos != null) {
					fos.flush();
					fos.close();
				}
			} catch (IOException e) {
				// Do Nothing
			}
		}
	}

	private static void downloadUsingStream(String urlStr, String file) throws IOException {
		URL url = new URL(urlStr);
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		FileOutputStream fis = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int count = 0;
		while ((count = bis.read(buffer, 0, 1024)) != -1) {
			fis.write(buffer, 0, count);
		}
		fis.close();
		bis.close();
	}
}
