/**
 * 
 */
package com.app.downloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.app.downloader.api.IDownloader;
import com.app.downloader.api.IFTPInterface;
import com.app.downloader.api.exception.DownloaderException;
import com.app.downloader.api.impl.DownloadTrackerImpl;
import com.app.downloader.logger.Logger;
import com.app.downloader.util.Utilities;

/**
 * @author Shivakumar Ramannavar
 *
 */
public class DownloadManager implements IDownloader {

	private static final String PASSWORD = "PASSWORD";

	private static final String ENV_USERNAME = "USERNAME";

	private static final String OPTION_FILE = "file";

	private static final String FTP = "ftp";

	private static final String OPTION_LOCATION = "location";

	private static final String ENV_DOWNLOAD_LOCATION = "download location";

	private static final String ENV_LOCATION = "LOCATION";

	private static final String SFTP = "sftp";

	private static final String PROTOCOL_SEPERATOR = "://";

	private static final String HTTP = "http";

	private String pathToPEMPrivateKey;

	private String pathToPublicKey;

	private String username;

	private String password;

	private IFTPInterface ftpInterface;

	private boolean useDefaultPublicKey = false;;

	public DownloadManager buildDownloadManagerWithDefaultKey(String username) {
		this.username = (username == null) ? "" : username;
		this.useDefaultPublicKey = true;
		return this;
	}

	public DownloadManager buildDownloadManagerWithpublicKey(String username, String pathToPublicKey) {
		this.username = (username == null) ? "" : username;
		this.pathToPublicKey = pathToPublicKey;

		return this;
	}

	public DownloadManager buildDownloadManagerWithPEMKey(String username, String pathToPEMFile) {
		this.username = (username == null) ? "" : username;
		this.pathToPEMPrivateKey = pathToPEMFile;

		return this;
	}

	public DownloadManager buildDownloadManagerWithEmptyCredentials() {
		return this;
	}

