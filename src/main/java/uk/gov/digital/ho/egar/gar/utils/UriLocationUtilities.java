package uk.gov.digital.ho.egar.gar.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * The uri location utilities provide URI's for the provided parameters.
 * These can be used to construct redirection responses
 */
public interface UriLocationUtilities {

    /**
     * Gets the gar URI from the provided gar id
     * @param garId the gar uuid.
     * @return The Gar URI
     * @throws URISyntaxException When unable to construct a valid URI
     */
    URI getGarURI(final UUID garId)throws URISyntaxException;

}
