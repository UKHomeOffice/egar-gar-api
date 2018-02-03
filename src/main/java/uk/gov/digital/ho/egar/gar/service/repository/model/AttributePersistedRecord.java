/**
 * 
 */
package uk.gov.digital.ho.egar.gar.service.repository.model;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author localuser
 *
 */
@Data
@Entity
@Table(name = "Attribute")
@IdClass(AttributePersistedRecordId.class)
public class AttributePersistedRecord {

	@Id
	@NotNull
	private UUID garUuid ;

	@NotNull
	@Id
	private String name ;

	private String value ;
	
	
}
