package uk.gov.digital.ho.egar.gar;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import uk.gov.digital.ho.egar.gar.model.Gar;
import uk.gov.digital.ho.egar.gar.model.PeopleOnGar;
import uk.gov.digital.ho.egar.gar.model.rest.GarPeopleRequestPojo;
import uk.gov.digital.ho.egar.gar.model.rest.GarRequestPojo;
import uk.gov.digital.ho.egar.gar.service.repository.GarPersistantRecordRepository;
import uk.gov.digital.ho.egar.gar.service.repository.model.*;
import uk.gov.digital.ho.egar.gar.utils.GarRepoUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.civica.microservice.util.testing.matcher.RegexMatcher.matchesRegex;
import static org.hamcrest.Matchers.hasSize;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(properties
        ={
        "eureka.client.enabled=false",
        "spring.cloud.config.discovery.enabled=false",
        "flyway.enabled=false",
        "spring.profiles.active=h2",
        "spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true"
})
@AutoConfigureMockMvc
public class EndpointTest {

    private static final String REGEX_UUID = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
	private static final String GAR_ENDPOINT = "/api/v1/GARs/";
    private static final String GAR_ENDPOINT_REGEX = "/api/v1/GARs/"+REGEX_UUID + "/";

    private static final String UPDATED_GAR_REQUEST = "{\"gar_uuid\":\"d8216eda-3854-4824-a6eb-e914bfd158ae\",\"user_uuid\":\"ff144d99-6292-4b2e-b1db-4aaf8cf1978b\",\"aircraft_uuid\":\"b5f47cbc-76e2-4e9e-92c8-b33809bfa503\",\"location_uuids\":[null,\"980f13e2-ab23-491a-8ecf-c9aa3a1a2313\"],\"file_uuids\":[\"be7a1f0c-b214-4888-872b-96efd6d83e78\",\"254bf626-2679-4d9e-84ee-6a3f4085855b\"],\"attributes\":{\"cta\":\"true\",\"hazardous\":\"false\"},\"people\":{\"captain\":\"011f4055-5f39-4c99-ac4e-ed33023fa329\",\"crew\":[\"c2c893ed-82da-4d61-ab0a-28bf1b87e3df\",\"740369e7-a6c2-4450-8b69-cb5e7cf17cfd\"],\"passengers\":[\"daab4dd6-b12f-4d53-97b9-1fc3c6c696b8\",\"61878e1f-10af-470d-8e16-2594079687db\"]}}";
    private static final String X_AUTH_SUBJECT = "x-auth-subject";

    @Autowired
    private GarPersistantRecordRepository repo;
	
    @Autowired
    private GarApiApplication app;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

	@Test
	public void contextLoads() {
	    assertThat(app).isNotNull();
	}

	/**
	 * SpringBoot heath endpoint
	 * @throws Exception
	 */
    @Test
    public void shouldPostToCreateAGar() throws Exception {
    	
    	// WHEN
        this.mockMvc
                .perform(post(GAR_ENDPOINT).header(X_AUTH_SUBJECT, UUID.randomUUID()))
                .andDo(print())
                .andExpect(status().isSeeOther())
                .andExpect(header().string("location", matchesRegex(GAR_ENDPOINT_REGEX)))
                ;
    }
    
