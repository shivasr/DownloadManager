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
public interface DownloadTracker  {
	
	public void beforeStartOfDownload(int size);
	
	public void endOfDownload(int size);
	
	public void downloadFailed(Path localFilePath);

}
