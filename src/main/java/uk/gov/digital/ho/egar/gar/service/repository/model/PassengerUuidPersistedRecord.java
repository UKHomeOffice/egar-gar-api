package uk.gov.digital.ho.egar.gar.service.repository.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "FlightPassenger")
public class PassengerUuidPersistedRecord extends UuidPersistedRecord {

}
