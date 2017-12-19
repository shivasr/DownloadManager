/**
 * 
 */
package com.app.downloader.transfer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.app.downloader.api.exception.DownloaderException;

/**
 * @author shiva
 *
 */
public class HttpFileTransferClientTest {
	
	HTTPFileTransferClient httpTransferClient;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		httpTransferClient = new HTTPFileTransferClient("test.rebex.net");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.app.downloader.transfer.HTTPFileTransferClient#downloadFrom(java.lang.String, java.lang.String)}.
	 * @throws DownloaderException 
	 */
	@Test
	public final void testDownloadFrom() throws DownloaderException {
		String remoteFile = "https://vignette.wikia.nocookie.net/geosworld/images/0/09/Toon_link.jpg";
		String localFile = "/Users/shiva/Downloads/";
		HTTPFileTransferClient httpTransferClient = mock(HTTPFileTransferClient.class);
		doNothing().when(httpTransferClient).downloadFrom(remoteFile, localFile);
		httpTransferClient.downloadFrom(remoteFile, localFile);
	}

}
