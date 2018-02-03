package uk.gov.digital.ho.egar.gar.model.rest;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import uk.gov.digital.ho.egar.gar.model.Gar;
import uk.gov.digital.ho.egar.gar.model.GarUuidList;

public class GarUuidListPojo implements GarUuidList {
	
	private final List<UUID> garUuids ;
	
	public 
		static
			GarUuidList create(List<? extends Gar> garUuids) {
		
		if ( garUuids == null )
		{
			return new GarUuidListPojo(Collections.emptyList());
		}
		else
		{
			List<UUID> uuids = garUuids.stream()
					.map((gar)-> gar.getGarUuid()).collect(Collectors.toList());

			return new GarUuidListPojo(uuids);			
		}
	}
	
	protected GarUuidListPojo(List<UUID> garUuids) {
		super();
		this.garUuids = Collections.unmodifiableList(garUuids);
	}




	@Override
	public List<UUID> getGarUuids() {
		return this.garUuids;
	}
	
	
}
