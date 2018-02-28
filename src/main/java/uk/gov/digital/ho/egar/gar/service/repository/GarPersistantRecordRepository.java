package uk.gov.digital.ho.egar.gar.service.repository;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.gov.digital.ho.egar.gar.service.repository.model.GarPersistantRecord;

@Transactional
public interface GarPersistantRecordRepository extends JpaRepository<GarPersistantRecord, UUID>{
	List<GarPersistantRecord> findAllByUserUuidOrderByCreatedDesc(UUID userUuid);

	GarPersistantRecord findByGarUuidAndUserUuidOrderByCreatedDesc(UUID garUUid, UUID userUuid);

	List<GarPersistantRecord> findAllByUserUuidAndGarUuidIn(UUID userUuid, List<UUID> garUuidList);
}
