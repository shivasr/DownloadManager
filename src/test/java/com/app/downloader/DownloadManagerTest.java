/**
 * 
 */
package com.app.downloader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;


import org.junit.Before;
import org.junit.Test;

import com.app.downloader.api.IFTPInterface;
import com.app.downloader.api.exception.DownloaderException;

/**
 * @author shiva
 *
 */
public class DownloadManagerTest {
	
	private static final String DEMO_USER = "demo-user";
	DownloadManager downloadManager = null;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		downloadManager = new DownloadManager();
	}

	/**
	 * Test method for {@link com.app.downloader.DownloadManager#buildDownloadManagerWithDefaultKey(java.lang.String)}.
	 */
	@Test
	public final void testBuildDownloadManagerWithDefaultKey() {
		downloadManager.buildDownloadManagerWithDefaultKey(DEMO_USER);
		assertThat(downloadManager.getUsername(), equalTo(DEMO_USER));
		assertThat(downloadManager.getPassword(), equalTo(null));
		assertThat(downloadManager.isUseDefaultPublicKey(), equalTo(true));
	}
	
	@Test
	public final void testBuildDownloadManagerWithDefaultKey_UsernameNull_ValidScenario_setsUsernameToEmptySpace() {
		downloadManager.buildDownloadManagerWithDefaultKey(null);
		assertThat(downloadManager.getUsername(), equalTo(""));
		assertThat(downloadManager.getPassword(), equalTo(null));
		assertThat(downloadManager.isUseDefaultPublicKey(), equalTo(true));
	}

	/**
	 * Test method for {@link com.app.downloader.DownloadManager#buildDownloadManagerWithpublicKey(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testBuildDownloadManagerWithpublicKey() {
		downloadManager.buildDownloadManagerWithpublicKey("demo-user", "/Users/shiva/.ssh/id_rsa.pub");
		assertThat(downloadManager.getUsername(), equalTo("demo-user"));
		assertThat(downloadManager.getPassword(), equalTo(null));
		assertThat(downloadManager.getPathToPublicKey(), equalTo("/Users/shiva/.ssh/id_rsa.pub"));
	}

	/**
	 * Test method for {@link com.app.downloader.DownloadManager#buildDownloadManagerWithPEMKey(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testBuildDownloadManagerWithPEMKey() {
		downloadManager.buildDownloadManagerWithPEMKey("demo-user", "/Users/shiva/shiva.pem");
		assertThat(downloadManager.getUsername(), equalTo("demo-user"));
		assertThat(downloadManager.getPassword(), equalTo(null));
		assertThat(downloadManager.getPathToPEMPrivateKey(), equalTo("/Users/shiva/shiva.pem"));
	
	}

	/**
	 * Test method for {@link com.app.downloader.DownloadManager#buildDownloadManagerWithEmptyCredentials()}.
	 */
	@Test
	public final void testBuildDownloadManagerWithEmptyCredentials() {
		downloadManager.buildDownloadManagerWithEmptyCredentials();
		assertThat(downloadManager.getUsername(), equalTo(null));
		assertThat(downloadManager.getPassword(), equalTo(null));
	
	}

	/**
	 * Test method for {@link com.app.downloader.DownloadManager#buildDownloadManagerWithUserNamePwd(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testBuildDownloadManagerWithUserNamePwd() {
		downloadManager.buildDownloadManagerWithUserNamePwd(DEMO_USER, DEMO_USER);
		assertThat(downloadManager.getUsername(), equalTo(DEMO_USER));
		assertThat(downloadManager.getPassword(), equalTo(DEMO_USER));
		assertThat(downloadManager.isUseDefaultPublicKey(), equalTo(false));
	}

	/**
	 * Test method for {@link com.app.downloader.DownloadManager#startDownload(java.lang.String, java.lang.String)}.
	 * @throws DownloaderException 
	 */
	@Test
	public final void testStartDownload() throws DownloaderException {
		downloadManager.buildDownloadManagerWithUserNamePwd(DEMO_USER, DEMO_USER);
		String source = "ftp://test.rebex.net:21/pub/example/ConsoleClientSmall.png";
		String target = "/Users/shiva/Downloads";
		
		IFTPInterface iftpInterface = mock(IFTPInterface.class);
		doNothing().when(iftpInterface).downloadFrom(source, target);
		downloadManager.startDownload(source, target);
	}

	/**
	 * Test method for {@link com.app.downloader.DownloadManager#startDownload(java.lang.String, java.lang.String)}.
	 * @throws DownloaderException 
	 */
	@Test(expected=DownloaderException.class)
	public final void testStartDownload_NullSourceURL_ThrowsDownloadException() throws DownloaderException {
		downloadManager.buildDownloadManagerWithUserNamePwd(DEMO_USER, DEMO_USER);
		String source = null;
		String target = "/Users/shiva/Downloads";
		
		IFTPInterface iftpInterface = mock(IFTPInterface.class);
		doNothing().when(iftpInterface).downloadFrom(source, target);
		downloadManager.startDownload(source, target);
	}
	/**
	 * Test method for {@link com.app.downloader.DownloadManager#startDownload(java.lang.String, java.lang.String)}.
	 * @throws DownloaderException 
	 */
	@Test(expected=DownloaderException.class)
	public final void testStartDownload_MallPerformedSourceURL_ThrowsDownloadException() throws DownloaderException {
		downloadManager.buildDownloadManagerWithUserNamePwd(DEMO_USER, DEMO_USER);
		String source = "://test.rebex.net:21/pub/example/ConsoleClientSmall.png";
		String target = "/Users/shiva/Downloads";
		
		IFTPInterface iftpInterface = mock(IFTPInterface.class);
		doNothing().when(iftpInterface).downloadFrom(source, target);
		downloadManager.startDownload(source, target);
	}

	/**
	 * Test method for {@link com.app.downloader.DownloadManager#startDownload(java.lang.String, java.lang.String)}.
	 * @throws DownloaderException 
	 */
	@Test(expected=DownloaderException.class)
	public final void testStartDownload_NullDownloadLocationURL_ThrowsDownloadException() throws DownloaderException {
		downloadManager.buildDownloadManagerWithUserNamePwd(DEMO_USER, DEMO_USER);
		String source = "ftp://test.rebex.net:21/pub/example/ConsoleClientSmall.png";;
		String target = null;
		
		IFTPInterface iftpInterface = mock(IFTPInterface.class);
		doNothing().when(iftpInterface).downloadFrom(source, target);
		downloadManager.startDownload(source, target);
	}
}