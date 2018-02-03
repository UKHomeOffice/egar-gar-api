package uk.gov.digital.ho.egar.gar.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.UNAUTHORIZED) 
public class UnauthenticatedException extends GarDataserviceException {

	private static final long serialVersionUID = 1L;

	public UnauthenticatedException(String message) {
		super(message);
	}

}