	public DownloadManager buildDownloadManagerWithUserNamePwd(String username, String pasword) {
		this.username = (username == null) ? "" : username;
		this.password = pasword;

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.app.donwloader.transfer.IDownloader#startDownload(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean startDownload(String source, String target) throws DownloaderException {

		Optional.ofNullable(source).orElseThrow(() -> new DownloaderException("Source URL is null."));
		Optional.ofNullable(target).orElseThrow(() -> new DownloaderException("Download location is null."));
		
		int indexOfProtocolSeperator = source.indexOf(PROTOCOL_SEPERATOR);
		
		if(indexOfProtocolSeperator == -1)
			throw new DownloaderException("Malformed URL value: " + source);
		
		String protocol = source.substring(0, indexOfProtocolSeperator);
		
		if(Utilities.isNullOrEmpty(protocol))
			throw new DownloaderException("Malformed URL value: " + source + ". Protocol is not given.");
		
		
		int beginningIndexOfHost = indexOfProtocolSeperator + 3;
		String host = source.substring(beginningIndexOfHost, source.indexOf("/", beginningIndexOfHost));
		String port = null;

		if (host.contains(":")) {
			String[] tokens = host.split(":");
			host = tokens[0];
			port = tokens[1];
		}
		String rfile = source;

		if (protocol.toUpperCase().contains("FTP"))
			rfile = source.substring(source.indexOf('/', beginningIndexOfHost));

		ftpInterface = FTPClientFactory.createFTPClient(protocol, host);
		ftpInterface.registerDownloadTracker(new DownloadTrackerImpl(System.currentTimeMillis()));

		if (port != null)
			ftpInterface.overridePort(Integer.parseInt(port));

		Logger.debugLine("Logging in ...");
		// ftpInterface.login(user, "password");

		if (pathToPEMPrivateKey != null)
			ftpInterface.loginWithPEMKey(username, pathToPEMPrivateKey);
		else if (pathToPublicKey != null)
			ftpInterface.loginWithKey(username, pathToPublicKey);
		else if (useDefaultPublicKey)
			ftpInterface.loginWithKey(username);
		else if (!Utilities.isNullOrEmpty(username))
			ftpInterface.login(username, password);

		Logger.debugLine("Done.");
		Logger.infoLine("Downloading the file: " + rfile + " to " + target + ".");
		ftpInterface.downloadFrom(rfile, target);
		Logger.debugLine("Done.");

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.app.donwloader.transfer.IDownloader#getProgress(int)
	 */
	@Override
	public DownloadStatus getProgress(int token) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPathToPEMPrivateKey() {
		return pathToPEMPrivateKey;
	}

	public void setPathToPEMPrivateKey(String pathToPEMPrivateKey) {
		this.pathToPEMPrivateKey = pathToPEMPrivateKey;
	}

	public String getPathToPublicKey() {
		return pathToPublicKey;
	}

	public void setPathToPublicKey(String pathToPublicKey) {
		this.pathToPublicKey = pathToPublicKey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isUseDefaultPublicKey() {
		return useDefaultPublicKey;
	}

	public void setUseDefaultPublicKey(boolean useDefaultPublicKey) {
		this.useDefaultPublicKey = useDefaultPublicKey;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String username = null;
		String password = null;
		
		String pathToPEMPrivateKey = null;
		String pathToPublicKey = null;
		
		
		// Sample to add handlers dynamically
		FTPClientFactory.registerClients("HTTP", "com.app.downloader.transfer.HTTPFileTransferClient");
		FTPClientFactory.registerClients("HTTPS", "com.app.downloader.transfer.HTTPFileTransferClient");

		Option helpOption = Option.builder("h").longOpt("help").required(false).desc("help").build();

		Option fileOption = Option.builder("f").longOpt(OPTION_FILE).numberOfArgs(1).required(false)
				.desc("File containing list of sources.").build();

		Option locOption = Option.builder("loc").longOpt(OPTION_LOCATION).numberOfArgs(1).required(false)
				.desc("Location on disk where the files have to be downloaded.").build();

		Options options = new Options();
		options.addOption(helpOption);
		options.addOption(fileOption);
		options.addOption(locOption);

		CommandLineParser parser = new DefaultParser();
		CommandLine cmdLine = null;
		try {
			cmdLine = parser.parse(options, args);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (cmdLine.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar download_manager.jar", options);
		} else if (cmdLine.hasOption(OPTION_FILE)) {
			String file = cmdLine.getOptionValue(OPTION_FILE);
			String location = "";

			if (cmdLine.hasOption(OPTION_LOCATION)) {
				location = cmdLine.getOptionValue(OPTION_LOCATION);
				System.setProperty(ENV_LOCATION, location);
			} else {
				String varName = ENV_LOCATION;
				String varDescription = ENV_DOWNLOAD_LOCATION;
				location = getValueFromUser(varDescription);
				System.setProperty(ENV_LOCATION, location);
			}
			
			if(!new File(file).canRead()) {
				System.out.println("\nFailed. Error: The input file : `" + file + "` does not exists or not readeable.");
				System.exit(-1);
			}
			
			if(!new File(location).canWrite()) {
				System.out.println("\nFailed. Error: The location: `" + location + "` is not writeable.");
				System.exit(-1);
			}

			username = getValueFromUser("user id to login into FTP servers.");
			password = getValueFromUser("password to login into FTP servers. Skip and press enter if you do want to use keys.");
			
			if(Utilities.isNullOrEmpty(password)) {
				pathToPEMPrivateKey = getValueFromUser("Path to PEM private key to login into FTP servers. Skip and press enter if you do want to use default public keys.");
			}
				

			Scanner scanner = null;
			try {
				scanner = new Scanner(new File(file));
				
				while (scanner.hasNextLine()) {
					String url = scanner.nextLine();

					executeDownloadTask(url, location, username, password, pathToPEMPrivateKey);
				}
			} catch (FileNotFoundException e) {
				System.out.println("\nFailed. Error: Unable to open file: " + file + ".");
				System.exit(-1);
			} finally {
				if(scanner != null)
					scanner.close();
			}
			
			
		} else {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Missing file option. \n\nUSAGE: java -jar download_manager.jar", options);
		}
	}

	private static void executeDownloadTask(String url, String location, String username, String password, String pathToPEMPrivateKey) {

		try {
			DownloadManager downloadManager = null;
			if (url.startsWith(SFTP)) {
				if (!Utilities.isNullOrEmpty(pathToPEMPrivateKey))
					downloadManager = new DownloadManager().buildDownloadManagerWithPEMKey(username, pathToPEMPrivateKey);
				else if (Utilities.isNullOrEmpty(password))
					downloadManager = new DownloadManager().buildDownloadManagerWithDefaultKey(username);
				else
					downloadManager = new DownloadManager().buildDownloadManagerWithUserNamePwd(username, password);
			} else if (url.startsWith(FTP)) {
				downloadManager = new DownloadManager().buildDownloadManagerWithUserNamePwd(username, password);
			} else if (url.startsWith(HTTP)) {
				downloadManager = new DownloadManager().buildDownloadManagerWithUserNamePwd(username, password);
			}

			downloadManager.startDownload(url, location);
			System.out.println("\n\n" + String.format("Success. Download for the URL %s succeeded.", url) + "\n\n");
		} catch (DownloaderException e) {
			System.out.println(
					"\n\n" + String.format("Failed. Download for the URL %s failed with error: %s", url, e.getMessage())
							+ "\n\n");
		}
	}

	private static String getValueFromUser(String varDescription) {
		System.out.println(String.format("Please enter the %s:\n", varDescription));
		String value = Keyin.inString();
		return value;
	}
}
