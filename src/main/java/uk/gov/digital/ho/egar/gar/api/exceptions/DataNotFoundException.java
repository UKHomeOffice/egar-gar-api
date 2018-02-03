/**
 * 
 */
package uk.gov.digital.ho.egar.gar.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *  
 */
@ResponseStatus(value=HttpStatus.BAD_REQUEST) 
abstract public class DataNotFoundException extends GarDataserviceException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public DataNotFoundException() {
	}

	/**
	 * @param message
	 */
	public DataNotFoundException(String message) {
		super(message);
	}


}
