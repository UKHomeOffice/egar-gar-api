/**
 * 
 */
package uk.gov.digital.ho.egar.gar.api.exceptions;

import java.util.UUID;

/**
 * @author localuser
 *
 */
public class GarNotFoundException extends DataNotFoundException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public GarNotFoundException(final UUID garId)
	{
		super(String.format("Can not find gar %s", garId.toString()));
	}
	
}
