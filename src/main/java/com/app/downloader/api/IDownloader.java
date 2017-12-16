/**
 * 
 */
package com.app.downloader.api;

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

	public int startDownload(String username, String source, String target);

	public DownloadStatus getProgress(int token);

}
