/**
 * 
 */
package com.app.downloader.util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
	 * @param session Session object containing the session to the SFTP server.
	 * 
	 * @throws ConnectionException
	 * @throws TransportException
	 * @throws IOException
	 */
	public static void listDirectory(Session session)
			throws ConnectionException, TransportException, IOException {
		final Command cmd = session.exec("ls -l");
		
		Logger.debug(IOUtils.readFully(cmd.getInputStream()).toString());
		cmd.join(5, TimeUnit.SECONDS);
	}

}
