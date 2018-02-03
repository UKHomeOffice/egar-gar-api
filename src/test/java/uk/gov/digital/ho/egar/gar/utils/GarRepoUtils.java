package uk.gov.digital.ho.egar.gar.utils;

import uk.gov.digital.ho.egar.gar.service.repository.GarPersistantRecordRepository;
import uk.gov.digital.ho.egar.gar.service.repository.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GarRepoUtils {

    public static GarPersistantRecord initRepoWithSingleGarWithBothLocations(UUID userUuid, UUID garUuid, GarPersistantRecordRepository repo) throws InstantiationException, IllegalAccessException {
        repo.deleteAll();

        // One for the current user
        GarPersistantRecord gar = initGar(userUuid, garUuid, repo);

        updateGarWithDetails(gar);
        updateGarWithDeparture(gar);
        updateGarWithArrival(gar);
        return repo.saveAndFlush(gar);
    }

    public static GarPersistantRecord initRepoWithSingleGarWithDeparture(UUID userUuid, UUID garUuid,  GarPersistantRecordRepository repo) throws InstantiationException, IllegalAccessException {
        repo.deleteAll();

        // One for the current user
        GarPersistantRecord gar = initGar(userUuid, garUuid, repo);

        updateGarWithDetails(gar);
        updateGarWithDeparture(gar);
        return repo.saveAndFlush(gar);
    }

    public static GarPersistantRecord initRepoWithSingleGarWithArrival(UUID userUuid, UUID garUuid,  GarPersistantRecordRepository repo) throws InstantiationException, IllegalAccessException {
        repo.deleteAll();

        GarPersistantRecord gar = initGar(userUuid, garUuid, repo);

        updateGarWithDetails(gar);
        updateGarWithArrival(gar);
        return repo.saveAndFlush(gar);
    }


    public static GarPersistantRecord initRepoWithSingleGarNoLocations(UUID userUuid, UUID garUuid,  GarPersistantRecordRepository repo) throws InstantiationException, IllegalAccessException {
        repo.deleteAll();

        // One for the current user
        GarPersistantRecord gar = initGar(userUuid, garUuid, repo);

        updateGarWithDetails(gar);
        return repo.saveAndFlush(gar);
    }

    private static GarPersistantRecord initGar(UUID userUuid, UUID garUuid,  GarPersistantRecordRepository repo){
        GarPersistantRecord gar = GarPersistantRecord.builder()
                .garUuid(garUuid)
                .userUuid(userUuid)
                .captainUuid(UUID.randomUUID())
                .aircraftUuid(UUID.randomUUID())
                .build();
        return repo.saveAndFlush(gar);
    }

    private static void updateGarWithDetails(GarPersistantRecord gar) throws InstantiationException, IllegalAccessException {
        AttributePersistedRecord attr1 = createAttributeRecord(gar.getGarUuid(), "a", "b");
        AttributePersistedRecord attr2 = createAttributeRecord(gar.getGarUuid(), "c", "d");
        gar.setAttributes(new ArrayList<>(Arrays.asList(attr1, attr2)));

        SupportingFileUuidPersistedRecord file1 = createPersistedRecord(SupportingFileUuidPersistedRecord.class, gar.getGarUuid(), UUID.randomUUID());
        gar.setFiles(new ArrayList<>(Arrays.asList(file1)));


        CrewUuidPersistedRecord crew1 = createPersistedRecord(CrewUuidPersistedRecord.class, gar.getGarUuid(), UUID.randomUUID());
        CrewUuidPersistedRecord crew2 = createPersistedRecord(CrewUuidPersistedRecord.class, gar.getGarUuid(), UUID.randomUUID());
        gar.setCrew(new ArrayList<>(Arrays.asList(crew1, crew2)));

        PassengerUuidPersistedRecord pass = createPersistedRecord(PassengerUuidPersistedRecord.class, gar.getGarUuid(), UUID.randomUUID());
        gar.setPassengers(new ArrayList<>(Arrays.asList(pass)));

        gar.setLocations(new ArrayList<>());

        gar.setSubmissionUuid(UUID.randomUUID());
    }

    private static void updateGarWithArrival(GarPersistantRecord gar) throws InstantiationException, IllegalAccessException {
        LocationUuidPersistedRecord loc2 = createPersistedRecord(LocationUuidPersistedRecord.class, gar.getGarUuid(), UUID.randomUUID());
        loc2.setLocationOrder(2);
        addLocations(gar, Arrays.asList(loc2));
    }

    private static void updateGarWithDeparture(GarPersistantRecord gar) throws InstantiationException, IllegalAccessException {
        LocationUuidPersistedRecord loc1 = createPersistedRecord(LocationUuidPersistedRecord.class, gar.getGarUuid(), UUID.randomUUID());
        loc1.setLocationOrder(1);

        addLocations(gar, Arrays.asList(loc1));

    }

    private static void addLocations(GarPersistantRecord gar, List<LocationUuidPersistedRecord> newLocations){
        List locations = gar.getLocations();
        if (locations==null){
            locations = new ArrayList();
        }

        locations.addAll(newLocations);
        gar.setLocations(locations);
    }

    private static AttributePersistedRecord createAttributeRecord(UUID garUuid, String name, String value) {
        AttributePersistedRecord record = new AttributePersistedRecord();
        record.setGarUuid(garUuid);
        record.setName(name);
        record.setValue(value);
        return record;
    }

    private static <T extends UuidPersistedRecord> T createPersistedRecord(Class<T> clazz, UUID gar, UUID uuid) throws IllegalAccessException, InstantiationException {

        T o = clazz.newInstance();

        o.setGarUuid(gar);
        o.setValueUuid(uuid);

        return o;
    }
}
