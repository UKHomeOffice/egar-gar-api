package uk.gov.digital.ho.egar.gar.service;

import java.util.List;
import java.util.UUID;

import uk.gov.digital.ho.egar.gar.api.exceptions.GarNotFoundException;
import uk.gov.digital.ho.egar.gar.model.Gar;
import uk.gov.digital.ho.egar.gar.model.rest.GarRequestPojo;

public interface GarService {

	List<? extends Gar> getAllUsersGars(UUID userUuid);

	Gar createAGar(UUID userUuid);

	Gar getSingleGar(UUID garUuid, UUID userUuid) throws GarNotFoundException;

	Gar updateAGar(UUID garUuid, UUID userUuid, Gar gar) throws GarNotFoundException;

	Gar[] getBulkGars(UUID uuidOfUser, List<UUID> garList);

}