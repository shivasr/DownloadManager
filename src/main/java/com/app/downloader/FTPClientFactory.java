/**
 * 
 */
package com.app.downloader;

import com.app.downloader.api.IFTPInterface;
import com.app.downloader.transfer.SecureFTPClient;

/**
 * This is the factory class to create appropriate FTP client implementations based on protocol.
 * 
 * @author Shivakumar Ramannavar
 *
 */
public class FTPClientFactory {

	/**
	 * 
	 */
	private FTPClientFactory() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * This method creates implementations of IFTPInterface used a a client for File Transfers based on protocol.
	 * Tshi method also initializes the client with the host ot be connected.
	 * 
	 * @param protocol protocol like HTTP, FTP, SFTP
	 * @param host host to which the client has to be configured.
	 * @return
	 */
	public static final IFTPInterface createFTPClient(String protocol, String host) {
		
		EnumProtocol enumProtocol = EnumProtocol.valueOf(protocol.toUpperCase());
		
		switch (enumProtocol) {
		case SFTP:
			return new SecureFTPClient(host);

		default:
			break;
		}
		return null;
	}

}
