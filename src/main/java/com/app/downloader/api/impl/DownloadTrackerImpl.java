/**
 * 
 */
package com.app.downloader.api.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.app.downloader.api.DownloadTracker;
import com.app.downloader.logger.Logger;

/**
 * This class is a implementation of DownloadTracker
 * 
 * @author Shivakumar Ramannavar
 *
 */
public class DownloadTrackerImpl implements DownloadTracker {

	long startInMillis;

	/**
	 * 
	 */
	public DownloadTrackerImpl(long startInMillis) {
		this.startInMillis = startInMillis;
	}

	@Override
	public void beforeStartOfDownload(long size) {
		Logger.debug("Starting the download.* ");

	}

	@Override
	public void endOfDownload(long size) {
		Logger.infoLine("Download complete. ");
	}

	@Override
	public void downloadFailed(Path localFilePath) {
		if (localFilePath != null)
			try {
				Files.deleteIfExists(localFilePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	public void reportProgress(long transferred, long size, String name) {
		Logger.infoLine(String.format("transferred %s %% of `%s`", ((transferred * 100) / size), name));
	}
}
