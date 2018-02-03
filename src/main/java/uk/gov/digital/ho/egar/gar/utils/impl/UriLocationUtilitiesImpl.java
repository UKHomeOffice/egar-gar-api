package uk.gov.digital.ho.egar.gar.utils.impl;

import org.springframework.stereotype.Component;
import uk.gov.digital.ho.egar.constants.ServicePathConstants;
import uk.gov.digital.ho.egar.gar.utils.UriLocationUtilities;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static uk.gov.digital.ho.egar.gar.api.PathConstants.ROOT_PATH;


/**
 * The uri location utilities provide URI's for the provided parameters.
 * Utilises the service name and identifiers found in the workflow api.
 * These can be used to construct redirection responses
 */
@Component
public class UriLocationUtilitiesImpl implements UriLocationUtilities {

    /**
     * Gets the gar URI from the provided gar id
     * @param garId the gar uuid.
     * @return The gar URI
     * @throws URISyntaxException When unable to construct a valid URI
     */
    @Override
    public URI getGarURI(final UUID garId) throws URISyntaxException {
        return new URI(ROOT_PATH + ServicePathConstants.ROOT_PATH_SEPERATOR + garId + ServicePathConstants.ROOT_PATH_SEPERATOR);
    }

}
