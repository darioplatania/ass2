package it.polito.dp2.NFV.lab2;

public class NoGraphException extends Exception {

	/**
	 * Thrown if no graph has been loaded onto the NEO4J DB
	 */
	private static final long serialVersionUID = 1L;

	public NoGraphException() {
	}

	public NoGraphException(String message) {
		super(message);
	}

	public NoGraphException(Throwable cause) {
		super(cause);
	}

	public NoGraphException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoGraphException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
