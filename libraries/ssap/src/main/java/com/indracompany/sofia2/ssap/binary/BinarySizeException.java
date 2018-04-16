package com.indracompany.sofia2.ssap.binary;

@SuppressWarnings("serial")
public class BinarySizeException extends Exception {

	public BinarySizeException(String message) {
		super(message);
	}

	public BinarySizeException(String message, Throwable exception) {
		super(message, exception);
	}
}
