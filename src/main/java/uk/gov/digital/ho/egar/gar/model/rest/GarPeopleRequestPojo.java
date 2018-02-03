package uk.gov.digital.ho.egar.gar.model.rest;

import lombok.Builder;
import lombok.Data;
import uk.gov.digital.ho.egar.gar.model.PeopleOnGar;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GarPeopleRequestPojo implements PeopleOnGar{
    private UUID captain;
    private List<UUID> crew;
    private List<UUID> passengers;
}
