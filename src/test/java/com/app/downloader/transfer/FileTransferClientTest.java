/**
 * 
 */
package com.app.downloader.transfer;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.nio.file.Files;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.app.downloader.api.exception.DownloaderException;

/**
 * @author shiva
 *
 */
public class FileTransferClientTest {
	FileTransferClient fileTransferClient;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		fileTransferClient = new FileTransferClient("test.rebex.net");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.app.downloader.transfer.FileTransferClientTest#downloadFrom(java.lang.String, java.lang.String)}.
	 * @throws DownloaderException 
	 * @throws IOException 
	 * @throws SocketException 
	 */
	@Test
	public final void testDownloadFrom() throws DownloaderException, SocketException, IOException {
		FTPClient ftp = mock(FTPClient.class);
		final String localFile = "/Users/shiva/Downloads";
		final String path = "ftp://test.rebex.net:21/pub/example/ConsoleClientSmallxxx.png";
		String fullpath = localFile + path.substring(path.lastIndexOf("/"));
		doNothing().when(ftp).addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		doNothing().when(ftp).connect("test.rebex.net");
		when(ftp.login("demo-user", "demo-user")).thenReturn(true);
		when(ftp.setFileType(FTP.BINARY_FILE_TYPE)).thenReturn(true);
		doNothing().when(ftp).enterLocalPassiveMode();
		
		when(ftp.getReplyCode()).thenReturn(0);
		
		FileOutputStream fos = new FileOutputStream(fullpath);
		
		Files.delete(new File(fullpath).toPath());
		
		when(ftp.retrieveFile(path, fos))
			.thenReturn(true);
		
		fileTransferClient.setFtp(ftp);
		
		fileTransferClient.downloadFrom(path, localFile);
	}
	
	@Test(expected=DownloaderException.class)
	public final void testDownloadFrom_HostNotAvailable_ThrowsDownloaderException() throws DownloaderException {
		FTPClient ftp = mock(FTPClient.class);
		final String localFile = "/Users/shiva/Downloads";
		final String path = "ftp://test.rebex.net:21/pub/example/ConsoleClientSmall.png";
		String fullpath = localFile + path.substring(path.lastIndexOf("/"));
		doNothing().when(ftp).addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		try {
			doThrow(new IOException("")).when(ftp).connect("test.rebex.net");
			
			when(ftp.login("demo-user", "demo-user")).thenReturn(true);
			when(ftp.setFileType(FTP.BINARY_FILE_TYPE)).thenReturn(true);
			doNothing().when(ftp).enterLocalPassiveMode();
			
			when(ftp.getReplyCode()).thenReturn(0);
			
			FileOutputStream fos = new FileOutputStream(fullpath);
			
			when(ftp.retrieveFile(path, fos)).thenReturn(true);
			
			fileTransferClient.setFtp(ftp);
			
			fileTransferClient.downloadFrom(path, localFile);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test(expected=DownloaderException.class)
	public final void testDownloadFrom_Connected_ButConnectionInterupted_ThrowsDownloaderException() throws DownloaderException {
		FTPClient ftp = mock(FTPClient.class);
		final String localFile = "/Users/shiva/Downloads";
		final String path = "ftp://test.rebex.net:21/pub/example/ConsoleClientSmall.png";
		String fullpath = localFile + path.substring(path.lastIndexOf("/"));
		doNothing().when(ftp).addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		try {
			doNothing().when(ftp).connect("test.rebex.net");
			
			when(ftp.login("demo-user", "demo-user")).thenReturn(true);
			when(ftp.setFileType(FTP.BINARY_FILE_TYPE)).thenReturn(true);
			doNothing().when(ftp).enterLocalPassiveMode();
			
			when(ftp.getReplyCode()).thenReturn(0);
			
			FileOutputStream fos = new FileOutputStream(fullpath);
			
			when(ftp.retrieveFile(path, fos)).thenThrow(new IOException());
			
			
			fileTransferClient.setFtp(ftp);
			
			fileTransferClient.downloadFrom(path, localFile);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
