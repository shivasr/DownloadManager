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
	public void beforeStartOfDownload(int size) {
		Logger.debug("****** Start of Download **** ");

	}

	@Override
	public void endOfDownload(int size) {
		Logger.debug("****** End of Download **** ");
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
}
