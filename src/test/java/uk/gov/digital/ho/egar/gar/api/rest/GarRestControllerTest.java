package uk.gov.digital.ho.egar.gar.api.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static uk.co.civica.microservice.util.testing.matcher.RegexMatcher.matchesRegex;

import java.util.*;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import uk.gov.digital.ho.egar.gar.api.exceptions.GarNotFoundException;
import uk.gov.digital.ho.egar.gar.model.Gar;
import uk.gov.digital.ho.egar.gar.model.PeopleOnGar;
import uk.gov.digital.ho.egar.gar.model.rest.GarPeopleRequestPojo;
import uk.gov.digital.ho.egar.gar.model.rest.GarRequestPojo;
import uk.gov.digital.ho.egar.gar.service.GarService;
import uk.gov.digital.ho.egar.gar.service.impl.GarServiceImpl;
import uk.gov.digital.ho.egar.gar.service.repository.GarPersistantRecordRepository;
import uk.gov.digital.ho.egar.gar.service.repository.GarPersistantRecordRepositoryTest;

import org.springframework.test.context.ContextConfiguration;
import uk.gov.digital.ho.egar.gar.service.repository.model.GarPersistantRecord;
import uk.gov.digital.ho.egar.gar.utils.GarRepoUtils;
import uk.gov.digital.ho.egar.gar.utils.UriLocationUtilities;
import uk.gov.digital.ho.egar.gar.utils.impl.UriLocationUtilitiesImpl;

