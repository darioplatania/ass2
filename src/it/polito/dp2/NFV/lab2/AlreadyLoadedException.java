/**
 * 
 */
package it.polito.dp2.NFV.lab2;

/**
 * An exception that occurs when something has already been loaded
 * and the user is requesting to load it again.
 *
 */
public class AlreadyLoadedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -10755011267053632L;

	/**
	 * 
	 */
	public AlreadyLoadedException() {
	}

	public AlreadyLoadedException(String message) {
		super(message);
	}

	public AlreadyLoadedException(Throwable cause) {
		super(cause);
	}

	public AlreadyLoadedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlreadyLoadedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
