/**
 * 
 */
package com.app.downloader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.app.downloader.api.IFTPInterface;
import com.app.downloader.transfer.SecureFTPClient;

/**
 * @author shiva
 *
 */
public class FTPClientFactoryTest {

	private static final String TEST_PROTOCOL = "SFTP";
	private static final String DUMMY_HOST = "host";

	/**
	 * Test method for {@link com.app.downloader.FTPClientFactory#registerClients(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testRegisterClients() {
		final String className = SecureFTPClient.class.getName();
		FTPClientFactory.registerClients(TEST_PROTOCOL, className);
		assertThat(FTPClientFactory.getRegistry().get(TEST_PROTOCOL), equalTo(className));
	}

	/**
	 * Test method for {@link com.app.downloader.FTPClientFactory#createFTPClient(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testCreateFTPClient() {
		final String className = SecureFTPClient.class.getName();
		FTPClientFactory.registerClients(TEST_PROTOCOL, className);
		
		final IFTPInterface ftpClient = FTPClientFactory.createFTPClient(TEST_PROTOCOL, DUMMY_HOST);
		
		assertThat(ftpClient, is(notNullValue()));
		
		assertThat(ftpClient.getClass().getName(), equalTo(className));
	}

}
