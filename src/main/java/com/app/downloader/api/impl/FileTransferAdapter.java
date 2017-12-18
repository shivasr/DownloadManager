/**
 * 
 */
package com.app.downloader.api.impl;

import java.io.IOException;

import com.app.downloader.api.DownloadTracker;
import com.app.downloader.api.ProgressListener;

import net.schmizz.sshj.common.StreamCopier;
import net.schmizz.sshj.xfer.TransferListener;

/**
 * @author shiva
 *
 */
public class FileTransferAdapter implements TransferListener {

	private ProgressListener transferListener;

	public FileTransferAdapter(ProgressListener transferListener) {
		this.transferListener = transferListener;
	}

	@Override
	public TransferListener directory(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StreamCopier.Listener file(final String name, final long size) {
		return new StreamCopier.Listener() {
			@Override
			public void reportProgress(long transferred) throws IOException {
					 transferListener.reportProgress(transferred, size, name);
			}
		};
	}

	/**
	 * @return the transferListener
	 */
	public ProgressListener getTransferListener() {
		return transferListener;
	}

	/**
	 * @param transferListener
	 *            the transferListener to set
	 */
	public void setTransferListener(ProgressListener transferListener) {
		this.transferListener = transferListener;
	}

}
