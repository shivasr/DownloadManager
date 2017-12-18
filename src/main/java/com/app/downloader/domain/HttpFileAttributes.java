/**
 * 
 */
package com.app.downloader.domain;

/**
 * This class is used to encapsulate the file attributes
 * @author shiva
 *
 */
public class HttpFileAttributes {

	private String contentType;

	private long size;

	/**
	 * 
	 */
	public HttpFileAttributes(String contentType, long size) {
		this.contentType = contentType;
		this.size = size;
	}
	

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}

}