/**
 * @See https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-testing-spring-boot-applications-testing-autoconfigured-jpa-test
 * 
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {GarPersistantRecordRepositoryTest.class})
@EnableAutoConfiguration
public class GarRestControllerTest {

    private static final String REGEX_UUID = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    @Autowired
    private GarPersistantRecordRepository repo;

	private GarService garService ;

	private UriLocationUtilities locationUtilities;
	
	private GarRestController restController ;
	
	@Rule 
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	
	@Before
	public void initRestController () 
	{		
		if ( this.garService == null )
		{
			this.garService = new GarServiceImpl(repo);
		}
        if ( this.locationUtilities == null )
        {
            this.locationUtilities = new UriLocationUtilitiesImpl();
        }
		if ( this.restController == null )
		{
			this.restController = new GarRestController(this.garService, locationUtilities);
		}
	}
	
	@Test
	public void jpaSchemaCanBeMappedFromClasses() {
	    assertThat(this.repo).isNotNull();
	}
	
	@Test
	public void canInitRestController() {
	    assertThat(this.restController).isNotNull();
	}

	@Test
	public void canCreateAGar() throws Exception
	{
		// WITH
		final long garCount = repo.count();
		final UUID userId = UUID.randomUUID() ;

		// WHEN
		ResponseEntity<Void> response = this.restController.createAGar(userId);
		
		// THEN
		assertThat(response).isNotNull();
		assertThat(response.getHeaders()).isNotNull();
		assertThat(response.getHeaders().get("location")).isNotNull();
		assertThat(repo.count()).isEqualTo(garCount+1);
				
	}
	
	@Test
	public void canFindAllGarsForUser() throws Exception
	{
		// WITH
		final UUID userUuid = UUID.randomUUID() ;
		final long garCount = repo.findAllByUserUuidOrderByCreatedDesc(userUuid).size();
		this.restController.createAGar(userUuid);
		this.restController.createAGar(userUuid);
		this.restController.createAGar(userUuid);
		
		// WHEN
		this.restController.getAllGars(userUuid);
		
		// THEN
		assertThat(repo.count()).isEqualTo(garCount+3);
	}

	@Test
    public void canRetrieveAGar() throws Exception
    {
        // WITH
        final UUID userUuid = UUID.randomUUID() ;
        final long garCount = repo.findAllByUserUuidOrderByCreatedDesc(userUuid).size();
        ResponseEntity<Void> createResponse = this.restController.createAGar(userUuid);

        UUID garUuid = getGarUuid(createResponse);

        // WHEN
        Gar result = this.restController.getSingleGar(userUuid, garUuid);

        // THEN
        assertThat(repo.count()).isEqualTo(garCount+1);
        assertThat(result).isNotNull();
        assertThat(result.getGarUuid()).isEqualTo(garUuid);
        assertThat(result.getUserUuid()).isEqualTo(userUuid);
    }

    @Test
    public void canRetrieveAGarWithNoLocations() throws Exception
    {
        // WITH
        final UUID userUuid = UUID.randomUUID() ;
        final UUID garUuid = UUID.randomUUID();
        GarPersistantRecord gar = GarRepoUtils.initRepoWithSingleGarNoLocations(userUuid, garUuid, repo);

        ResponseEntity<Void> createResponse = this.restController.createAGar(userUuid);

        // WHEN
        Gar result = this.restController.getSingleGar(userUuid, garUuid);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getGarUuid()).isEqualTo(garUuid);
        assertThat(result.getUserUuid()).isEqualTo(userUuid);
        assertTrue(result.getLocationUuids().size()== 2);
        assertNull(result.getLocationUuids().get(0));
        assertNull(result.getLocationUuids().get(1));
    }

    @Test
    public void canRetrieveAGarWithArrival() throws Exception
    {
        // WITH
        final UUID userUuid = UUID.randomUUID() ;
        final UUID garUuid = UUID.randomUUID();
        GarPersistantRecord gar = GarRepoUtils.initRepoWithSingleGarWithArrival(userUuid, garUuid, repo);

        ResponseEntity<Void> createResponse = this.restController.createAGar(userUuid);

        // WHEN
        Gar result = this.restController.getSingleGar(userUuid, garUuid);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getGarUuid()).isEqualTo(garUuid);
        assertThat(result.getUserUuid()).isEqualTo(userUuid);
        assertTrue(result.getLocationUuids().size()== 2);
        assertNull(result.getLocationUuids().get(0));
        assertTrue(result.getLocationUuids().get(1).toString().matches(REGEX_UUID));
    }
    @Test
    public void canRetrieveAGarWithDeparture() throws Exception
    {
        // WITH
        final UUID userUuid = UUID.randomUUID() ;
        final UUID garUuid = UUID.randomUUID();
        GarPersistantRecord gar = GarRepoUtils.initRepoWithSingleGarWithDeparture(userUuid, garUuid, repo);

        ResponseEntity<Void> createResponse = this.restController.createAGar(userUuid);

        // WHEN
        Gar result = this.restController.getSingleGar(userUuid, garUuid);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getGarUuid()).isEqualTo(garUuid);
        assertThat(result.getUserUuid()).isEqualTo(userUuid);
        assertTrue(result.getLocationUuids().size()== 2);
        assertTrue(result.getLocationUuids().get(0).toString().matches(REGEX_UUID));
        assertNull(result.getLocationUuids().get(1));
    }


    @Test(expected=GarNotFoundException.class)
    public void cannotRetrieveAGarForDifferentUser() throws Exception
    {
        // WITH
        final UUID userUuid = UUID.randomUUID() ;
        ResponseEntity<Void> createResponse = this.restController.createAGar(userUuid);

        UUID garUuid = getGarUuid(createResponse);

        final UUID differentUser = UUID.randomUUID();

        // WHEN
         this.restController.getSingleGar(differentUser, garUuid);
    }

    @Test
    public void canUpdateAGar() throws Exception
    {
        // WITH
        final UUID userUuid = UUID.randomUUID() ;
        final long garCount = repo.findAllByUserUuidOrderByCreatedDesc(userUuid).size();
        ResponseEntity<Void> createResponse = this.restController.createAGar(userUuid);

        UUID garUuid = getGarUuid(createResponse);

        List<UUID> locations = new ArrayList<>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));

        List<UUID> files = new ArrayList<>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));

        Map<String, String> attributes = ImmutableMap.of("cta", "true", "hazardous", "false");

        List<UUID> crew = new ArrayList<>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));

        List<UUID> passengers = new ArrayList<>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));


        GarRequestPojo request = GarRequestPojo.builder().userUuid(userUuid).garUuid(garUuid).aircraftUuid(UUID.randomUUID())
                .locationUuids(locations).fileUuids(files).attributeMap(attributes).peopleOnGar(
                        GarPeopleRequestPojo.builder().captain(UUID.randomUUID()).crew(crew).passengers(passengers).build()
                ).submissionUuid(UUID.randomUUID()).build();


        // WHEN
        ResponseEntity<Void> result = this.restController.updateSingleGar(userUuid, garUuid, request);

        // THEN
        assertThat(repo.count()).isEqualTo(garCount+1);
        assertThat(result).isNotNull();
        assertThat(result.getHeaders()).isNotNull();
        assertThat(result.getHeaders().get("location")).isNotNull();

        assertThat(repo.getOne(garUuid)).isNotNull();

        Gar repoObject = repo.getOne(garUuid);
        assertThat(repoObject.getUserUuid()).isEqualTo(userUuid);
        assertThat(repoObject.getGarUuid()).isEqualTo(garUuid);
        assertThat(repoObject.getAircraftUuid()).isEqualTo(request.getAircraftUuid());
        assertThat(repoObject.getFileUuids()).contains(request.getFileUuids().toArray(new UUID[request.getFileUuids().size()]));
        assertThat(repoObject.getLocationUuids()).contains(request.getLocationUuids().toArray(new UUID[request.getLocationUuids().size()]));
        assertThat(repoObject.getAttributeMap()).hasSize(2);
        assertThat(repoObject.getAttributeMap()).contains(entry("cta", "true"), entry("hazardous", "false"));

        PeopleOnGar repoPeople = repoObject.getPeopleOnGar();
        assertThat(repoPeople.getCaptain()).isEqualTo(request.getPeopleOnGar().getCaptain());
        assertThat(repoPeople.getCrew()).contains(repoPeople.getCrew().toArray(new UUID[repoPeople.getCrew().size()]));
        assertThat(repoPeople.getPassengers()).contains(repoPeople.getPassengers().toArray(new UUID[repoPeople.getPassengers().size()]));

    }

    @Test(expected=GarNotFoundException.class)
    public void cannotUpdateAGarForAnotherUser() throws Exception
    {
        final UUID userUuid = UUID.randomUUID() ;
        ResponseEntity<Void> createResponse = this.restController.createAGar(userUuid);

        UUID garUuid = getGarUuid(createResponse);
        List<UUID> locations = new ArrayList<>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        List<UUID> files = new ArrayList<>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        Map<String, String> attributes = ImmutableMap.of("cta", "true", "hazardous", "false");
        List<UUID> crew = new ArrayList<>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        List<UUID> passengers = new ArrayList<>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));

        GarRequestPojo request = GarRequestPojo.builder().userUuid(userUuid).garUuid(garUuid).aircraftUuid(UUID.randomUUID())
                .locationUuids(locations).fileUuids(files).attributeMap(attributes).peopleOnGar(
                        GarPeopleRequestPojo.builder().captain(UUID.randomUUID()).crew(crew).passengers(passengers).build()
                ).build();


        UUID anotherUser = UUID.randomUUID();

        // WHEN
        this.restController.updateSingleGar(anotherUser, garUuid, request);

    }


    private UUID getGarUuid(ResponseEntity<Void> response) {
        String redirectPath = response.getHeaders().getLocation().getPath();
        String[] delimitedPath = redirectPath.split("/");
        String stringId = delimitedPath[delimitedPath.length -1];
        return UUID.fromString(stringId);
    }

}
