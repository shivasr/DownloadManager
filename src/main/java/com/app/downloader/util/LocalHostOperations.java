/**
 * 
 */
package com.app.downloader.util;

import java.io.File;
import java.io.IOException;

import com.app.downloader.api.exception.DownloaderException;

/**
 * @author shiva
 *
 */
public class LocalHostOperations {

	/**
	 * Check Feasibility to create target file locally.
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static boolean checkTargetFileCreation(String filename) throws DownloaderException {

		File file = new File(filename);

		if (!file.exists()) {
			try {
				if (!file.createNewFile())
					throw new DownloaderException("Could not create: " + file);
			} catch (IOException e) {
				throw new DownloaderException("Could not create: " + file, e);
			}
		} else
			throw new DownloaderException(String.format("File with %s already exists.", filename));
		

		return true;
	}

}
