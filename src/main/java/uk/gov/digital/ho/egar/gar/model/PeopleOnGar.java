package uk.gov.digital.ho.egar.gar.model;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface PeopleOnGar {
	@JsonProperty("captain")
	public UUID getCaptain();
	@JsonProperty("crew")
	public List<UUID> getCrew();
	@JsonProperty("passengers")
	public List<UUID> getPassengers();
}
