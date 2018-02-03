package uk.gov.digital.ho.egar.gar.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.digital.ho.egar.gar.api.exceptions.GarNotFoundException;
import uk.gov.digital.ho.egar.gar.model.Gar;
import uk.gov.digital.ho.egar.gar.model.PeopleOnGar;
import uk.gov.digital.ho.egar.gar.service.GarService;
import uk.gov.digital.ho.egar.gar.service.repository.GarPersistantRecordRepository;
import uk.gov.digital.ho.egar.gar.service.repository.model.*;

@Service
public class GarServiceImpl implements GarService {
	protected final Log logger = LogFactory.getLog(getClass());
	
	private final GarPersistantRecordRepository repository;
	
	
	public GarServiceImpl(@Autowired
						  GarPersistantRecordRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public List<? extends Gar> getAllUsersGars(UUID userUuid)
	{
		return repository.findAllByUserUuid(userUuid) ;
	}

	@Override
	public Gar createAGar(UUID userUuid) {
		
		GarPersistantRecord gar = GarPersistantRecord.builder()
										.garUuid(UUID.randomUUID())
										.userUuid(userUuid)
										.build();
		
		
		return save(gar);
	}

	private Gar save(final GarPersistantRecord gar) {
		return repository.saveAndFlush(gar.updateLastModified());
	}

	@Override
	public Gar getSingleGar(UUID garUuid, UUID userUuid) throws GarNotFoundException {
		GarPersistantRecord dbResponse = repository.findByGarUuidAndUserUuid(garUuid, userUuid);

		if (dbResponse==null){
		    //LOG
		    throw new GarNotFoundException(garUuid);
        }

		return dbResponse;
	}

	@Override
	public Gar updateAGar(UUID garUuid, UUID userUuid, Gar gar) throws GarNotFoundException {

		GarPersistantRecord existingGar = (GarPersistantRecord) getSingleGar(garUuid, userUuid);

        existingGar = initaliseAndClear(existingGar);
        existingGar = update(existingGar, gar);

		return save(existingGar);
	}

	private GarPersistantRecord initaliseAndClear(final GarPersistantRecord existingGar){
        if (existingGar.getAttributes()==null){
            existingGar.setAttributes(new ArrayList<>());
        }
        existingGar.getAttributes().clear();

        if (existingGar.getFiles()==null){
            existingGar.setFiles(new ArrayList<>());
        }
        existingGar.getFiles().clear();

        if (existingGar.getLocations()==null){
            existingGar.setLocations(new ArrayList<>());
        }
        existingGar.getLocations().clear();

        if (existingGar.getCrew()==null){
            existingGar.setCrew(new ArrayList<>());
        }
        existingGar.getCrew().clear();

        if (existingGar.getPassengers()==null){
            existingGar.setPassengers(new ArrayList<>());
        }
        existingGar.getPassengers().clear();

        return existingGar;
    }

    private GarPersistantRecord update(final GarPersistantRecord existingGar, final Gar gar) {

        try {
            existingGar.setAircraftUuid(gar.getAircraftUuid());
            existingGar.getAttributes().addAll(copyAttributes(existingGar.getGarUuid(),gar.getAttributeMap()));
            existingGar.getFiles().addAll(copyList(existingGar.getGarUuid(),SupportingFileUuidPersistedRecord.class, gar.getFileUuids()));
            existingGar.getLocations().addAll(copyLocationList(existingGar.getGarUuid(),gar.getLocationUuids()));
            existingGar.setSubmissionUuid(gar.getSubmissionUuid());
            if (gar.getPeopleOnGar() == null) {
                existingGar.setCaptainUuid(null);
            } else {
                PeopleOnGar people = gar.getPeopleOnGar();

                existingGar.setCaptainUuid(people.getCaptain());
                existingGar.getCrew().addAll(copyList(existingGar.getGarUuid(),CrewUuidPersistedRecord.class, people.getCrew()));
                existingGar.getPassengers().addAll(copyList(existingGar.getGarUuid(),PassengerUuidPersistedRecord.class, people.getPassengers()));
            }
        }catch (IllegalAccessException | InstantiationException e){
            //TODO logging we should never get here
            throw new IllegalStateException("Unable to update a gar persistence record due to type instantiation");
        }

        return existingGar;
    }

    private List<AttributePersistedRecord> copyAttributes(final UUID garUuid, Map<String, String> attributes) {
        List<AttributePersistedRecord> records = new ArrayList<>();
        if (attributes != null && !attributes.isEmpty()) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                AttributePersistedRecord record = new AttributePersistedRecord();
                record.setGarUuid(garUuid);
                record.setName(entry.getKey());
                record.setValue(entry.getValue());
                records.add(record);
            }
        }
        return records;
    }

    private <T extends UuidPersistedRecord> List<UuidPersistedRecord> copyList(UUID garUuid, Class<T> clazz, List<UUID> values) throws IllegalAccessException, InstantiationException {
        List<UuidPersistedRecord> records = new ArrayList<>();
        if (values!=null && !values.isEmpty()){
            for (UUID uuid : values){
                UuidPersistedRecord record = clazz.newInstance();
                record.setGarUuid(garUuid);
                record.setValueUuid(uuid);
                records.add(record);
            }

        }
        return records;
    }

    private List<UuidPersistedRecord> copyLocationList(UUID garUuid, List<UUID> values) throws IllegalAccessException, InstantiationException {

        final List<UuidPersistedRecord> records = copyList(garUuid, LocationUuidPersistedRecord.class, values);
        if (values!=null && !values.isEmpty()){
            int order = 1;
            for (UuidPersistedRecord record: records){
                if (record instanceof LocationUuidPersistedRecord){
                    LocationUuidPersistedRecord locRec = (LocationUuidPersistedRecord) record;
                    locRec.setLocationOrder(order);
                }
                order++;
            }
        }

        return records.stream().filter(record -> record.getValueUuid() != null).collect(Collectors.toList());
    }
}
