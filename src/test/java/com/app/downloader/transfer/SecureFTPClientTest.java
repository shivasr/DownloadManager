/**
 * 
 */
package com.app.downloader.transfer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.app.downloader.api.exception.DownloaderException;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.FileMode;
import net.schmizz.sshj.sftp.PathComponents;
import net.schmizz.sshj.sftp.PathHelper;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.sftp.SFTPEngine;
import net.schmizz.sshj.sftp.SFTPFileTransfer;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.userauth.UserAuthException;
import net.schmizz.sshj.xfer.FileSystemFile;

/**
 * @author shiva
 *
 */
public class SecureFTPClientTest {
	
	SecureFTPClient secureFTPClient;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		secureFTPClient = new SecureFTPClient("host");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.app.downloader.transfer.SecureFTPClient#login(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testLogin() {
		secureFTPClient.login("demo-user", "demo-user");
		assertThat(secureFTPClient.getUsername(), equalTo("demo-user"));
		assertThat(secureFTPClient.getPassword(), equalTo("demo-user"));
		assertThat(secureFTPClient.useUserNamePwd, equalTo(true));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void testLogin_forNullUsername() {
		secureFTPClient.login(null, "demo-user");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void testLogin_forNullPassword_ThrowIllegalArgumentException() {
		secureFTPClient.login("demo-user", null);
		
	}

	/**
	 * Test method for {@link com.app.downloader.transfer.SecureFTPClient#loginWithKey(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testLoginWithKeyStringString() {
		secureFTPClient.loginWithKey("demo-user", "/Users/shiva/.ssh/id_rsa.pub");
		assertThat(secureFTPClient.getUsername(), equalTo("demo-user"));
		assertThat(secureFTPClient.getPassword(), equalTo(null));
		assertThat(secureFTPClient.pathToPublicKey, equalTo("/Users/shiva/.ssh/id_rsa.pub"));
		assertThat(secureFTPClient.usePublicKey, equalTo(true));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void testLoginWithKeyStringString_InputNullUsername_ThrowIllegalArgumentException() {
		secureFTPClient.login(null, "/Users/shiva/.ssh/id_rsa.pub");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void testLoginWithKeyStringString_InputNullPathToPublicKey_ThrowIllegalArgumentException() {
		secureFTPClient.login("demo-user", null);
	}

	/**
	 * Test method for {@link com.app.downloader.transfer.SecureFTPClient#loginWithKey(java.lang.String)}.
	 */
	@Test
	public final void testLoginWithKeyString() {
		secureFTPClient.loginWithKey("demo-user");
		assertThat(secureFTPClient.getUsername(), equalTo("demo-user"));
		assertThat(secureFTPClient.getPassword(), equalTo(null));
		assertThat(secureFTPClient.pathToPublicKey, equalTo(null));
		assertThat(secureFTPClient.useDefaultKey, equalTo(true));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void testLoginWithKeyString_InputNullUsername_ThrowIllegalArgumentException() {
		secureFTPClient.loginWithKey(null);
	}
	

	/**
	 * Test method for {@link com.app.downloader.transfer.SecureFTPClient#loginWithPEMKey(java.lang.String, java.lang.String)}.
	 */
	/**
	 * Test method for {@link com.app.downloader.transfer.SecureFTPClient#loginWithKey(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testLoginWithPEMKey() {
		secureFTPClient.loginWithPEMKey("demo-user", "/Users/shiva/shiva.pem");
		assertThat(secureFTPClient.getUsername(), equalTo("demo-user"));
		assertThat(secureFTPClient.getPassword(), equalTo(null));
		assertThat(secureFTPClient.pathToPEMKeyFile, equalTo("/Users/shiva/shiva.pem"));
		assertThat(secureFTPClient.usePemKey, equalTo(true));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void loginWithPEMKey_InputNullUsername_ThrowIllegalArgumentException() {
		secureFTPClient.login(null, "/Users/shiva/.ssh/id_rsa.pub");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void loginWithPEMKey_InputNullPathToPublicKey_ThrowIllegalArgumentException() {
		secureFTPClient.login("demo-user", null);
	}

	/**
	 * Test method for {@link com.app.downloader.transfer.SecureFTPClient#downloadFrom(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testDownloadFrom() {
		secureFTPClient = new SecureFTPClient("test.rebex.net");
		secureFTPClient.login("demo-user", "demo-user");
		try {
			
			SSHClient ssh = mock(SSHClient.class);
			secureFTPClient.setSsh(ssh);
			
			SFTPClient sftp = mock(SFTPClient.class);
			SFTPEngine engine = mock(SFTPEngine.class);
			PathHelper pathHelper = mock(PathHelper.class);
			PathComponents pathComponents = new PathComponents("", "ConsoleClientSmall.png", "/");
			final String path = "ftp://test.rebex.net:21/pub/example/ConsoleClientSmall.png";
			when(pathHelper.getComponents(path)).thenReturn(pathComponents);
			when(engine.getPathHelper()).thenReturn(pathHelper);
			SFTPFileTransfer sftpFileTransfer = mock(SFTPFileTransfer.class);
			Session session = mock(Session.class);
			// final PathComponents pathComponents = sftp.getSFTPEngine().getPathHelper().getComponents(remoteFile);
			when(sftp.getFileTransfer()).thenReturn(sftpFileTransfer);
			when(sftp.getSFTPEngine()).thenReturn(engine);
			when(ssh.newSFTPClient()).thenReturn(sftp);
			
			doNothing().when(ssh).authPassword("demo-user", "demo-user");
			doNothing().when(ssh).connect("test.rebex.net");
			when(ssh.startSession()).thenReturn(session);
			when(sftp.stat(path)).thenReturn(new FileAttributes(0, 1, 0, 0, new FileMode(0), 0, 0, new HashMap<String, String>()));
			doNothing().when(sftpFileTransfer).download(path, new FileSystemFile("/Users/shiva/Downloads"));
			
			secureFTPClient.downloadFrom(path, "/Users/shiva/Downloads");
		} catch (DownloaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UserAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Test method for {@link com.app.downloader.transfer.SecureFTPClient#downloadFrom(java.lang.String, java.lang.String)}.
	 * @throws DownloaderException 
	 */
	@Test(expected=DownloaderException.class)
	public final void testDownloadFrom_NetworkDown_ThrowsDownloadException() throws DownloaderException {
		secureFTPClient = new SecureFTPClient("test.rebex.net");
		secureFTPClient.login("demo-user", "demo-user");
		try {
			
			SSHClient ssh = mock(SSHClient.class);
			secureFTPClient.setSsh(ssh);
			
			SFTPClient sftp = mock(SFTPClient.class);
			SFTPEngine engine = mock(SFTPEngine.class);
			PathHelper pathHelper = mock(PathHelper.class);
			PathComponents pathComponents = new PathComponents("", "ConsoleClientSmall.png", "/");
			final String path = "sftp://test.rebex.net:22/pub/example/ConsoleClientSmall.png";
			when(pathHelper.getComponents(path)).thenReturn(pathComponents);
			when(engine.getPathHelper()).thenReturn(pathHelper);
			SFTPFileTransfer sftpFileTransfer = mock(SFTPFileTransfer.class);
			Session session = mock(Session.class);
			// final PathComponents pathComponents = sftp.getSFTPEngine().getPathHelper().getComponents(remoteFile);
			when(sftp.getFileTransfer()).thenReturn(sftpFileTransfer);
			when(sftp.getSFTPEngine()).thenReturn(engine);
			when(ssh.newSFTPClient()).thenReturn(sftp);
			
			doNothing().when(ssh).authPassword("demo-user", "demo-user");
			doNothing().when(ssh).connect("test.rebex.net");
			when(ssh.startSession()).thenReturn(session);
			when(sftp.stat(path)).thenReturn(new FileAttributes(0, 1, 0, 0, new FileMode(0), 0, 0, new HashMap<String, String>()));
			
			doThrow(new IOException("Encountered EOF, could not transfer ** bytes"))
					.when(sftpFileTransfer)
					.download(path, new FileSystemFile("/Users/shiva/Downloads"));
			
			secureFTPClient.downloadFrom(path, "/Users/shiva/Downloads");
		} catch (DownloaderException e) {
			throw e;
		} catch (UserAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
