package gov.nist.swidval.controller;

public class UnrecognizedContentException extends Exception {

	/** the serial version UID */
	private static final long serialVersionUID = 1L;

	public UnrecognizedContentException() {
		super();
	}

	public UnrecognizedContentException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnrecognizedContentException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnrecognizedContentException(String message) {
		super(message);
	}

	public UnrecognizedContentException(Throwable cause) {
		super(cause);
	}
	
}
