package uk.gov.digital.ho.egar.gar.service.repository.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "SupportingFileUuid")
@EqualsAndHashCode(callSuper=true)
public class SupportingFileUuidPersistedRecord extends UuidPersistedRecord {

}