	/**
	 * SpringBoot heath endpoint
	 * @throws Exception
	 */
    @Test
    public void shouldGetToFetchGars() throws Exception {
    	
    	// WITH
    	repo.deleteAll();
    	UUID userUuid = UUID.randomUUID();
    	
    	// One for the current user
		GarPersistantRecord gar = GarPersistantRecord.builder()
				.garUuid(UUID.randomUUID())
				.userUuid(userUuid)
				.build();
    	repo.saveAndFlush(gar);
    	// One at random
    	repo.saveAndFlush(GarPersistantRecord.builder()
				.garUuid(UUID.randomUUID())
				.userUuid(UUID.randomUUID())
				.build());
    	
    	
    	
    	// WHEN
        this.mockMvc
                .perform(get(GAR_ENDPOINT).header(X_AUTH_SUBJECT, userUuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.gar_uuids").exists())
        		.andExpect(jsonPath("$.gar_uuids").isArray())
        		.andExpect(jsonPath("$.gar_uuids", hasSize(1))  )
        		.andExpect(jsonPath("$.gar_uuids[0]", matchesRegex(REGEX_UUID)))
        		.andExpect(jsonPath("$.gar_uuids[0]", is(gar.getGarUuid().toString())))
        		;
    }

	@Test
	public void shouldGetToFetchSingleGar() throws Exception {

		// WITH
		repo.deleteAll();
		UUID userUuid = UUID.randomUUID();
        UUID garUuid = UUID.randomUUID();


        // One for the current user
		GarPersistantRecord gar = GarRepoUtils.initRepoWithSingleGarWithBothLocations(userUuid, garUuid, repo);

        // WHEN
        this.mockMvc
                .perform(get(GAR_ENDPOINT + gar.getGarUuid() + "/").header(X_AUTH_SUBJECT, userUuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.gar_uuid").exists())
                .andExpect(jsonPath("$.gar_uuid", is(gar.getGarUuid().toString())))
                .andExpect(jsonPath("$.user_uuid", is(userUuid.toString())))
                .andExpect(jsonPath("$.people.captain", is(gar.getCaptainUuid().toString())))
                .andExpect(jsonPath("$.aircraft_uuid", is(gar.getAircraftUuid().toString())))
                .andExpect(jsonPath("$.location_uuids").isArray())
                .andExpect(jsonPath("$.location_uuids", hasSize(2)))
                .andExpect(jsonPath("$.location_uuids[0]", is(gar.getLocations().get(0).getValueUuid().toString())))
                .andExpect(jsonPath("$.location_uuids[1]", is(gar.getLocations().get(1).getValueUuid().toString())))
                .andExpect(jsonPath("$.file_uuids").isArray())
                .andExpect(jsonPath("$.file_uuids", hasSize(1)))
                .andExpect(jsonPath("$.file_uuids[0]", is(gar.getFiles().get(0).getValueUuid().toString())))
                .andExpect(jsonPath("$.people.passengers").isArray())
                .andExpect(jsonPath("$.people.passengers", hasSize(1)))
                .andExpect(jsonPath("$.people.passengers[0]", is(gar.getPassengers().get(0).getValueUuid().toString())))
                .andExpect(jsonPath("$.people.crew").isArray())
                .andExpect(jsonPath("$.people.crew", hasSize(2)))
                .andExpect(jsonPath("$.people.crew", containsInAnyOrder(is(gar.getCrew().get(0).getValueUuid().toString()), is(gar.getCrew().get(1).getValueUuid().toString()))))
                .andExpect(jsonPath("$.submission_uuid").exists())
                .andExpect(jsonPath("$.submission_uuid", is (gar.getSubmissionUuid().toString())));

	}

    @Test
    public void shouldGetToFetchSingleGarArrivalOnly() throws Exception {
        // WITH
        repo.deleteAll();
        UUID userUuid = UUID.randomUUID();
        UUID garUuid = UUID.randomUUID();


        // One for the current user
        GarPersistantRecord gar = GarRepoUtils.initRepoWithSingleGarWithArrival(userUuid, garUuid, repo);

        // WHEN
        this.mockMvc
                .perform(get(GAR_ENDPOINT + gar.getGarUuid() + "/").header(X_AUTH_SUBJECT, userUuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.gar_uuid").exists())
                .andExpect(jsonPath("$.gar_uuid", is(gar.getGarUuid().toString())))
                .andExpect(jsonPath("$.user_uuid", is(userUuid.toString())))
                .andExpect(jsonPath("$.people.captain", is(gar.getCaptainUuid().toString())))
                .andExpect(jsonPath("$.aircraft_uuid", is(gar.getAircraftUuid().toString())))
                .andExpect(jsonPath("$.location_uuids").isArray())
                .andExpect(jsonPath("$.location_uuids", hasSize(2)))
                .andExpect(jsonPath("$.location_uuids[0]", is(nullValue())))
                .andExpect(jsonPath("$.location_uuids[1]", is(gar.getLocations().get(0).getValueUuid().toString())))
                .andExpect(jsonPath("$.file_uuids").isArray())
                .andExpect(jsonPath("$.file_uuids", hasSize(1)))
                .andExpect(jsonPath("$.file_uuids[0]", is(gar.getFiles().get(0).getValueUuid().toString())))
                .andExpect(jsonPath("$.people.passengers").isArray())
                .andExpect(jsonPath("$.people.passengers", hasSize(1)))
                .andExpect(jsonPath("$.people.passengers[0]", is(gar.getPassengers().get(0).getValueUuid().toString())))
                .andExpect(jsonPath("$.people.crew").isArray())
                .andExpect(jsonPath("$.people.crew", hasSize(2)))
                .andExpect(jsonPath("$.people.crew", containsInAnyOrder(is(gar.getCrew().get(0).getValueUuid().toString()), is(gar.getCrew().get(1).getValueUuid().toString()))))
                .andExpect(jsonPath("$.submission_uuid").exists())
                .andExpect(jsonPath("$.submission_uuid", is (gar.getSubmissionUuid().toString())));
    }

    @Test
    public void shouldGetToFetchSingleGarDepartureOnly() throws Exception {
        // WITH
        repo.deleteAll();
        UUID userUuid = UUID.randomUUID();
        UUID garUuid = UUID.randomUUID();


        // One for the current user
        GarPersistantRecord gar = GarRepoUtils.initRepoWithSingleGarWithDeparture(userUuid, garUuid, repo);

        // WHEN
        this.mockMvc
                .perform(get(GAR_ENDPOINT + gar.getGarUuid() + "/").header(X_AUTH_SUBJECT, userUuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.gar_uuid").exists())
                .andExpect(jsonPath("$.gar_uuid", is(gar.getGarUuid().toString())))
                .andExpect(jsonPath("$.user_uuid", is(userUuid.toString())))
                .andExpect(jsonPath("$.people.captain", is(gar.getCaptainUuid().toString())))
                .andExpect(jsonPath("$.aircraft_uuid", is(gar.getAircraftUuid().toString())))
                .andExpect(jsonPath("$.location_uuids").isArray())
                .andExpect(jsonPath("$.location_uuids", hasSize(2)))
                .andExpect(jsonPath("$.location_uuids[0]", is(gar.getLocations().get(0).getValueUuid().toString())))
                .andExpect(jsonPath("$.location_uuids[1]", is(nullValue())))
                .andExpect(jsonPath("$.file_uuids").isArray())
                .andExpect(jsonPath("$.file_uuids", hasSize(1)))
                .andExpect(jsonPath("$.file_uuids[0]", is(gar.getFiles().get(0).getValueUuid().toString())))
                .andExpect(jsonPath("$.people.passengers").isArray())
                .andExpect(jsonPath("$.people.passengers", hasSize(1)))
                .andExpect(jsonPath("$.people.passengers[0]", is(gar.getPassengers().get(0).getValueUuid().toString())))
                .andExpect(jsonPath("$.people.crew").isArray())
                .andExpect(jsonPath("$.people.crew", hasSize(2)))
                .andExpect(jsonPath("$.people.crew", containsInAnyOrder(is(gar.getCrew().get(0).getValueUuid().toString()), is(gar.getCrew().get(1).getValueUuid().toString()))))
                .andExpect(jsonPath("$.submission_uuid").exists())
                .andExpect(jsonPath("$.submission_uuid", is (gar.getSubmissionUuid().toString())));
    }

    @Test
    public void shouldGetToFetchSingleGarNoLocations() throws Exception {
        // WITH
        repo.deleteAll();
        UUID userUuid = UUID.randomUUID();
        UUID garUuid = UUID.randomUUID();


        // One for the current user
        GarPersistantRecord gar = GarRepoUtils.initRepoWithSingleGarNoLocations(userUuid, garUuid, repo);

        // WHEN
        this.mockMvc
                .perform(get(GAR_ENDPOINT + gar.getGarUuid() + "/").header(X_AUTH_SUBJECT, userUuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.gar_uuid").exists())
                .andExpect(jsonPath("$.gar_uuid", is(gar.getGarUuid().toString())))
                .andExpect(jsonPath("$.user_uuid", is(userUuid.toString())))
                .andExpect(jsonPath("$.people.captain", is(gar.getCaptainUuid().toString())))
                .andExpect(jsonPath("$.aircraft_uuid", is(gar.getAircraftUuid().toString())))
                .andExpect(jsonPath("$.location_uuids").isArray())
                .andExpect(jsonPath("$.location_uuids", hasSize(2)))
                .andExpect(jsonPath("$.location_uuids[0]", is(nullValue())))
                .andExpect(jsonPath("$.location_uuids[1]", is(nullValue())))
                .andExpect(jsonPath("$.file_uuids").isArray())
                .andExpect(jsonPath("$.file_uuids", hasSize(1)))
                .andExpect(jsonPath("$.file_uuids[0]", is(gar.getFiles().get(0).getValueUuid().toString())))
                .andExpect(jsonPath("$.people.passengers").isArray())
                .andExpect(jsonPath("$.people.passengers", hasSize(1)))
                .andExpect(jsonPath("$.people.passengers[0]", is(gar.getPassengers().get(0).getValueUuid().toString())))
                .andExpect(jsonPath("$.people.crew").isArray())
                .andExpect(jsonPath("$.people.crew", hasSize(2)))
                .andExpect(jsonPath("$.people.crew", containsInAnyOrder(is(gar.getCrew().get(0).getValueUuid().toString()), is(gar.getCrew().get(1).getValueUuid().toString()))))
                .andExpect(jsonPath("$.submission_uuid").exists())
                .andExpect(jsonPath("$.submission_uuid", is (gar.getSubmissionUuid().toString())));
    }


    @Test
    public void unableToFetchSingleGarForAnotherUser() throws Exception {

        // WITH
        repo.deleteAll();
        UUID userUuid = UUID.randomUUID();

        // One for the current user
        GarPersistantRecord gar = GarPersistantRecord.builder()
                .garUuid(UUID.randomUUID())
                .userUuid(userUuid)
                .build();
        gar = repo.saveAndFlush(gar);

        UUID anotherUser = UUID.randomUUID();

        //THEN
        this.mockMvc
                .perform(get(GAR_ENDPOINT + gar.getGarUuid() + "/").header(X_AUTH_SUBJECT, anotherUser))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldUpdateGar() throws Exception {

        // WITH
        repo.deleteAll();
        UUID userUuid = UUID.randomUUID();
        UUID garUuid = UUID.randomUUID();

        GarPersistantRecord existingGar = GarRepoUtils.initRepoWithSingleGarNoLocations(userUuid, garUuid, repo);

        //UpdatedGar


        // WHEN
        this.mockMvc
                .perform(post(GAR_ENDPOINT + garUuid + "/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(UPDATED_GAR_REQUEST)
                        .header(X_AUTH_SUBJECT, userUuid))
                .andDo(print())
                .andExpect(status().isSeeOther())
                .andExpect(header().string("location", is(GAR_ENDPOINT + garUuid  + "/")));

        assertThat(repo.getOne(existingGar.getGarUuid())).isNotNull();

        Gar repoObject = repo.getOne(existingGar.getGarUuid());
        assertThat(repoObject.getUserUuid()).isEqualTo(userUuid);
        assertThat(repoObject.getGarUuid()).isEqualTo(garUuid);
        assertThat(repoObject.getAircraftUuid()).isEqualTo(UUID.fromString("b5f47cbc-76e2-4e9e-92c8-b33809bfa503"));
        assertThat(repoObject.getFileUuids()).hasSize(2);
        assertThat(repoObject.getFileUuids()).contains(UUID.fromString("be7a1f0c-b214-4888-872b-96efd6d83e78"), UUID.fromString("254bf626-2679-4d9e-84ee-6a3f4085855b"));
        assertThat(repoObject.getLocationUuids()).hasSize(2);
        assertThat(repoObject.getLocationUuids()).contains(null,UUID.fromString("980f13e2-ab23-491a-8ecf-c9aa3a1a2313"));
        assertThat(repoObject.getAttributeMap()).hasSize(2);
        assertThat(repoObject.getAttributeMap()).contains(entry("cta", "true"), entry("hazardous", "false"));

        PeopleOnGar repoPeople = repoObject.getPeopleOnGar();
        assertThat(repoPeople.getCaptain()).isEqualTo(UUID.fromString("011f4055-5f39-4c99-ac4e-ed33023fa329"));
        assertThat(repoPeople.getCrew()).hasSize(2);
        assertThat(repoPeople.getCrew()).contains(UUID.fromString("c2c893ed-82da-4d61-ab0a-28bf1b87e3df"), UUID.fromString("740369e7-a6c2-4450-8b69-cb5e7cf17cfd"));
        assertThat(repoPeople.getPassengers()).hasSize(2);
        assertThat(repoPeople.getPassengers()).contains(UUID.fromString("daab4dd6-b12f-4d53-97b9-1fc3c6c696b8"), UUID.fromString("61878e1f-10af-470d-8e16-2594079687db"));

    }

    @Test
    public void unableToUpdateGarForAnotherUser() throws Exception {

        // WITH
        repo.deleteAll();
        UUID userUuid = UUID.randomUUID();

        // One for the current user
        GarPersistantRecord gar = GarPersistantRecord.builder()
                .garUuid(UUID.randomUUID())
                .userUuid(userUuid)
                .build();
        gar = repo.saveAndFlush(gar);

        Gar request = GarRequestPojo.builder().userUuid(userUuid).garUuid(gar.getGarUuid()).aircraftUuid(UUID.randomUUID())
                .peopleOnGar(
                        GarPeopleRequestPojo.builder().captain(UUID.randomUUID()).build()
                ).build();

        UUID anotherUser = UUID.randomUUID();

        //THEN
        this.mockMvc
                .perform(post(GAR_ENDPOINT + gar.getGarUuid() + "/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(mapper.writeValueAsString(request))
                        .header(X_AUTH_SUBJECT, anotherUser))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }




}
