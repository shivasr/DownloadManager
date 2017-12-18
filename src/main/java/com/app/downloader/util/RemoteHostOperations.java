/**
 * 
 */
package com.app.downloader.util;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import com.app.downloader.api.exception.DownloaderException;
import com.app.downloader.domain.HttpFileAttributes;
import com.app.downloader.logger.Logger;

import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.TransportException;

/**
 * This class is used to execute remote host operations.
 * 
 * @author Shivakumar Ramannavar
 *
 */
public class RemoteHostOperations {

	/**
	 * Method to test the connectivity of session.
	 * 
	 * @param session
	 *            Session object containing the session to the SFTP server.
	 * 
	 * @throws ConnectionException
	 * @throws TransportException
	 * @throws IOException
	 */
	public static void listDirectory(Session session) throws ConnectionException, TransportException, IOException {
		final Command cmd = session.exec("ls -l");

		Logger.debug(IOUtils.readFully(cmd.getInputStream()).toString());
		cmd.join(5, TimeUnit.SECONDS);
	}

	/**
	 * Get File Size from url.
	 * 
	 * @param url
	 *            URL String like "www.google.com"
	 * @return
	 * @throws DownloaderException
	 */
	public static HttpFileAttributes getFileAttributes(String urlStr) throws DownloaderException {
		HttpURLConnection conn = null;

		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("HEAD");

			String file = url.getFile();
			System.out.println(file);
			String filename = Paths.get(url.getPath()).getFileName().toString();
			return new HttpFileAttributes(conn.getContentType(), conn.getContentLengthLong());
		} catch (IOException e) {
			throw new DownloaderException("Unable to check the size.", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	/**
	 * Get File Size from url.
	 * 
	 * @param url
	 *            URL String like "www.google.com"
	 * @return
	 * @throws DownloaderException
	 */
	public static String getFileContentType(String urlStr) throws DownloaderException {
		HttpURLConnection conn = null;

		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("HEAD");
			return conn.getContentType();
		} catch (IOException e) {
			throw new DownloaderException("Unable to check the size.", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

}
