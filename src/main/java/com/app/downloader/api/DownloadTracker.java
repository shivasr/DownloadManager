/**
 * 
 */
package com.app.downloader.api;

import java.nio.file.Path;

/**
 * This is a listener to take actions for the download events such before start of download, end of download, failure of download.
 * @author shiva
 *
 */
public interface DownloadTracker extends ProgressListener {
	
	public void beforeStartOfDownload(long size);
	
	public void endOfDownload(long size);
	
	public void downloadFailed(Path localFilePath);

}
