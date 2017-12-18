/**
 * 
 */
package com.app.downloader.api;

import java.io.IOException;

/**
 * @author shiva
 *
 */
public interface ProgressListener {
	public void reportProgress(long transferred, long size, String name);
}
