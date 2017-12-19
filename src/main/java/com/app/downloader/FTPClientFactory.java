/**
 * 
 */
package com.app.downloader;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.app.downloader.api.IFTPInterface;
import com.app.downloader.logger.Logger;
import com.app.downloader.transfer.SecureFTPClient;

/**
 * This is the factory class to create appropriate FTP client implementations based on protocol.
 * 
 * @author Shivakumar Ramannavar
 *
 */
public class FTPClientFactory {
	
	private static Map<String, String> registry; 
	
	static {
		setRegistry(new HashMap<>());
		getRegistry().put("SFTP", "com.app.downloader.transfer.SecureFTPClient");
		getRegistry().put("FTP", "com.app.downloader.transfer.FileTransferClient");
	
	}

	/**
	 * 
	 */
	private FTPClientFactory() {
		// Do Nothing
	}
	
	public static void registerClients(String protocol, String fullyQualifiedClassName) {
		getRegistry().put(protocol, fullyQualifiedClassName);
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
		
		String classNameToBeLoaded = getRegistry().get(protocol.toUpperCase());
		
		Optional.ofNullable(classNameToBeLoaded).orElseThrow(
				() -> new UnsupportedOperationException(String.format("No client registered for the protocol: %s", protocol)));
		
		IFTPInterface iftpInterface =  null;
		Class<?> myClass;
		try {
			myClass = Class.forName(classNameToBeLoaded);
			iftpInterface = (IFTPInterface) myClass
								.getConstructor(String.class)
								.newInstance(host);
		} catch (ClassNotFoundException e) {
			throw new UnsupportedOperationException(String.format("Client class not in classpath for the protocol: %s", protocol));
		} catch (InstantiationException e) {
			throw new UnsupportedOperationException(String.format("Unable to load class with name %s for the protocol: %s. Error: %s", classNameToBeLoaded, protocol, e.getMessage()));
		} catch (IllegalAccessException e) {
			String.format("Unable to load class with name %s for the protocol: %s. Error: %s", classNameToBeLoaded, protocol, e.getMessage());
			throw new UnsupportedOperationException(String.format("Unable to load class with name %s for the protocol: %s. Error: %s", classNameToBeLoaded, protocol, e.getMessage()));
		} catch (IllegalArgumentException e) {
			String.format("Unable to load class with name %s for the protocol: %s. Error: %s", classNameToBeLoaded, protocol, e.getMessage());
			throw new UnsupportedOperationException(String.format("Unable to load class with name %s for the protocol: %s. Error: %s", classNameToBeLoaded, protocol, e.getMessage()));
		} catch (InvocationTargetException e) {
			
			String.format("Unable to load class with name %s for the protocol: %s. Error: %s", classNameToBeLoaded, protocol, e.getMessage());
			new UnsupportedOperationException(String.format("Unable to load class with name %s for the protocol: %s. Error: %s", classNameToBeLoaded, protocol, e.getMessage()));
		} catch (NoSuchMethodException e) {
			String.format("Unable to load class with name %s for the protocol: %s. Error: %s", classNameToBeLoaded, protocol, e.getMessage());
			new UnsupportedOperationException(String.format("Unable to load class with name %s for the protocol: %s. Error: %s", classNameToBeLoaded, protocol, e.getMessage()));
		} catch (SecurityException e) {
			String.format("Unable to load class with name %s for the protocol: %s. Error: %s", classNameToBeLoaded, protocol, e.getMessage());
			new UnsupportedOperationException(String.format("Unable to load class with name %s for the protocol: %s. Error: %s", classNameToBeLoaded, protocol, e.getMessage()));
		}
		
		return iftpInterface;
	}

	public static Map<String, String> getRegistry() {
		return registry;
	}

	public static void setRegistry(Map<String, String> registry) {
		FTPClientFactory.registry = registry;
	}

}
