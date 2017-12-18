/**
 * 
 */
package com.app.downloader;

import java.io.File;
import java.io.FileNotFoundException;
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

	public DownloadManager buildDownloadManagerWithUserNamePwdd(String username, String pasword) {
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

		int indexOfProtocolSeperator = source.indexOf(PROTOCOL_SEPERATOR);
		String protocol = source.substring(0, indexOfProtocolSeperator);
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
			rfile = source.substring(source.indexOf('/', beginningIndexOfHost) + 1);

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
		Logger.debugLine("Downloading the file: " + rfile + " to " + target);
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String username = "";
		String password = "";
		String pemPrivateKeyLocation = "";
		String publicKeyLocation = "";
		String usePEMPrivateKey = "";

		// DownloadManager downloadManager = new DownloadManager()
		// .buildDownloadManagerWithPEMKey("ec2-user", "/Users/shiva/shiva.pem");
		// downloadManager.startDownload("sftp://ec2-34-227-108-143.compute-1.amazonaws.com/home/ec2-user/JobZtop.zip",
		// "/Users/shiva/Downloads/");
		// downloadManager.startDownload("ftp://demo.wftpserver.com/download/manual_en.pdf",
		// "/Users/shiva/Downloads/");
		FTPClientFactory.registerClients("HTTP", "com.app.downloader.transfer.HTTPFileTransferClient");
		FTPClientFactory.registerClients("HTTPS", "com.app.downloader.transfer.HTTPFileTransferClient");
		// DownloadManager downloadManager = new
		// DownloadManager().buildDownloadManagerWithUserNamePwdd("demo-user",
		// "demo-user");
		// downloadManager.startDownload("https://vignette.wikia.nocookie.net/geosworld/images/0/09/Toon_link.jpg",
		// "/Users/shiva/Downloads/");

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
				location = getValueFromUser(varName, varDescription);
				System.setProperty(ENV_LOCATION, location);
			}

			username = getValueFromUser("USERNAME", "user id to login into FTP servers.");
			password = getValueFromUser("PASSWORD", "password to login into FTP servers.");

			if (cmdLine.hasOption(OPTION_LOCATION)) {
				location = cmdLine.getOptionValue(OPTION_LOCATION);
				System.setProperty(ENV_LOCATION, location);
			} else {
				String varName = ENV_LOCATION;
				String varDescription = ENV_DOWNLOAD_LOCATION;
				location = getValueFromUser(varName, varDescription);
			}

			Scanner scanner = null;
			try {
				scanner = new Scanner(new File(file));
			} catch (FileNotFoundException e) {
				System.out.println("Error: Unable to open file: " + file);
			}
			while (scanner.hasNextLine()) {
				String url = scanner.nextLine();

				executeDownloadTask(url, location, username, password);
			}
			scanner.close();
		} else {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Missing file option. \n\nUSAGE: java -jar download_manager.jar", options);
		}
	}

	private static void executeDownloadTask(String url, String location, String username, String password) {

		try {
			DownloadManager downloadManager = null;
			if (url.startsWith(SFTP)) {
				if (Utilities.isNullOrEmpty(password))
					downloadManager = new DownloadManager().buildDownloadManagerWithDefaultKey(username);
				else
					downloadManager = new DownloadManager().buildDownloadManagerWithUserNamePwdd(username, password);
			} else if (url.startsWith(FTP)) {
				downloadManager = new DownloadManager().buildDownloadManagerWithUserNamePwdd(username, password);
			} else if (url.startsWith(HTTP)) {
				downloadManager = new DownloadManager().buildDownloadManagerWithUserNamePwdd(username, password);
			}

			downloadManager.startDownload(url, location);
		} catch (DownloaderException e) {
			System.out.println(String.format("Download for the URL %s failed with error: %s", url, e.getMessage()));
		}
	}

	private static String getValueFromUser(String varName, String varDescription) {
		String location;
		location = System.getenv(varName);

		if (Utilities.isNullOrEmpty(location)) {
			System.out.println(String.format("Please enter the %s:\n", varDescription));
			location = Keyin.inString();
		}
		return location;
	}

}
