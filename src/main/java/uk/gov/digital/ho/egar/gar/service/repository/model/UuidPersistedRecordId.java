package uk.gov.digital.ho.egar.gar.service.repository.model;

import java.io.Serializable;
import java.util.UUID;


/**
 * This is a magic hibernate composite key.
 * There are no methods due to hibernate using reflection.
 */
@SuppressWarnings("unused")
public class UuidPersistedRecordId implements Serializable{
	private static final long serialVersionUID = 1L;
	private UUID garUuid ;
    private UUID valueUuid ;
}
