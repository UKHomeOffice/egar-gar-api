package uk.gov.digital.ho.egar.gar.model.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.digital.ho.egar.gar.model.PeopleOnGar;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GarPeopleRequestPojo implements PeopleOnGar{
    private UUID captain;
    private List<UUID> crew;
    private List<UUID> passengers;
}
