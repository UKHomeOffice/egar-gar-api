package uk.gov.digital.ho.egar.gar.model;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface GarUuidList {

	@JsonProperty("gar_uuids")
	List<UUID>  getGarUuids ();
	
}
