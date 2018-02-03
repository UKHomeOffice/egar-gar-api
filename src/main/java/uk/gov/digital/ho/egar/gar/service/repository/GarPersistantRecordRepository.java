package uk.gov.digital.ho.egar.gar.service.repository;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.gov.digital.ho.egar.gar.service.repository.model.GarPersistantRecord;

@Transactional
public interface GarPersistantRecordRepository extends JpaRepository<GarPersistantRecord, UUID>{
	List<GarPersistantRecord> findAllByUserUuid(UUID userUuid);

	GarPersistantRecord findByGarUuidAndUserUuid(UUID garUUid, UUID userUuid);

}
