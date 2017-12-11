package it.polito.dp2.NFV.lab2;

public class UnknownNameException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnknownNameException() {
	}

	public UnknownNameException(String message) {
		super(message);
	}

	public UnknownNameException(Throwable cause) {
		super(cause);
	}

	public UnknownNameException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownNameException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
