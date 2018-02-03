package uk.gov.digital.ho.egar.gar.service.repository.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "FlightLocation")
@Data
@EqualsAndHashCode(callSuper=true)
public class LocationUuidPersistedRecord extends UuidPersistedRecord {

    @NotNull
    private int locationOrder;
	
}
