/**
 * 
 */
package uk.gov.digital.ho.egar.gar.api.exceptions;

/**
 * A base class for errors.
 *
 */
public abstract class GarDataserviceException extends Exception {

	private static final long serialVersionUID = 1L;

	public GarDataserviceException(){
	}

	public GarDataserviceException(String message, Throwable cause) {
		super(message, cause);
	}

	public GarDataserviceException(String message) {
		super(message);
	}

	public GarDataserviceException(Throwable cause) {
		super(cause);
	}

	
	
	
}
