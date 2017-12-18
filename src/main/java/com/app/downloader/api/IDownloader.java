/**
 * 
 */
package com.app.downloader.api;

import com.app.downloader.api.exception.DownloaderException;

/**
 * 
 * @author Shivakumar Ramannavar
 *
 */
public interface IDownloader {

	public class DownloadStatus {
		int totalSize;
		int currentSize;
	}

	public boolean startDownload(String source, String target) throws DownloaderException;

	public DownloadStatus getProgress(int token);

}
