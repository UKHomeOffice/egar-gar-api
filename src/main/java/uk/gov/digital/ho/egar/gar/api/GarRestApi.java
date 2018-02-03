package uk.gov.digital.ho.egar.gar.api;

import java.net.URISyntaxException;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import uk.gov.digital.ho.egar.gar.api.exceptions.GarDataserviceException;
import uk.gov.digital.ho.egar.gar.model.Gar;
import uk.gov.digital.ho.egar.gar.model.GarUuidList;

public interface GarRestApi  {
	/**
	 * 
	 * @return A set of GARS.
	 * @throws GarDataserviceException
	 */
	public GarUuidList getAllGars(UUID uuidOfUser) throws GarDataserviceException;
	public ResponseEntity<Void> createAGar(UUID uuidOfUser) throws GarDataserviceException, URISyntaxException;
	public Gar getSingleGar(UUID uuidOfUser,UUID garUuid) throws GarDataserviceException;
	public ResponseEntity<Void> updateSingleGar(UUID uuidOfUser,UUID garUuid, Gar gar) throws GarDataserviceException, URISyntaxException;
	public void deleteASingleGar(UUID uuidOfUser,UUID garUuid) throws GarDataserviceException;
}
