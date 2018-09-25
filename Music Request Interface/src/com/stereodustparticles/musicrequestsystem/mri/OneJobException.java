package com.stereodustparticles.musicrequestsystem.mri;

public class OneJobException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7357161040990975239L;

	/**
	 * 
	 */

	public OneJobException() {
		super("Item not present");
	}

	public OneJobException(String arg0) {
		super(arg0);
	}

	public OneJobException(Throwable cause) {
		super(cause);
	}

	public OneJobException(String message, Throwable cause) {
		super(message, cause);
	}

	public OneJobException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
