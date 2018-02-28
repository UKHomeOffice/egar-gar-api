package uk.gov.digital.ho.egar.gar.model.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.digital.ho.egar.gar.model.Gar;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GarRequestPojo implements Gar{
    private UUID garUuid;
    private UUID userUuid;
    private UUID aircraftUuid;
    private List<UUID> locationUuids;
    private List<UUID> fileUuids;
    private Map<String, String> attributeMap;
    private GarPeopleRequestPojo peopleOnGar;
    private UUID submissionUuid;
}
