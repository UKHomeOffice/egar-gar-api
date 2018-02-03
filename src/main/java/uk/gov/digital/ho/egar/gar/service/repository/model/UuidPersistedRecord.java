/**
 * 
 */
package uk.gov.digital.ho.egar.gar.service.repository.model;

import java.util.UUID;

import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import lombok.*;

/**
 * @author localuser
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@MappedSuperclass
@IdClass(UuidPersistedRecordId.class)
public class UuidPersistedRecord {

	@Id
	@NotNull
	private UUID garUuid ;

	@Id
	@NotNull
    private UUID valueUuid ;

}
