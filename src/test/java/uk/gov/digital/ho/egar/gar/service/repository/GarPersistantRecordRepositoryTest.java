/**
 * 
 */
package uk.gov.digital.ho.egar.gar.service.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import uk.gov.digital.ho.egar.gar.service.repository.model.AttributePersistedRecord;
import uk.gov.digital.ho.egar.gar.service.repository.model.GarPersistantRecord;
import uk.gov.digital.ho.egar.gar.service.repository.model.SupportingFileUuidPersistedRecord;
import uk.gov.digital.ho.egar.gar.service.repository.model.UuidPersistedRecord;

import javax.validation.ConstraintViolationException;
/**
 * @author localuser
 *
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {GarPersistantRecordRepositoryTest.class})
@EnableAutoConfiguration
public class GarPersistantRecordRepositoryTest {

	@Autowired
    private GarPersistantRecordRepository repo;
	
	@Test
	public void jpaSchemaCanBeMappedFromClasses() {
	    assertThat(this.repo).isNotNull();
	}

	@Test(expected=ConstraintViolationException.class)
	public void cannotSaveWithOnlyId() {
		
		// WITH
		GarPersistantRecord gar = GarPersistantRecord.builder().garUuid(UUID.randomUUID()).build();
				
	    // WHEN
		repo.saveAndFlush(gar); // Cause Hibernate to flush result to database on the spot
		
		// THEN
		Assert.fail();
	}

	@Test
	public void canSaveWithGarIdAndUserId() {
		
		// WITH
		long count = repo.count();
				
	    // WHEN
		repo.saveAndFlush(GarPersistantRecord.builder().garUuid(UUID.randomUUID())
											   .userUuid(UUID.randomUUID()).build());
		
		// THEN
		assertThat(repo.count()).isEqualTo(count+1);
	}
	
	
	@Test
	public void canSaveAndFind() {
		
		// WITH
		UUID uuid = repo.saveAndFlush(GarPersistantRecord.builder().garUuid(UUID.randomUUID())
																   .userUuid(UUID.randomUUID()).build())
					.getGarUuid();
		
	    // WHEN
		GarPersistantRecord gar = repo.findOne(uuid);
		
		// THEN
		assertThat(gar).isNotNull();
		assertThat(gar.getGarUuid()).isEqualTo(uuid);
		
	}
	
	@Test
	public void canSaveByUserId() {
		
		// WITH
		int numberOfGars = 10 ;
		UUID userUuid1 = UUID.randomUUID();
		UUID userUuid2 = UUID.randomUUID();
		for ( int n = 0 ; n < numberOfGars ; n++ )
		{
			repo.saveAndFlush(GarPersistantRecord.builder().garUuid(UUID.randomUUID()).userUuid(userUuid1).build());
			repo.saveAndFlush(GarPersistantRecord.builder().garUuid(UUID.randomUUID()).userUuid(userUuid2).build());
		}
		
	    // WHEN
		List<GarPersistantRecord> gars = repo.findAllByUserUuidOrderByCreatedDesc(userUuid1);


		// THEN
		assertThat(repo.count()).isGreaterThan(numberOfGars);
		assertThat(gars).isNotNull().hasSize(numberOfGars);
		
	}

	@Test
	public void canReplaceListOfFiles() throws InstantiationException, IllegalAccessException {

	    // WITH
		UUID userUuid1 = UUID.randomUUID();
		UUID garUuid = UUID.randomUUID();
        SupportingFileUuidPersistedRecord file1 = createPersistedRecord(SupportingFileUuidPersistedRecord.class, garUuid, UUID.randomUUID());
        SupportingFileUuidPersistedRecord file2 = createPersistedRecord(SupportingFileUuidPersistedRecord.class, garUuid, UUID.randomUUID());

        List<UuidPersistedRecord> list = new ArrayList<>();
        list.add(file1);
        list.add(file2);

		GarPersistantRecord record = repo.saveAndFlush(GarPersistantRecord.builder().garUuid(garUuid).userUuid(userUuid1).files(list).build());

		// WHEN
        SupportingFileUuidPersistedRecord file3 = createPersistedRecord(SupportingFileUuidPersistedRecord.class, garUuid, UUID.randomUUID());

        List<UuidPersistedRecord> list2 = new ArrayList<>();
        list2.add(file3);

        record.getFiles().clear();
        record.getFiles().addAll(list2);

		repo.saveAndFlush(record);

        GarPersistantRecord result = repo.getOne(garUuid);


        // THEN
		assertThat(result.getFiles()).hasSize(1).contains(file3);

	}

	@Test
	public void canAddFilesToGar() throws InstantiationException, IllegalAccessException {


        // WITH
        UUID userUuid1 = UUID.randomUUID();
        UUID garUuid = UUID.randomUUID();
        SupportingFileUuidPersistedRecord file1 = createPersistedRecord(SupportingFileUuidPersistedRecord.class, garUuid, UUID.randomUUID());
        SupportingFileUuidPersistedRecord file2 = createPersistedRecord(SupportingFileUuidPersistedRecord.class, garUuid, UUID.randomUUID());

        // WHEN
        repo.saveAndFlush(GarPersistantRecord.builder().garUuid(garUuid).userUuid(userUuid1).files(Arrays.asList(file1, file2)).build());

        GarPersistantRecord result = repo.getOne(garUuid);


        // THEN
        assertThat(result.getFiles()).hasSize(2).contains(file1, file2);

    }

	@Test
	public void canAddSingleFileToGar() throws InstantiationException, IllegalAccessException {


		// WITH
		UUID userUuid1 = UUID.randomUUID();
		UUID garUuid = UUID.randomUUID();
		SupportingFileUuidPersistedRecord file1 = createPersistedRecord(SupportingFileUuidPersistedRecord.class, garUuid, UUID.randomUUID());

		// WHEN
		repo.saveAndFlush(GarPersistantRecord.builder().garUuid(garUuid).userUuid(userUuid1).files(Arrays.asList(file1)).build());


        GarPersistantRecord result = repo.getOne(garUuid);

        // THEN
		assertThat(result.getFiles()).hasSize(1).contains(file1);

	}

    @Test
    public void canReplaceListOfAttributes() throws InstantiationException, IllegalAccessException {


        // WITH
        UUID userUuid1 = UUID.randomUUID();
        UUID garUuid = UUID.randomUUID();
        AttributePersistedRecord attr1 = createAttributeRecord(garUuid, "a", "b");
        AttributePersistedRecord attr2 = createAttributeRecord(garUuid, "c", "d");

        List<AttributePersistedRecord> list = new ArrayList<>();
        list.add(attr1);
        list.add(attr2);

        GarPersistantRecord record = repo.saveAndFlush(GarPersistantRecord.builder().garUuid(garUuid).userUuid(userUuid1).attributes(list).build());

        // WHEN
        AttributePersistedRecord attr3 = createAttributeRecord(garUuid, "e", "f");

        List<AttributePersistedRecord> list2 = new ArrayList<>();
        list2.add(attr3);

        record.getAttributes().clear();
        record.getAttributes().addAll(list2);

        repo.saveAndFlush(record);


        GarPersistantRecord result = repo.getOne(garUuid);
        // THEN
        assertThat(result.getAttributeMap()).hasSize(1).containsExactly(entry("e", "f"));

    }

    @Test
    public void canAddAttributesToGar() throws InstantiationException, IllegalAccessException {

        // WITH
        UUID userUuid1 = UUID.randomUUID();
        UUID garUuid = UUID.randomUUID();
        AttributePersistedRecord attr1 = createAttributeRecord(garUuid, "a", "b");
        AttributePersistedRecord attr2 = createAttributeRecord(garUuid, "c", "d");

        // WHEN
        GarPersistantRecord record = repo.saveAndFlush(GarPersistantRecord.builder().garUuid(garUuid).userUuid(userUuid1).attributes(Arrays.asList(attr1, attr2)).build());


        // THEN
        assertThat(record.getAttributeMap()).hasSize(2).containsExactly(entry("a", "b"), entry("c", "d"));

    }

    @Test
    public void canAddSingleAttributeToGar() throws InstantiationException, IllegalAccessException {


        // WITH
        UUID userUuid1 = UUID.randomUUID();
        UUID garUuid = UUID.randomUUID();
        AttributePersistedRecord attr1 = createAttributeRecord(garUuid, "a", "b");

        // WHEN
        GarPersistantRecord record = repo.saveAndFlush(GarPersistantRecord.builder().garUuid(garUuid).userUuid(userUuid1).attributes(Arrays.asList(attr1)).build());


        // THEN
        assertThat(record.getAttributeMap()).hasSize(1).containsExactly(entry("a", "b"));

    }

    private AttributePersistedRecord createAttributeRecord(UUID garUuid, String name, String value) {
        AttributePersistedRecord record = new AttributePersistedRecord();
        record.setGarUuid(garUuid);
        record.setName(name);
        record.setValue(value);
        return record;
    }

	private <T extends UuidPersistedRecord> T createPersistedRecord(Class<T> clazz,UUID gar, UUID uuid) throws IllegalAccessException, InstantiationException {

		T o = clazz.newInstance();

		o.setGarUuid(gar);
		o.setValueUuid(uuid);

		return o;
	}

	
	
	
}
