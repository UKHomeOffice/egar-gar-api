package uk.gov.digital.ho.egar.gar.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import uk.gov.digital.ho.egar.gar.model.rest.GarRequestPojo;

@JsonDeserialize(as=GarRequestPojo.class)
public interface Gar {
	@JsonProperty("gar_uuid")
	public UUID getGarUuid();
	@JsonProperty("user_uuid")
	public UUID getUserUuid();
	@JsonProperty("aircraft_uuid")
	public UUID getAircraftUuid();
	@JsonProperty("location_uuids")
	public List<UUID> getLocationUuids();
	@JsonProperty("people")
	public PeopleOnGar getPeopleOnGar();
	@JsonProperty("file_uuids")
	public List<UUID> getFileUuids();
	@JsonProperty("attributes")
	public Map<String,String> getAttributeMap();
	@JsonProperty("submission_uuid")
	public UUID getSubmissionUuid();
}
