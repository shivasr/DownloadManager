/**
 * 
 */
package com.app.downloader.api.impl;

import java.io.IOException;

import com.app.downloader.logger.Logger;

import net.schmizz.sshj.common.StreamCopier;
import net.schmizz.sshj.common.StreamCopier.Listener;
import net.schmizz.sshj.xfer.TransferListener;

/**
 * @author shiva
 *
 */
public class CustomTransferListener implements TransferListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.schmizz.sshj.xfer.TransferListener#directory(java.lang.String)
	 */
	@Override
	public TransferListener directory(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.schmizz.sshj.xfer.TransferListener#file(java.lang.String, long)
	 */
	@Override
	public Listener file(String name, long size) {
		Logger.debugLine(String.format("started transferring file `%s` (%s MB)", name, size/(1024 * 1024)));
		return new StreamCopier.Listener() {
			@Override
			public void reportProgress(long transferred) throws IOException {
				Logger.debugLine(String.format("transferred %s %% of `%s`", ((transferred * 100) / size), name));
			}
		};
	}

}
