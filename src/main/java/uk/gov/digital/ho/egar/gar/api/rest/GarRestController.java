package uk.gov.digital.ho.egar.gar.api.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import uk.gov.digital.ho.egar.gar.api.GarRestApi;
import uk.gov.digital.ho.egar.gar.api.PathConstants;
import uk.gov.digital.ho.egar.gar.api.exceptions.GarDataserviceException;
import uk.gov.digital.ho.egar.gar.model.Gar;
import uk.gov.digital.ho.egar.gar.model.GarUuidList;
import uk.gov.digital.ho.egar.gar.model.rest.GarRequestPojo;
import uk.gov.digital.ho.egar.gar.model.rest.GarUuidListPojo;
import uk.gov.digital.ho.egar.gar.service.GarService;
import uk.gov.digital.ho.egar.gar.utils.UriLocationUtilities;
import uk.gov.digital.ho.egar.shared.auth.api.token.AuthValues;

import static uk.gov.digital.ho.egar.gar.api.GarApiResponse.*;

@RestController
@RequestMapping(PathConstants.ROOT_PATH)
@Api(value = PathConstants.ROOT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class GarRestController implements GarRestApi {
    protected final Log logger = LogFactory.getLog(getClass());

    private static final String USER_HEADER_NAME = "x-auth-subject";

    private final GarService garService;

    /**
     * The uri location utilities.
     */
    private final UriLocationUtilities uriLocationUtilities;

    public GarRestController(@Autowired final GarService garService, @Autowired final UriLocationUtilities uriLocationUtilities) {
        super();
        this.garService = garService;
        this.uriLocationUtilities = uriLocationUtilities;
    }

    @ApiOperation(value = "Get all GAR's for the current Security Principle User",
            notes = "Get all GAR's for a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = List.class),
            @ApiResponse(
                    code = 400,
                    message = SWAGGER_MESSAGE_BAD_REQUEST),
            @ApiResponse(
                    code = 401,
                    message = SWAGGER_MESSAGE_UNAUTHORISED)})
    @GetMapping(path = PathConstants.PATH_GARS, produces = MediaType.APPLICATION_JSON_VALUE)
    public GarUuidList getAllGars(
            @RequestHeader(USER_HEADER_NAME) UUID uuidOfUser
    ) throws GarDataserviceException {

        return GarUuidListPojo.create(garService.getAllUsersGars(uuidOfUser));
    }


    @ApiOperation(value = "Create a gar and link it to a user",
            notes = "Create a gar and link it to a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 303,
                    message = SWAGGER_MESSAGE_SUCCESSFUL_UPDATE_REDIRECT_KEY),
            @ApiResponse(
                    code = 400,
                    message = SWAGGER_MESSAGE_BAD_REQUEST),
            @ApiResponse(
                    code = 401,
                    message = SWAGGER_MESSAGE_UNAUTHORISED)})
    @PostMapping(path = PathConstants.PATH_GARS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.SEE_OTHER)
    public ResponseEntity<Void> createAGar(@RequestHeader(USER_HEADER_NAME) UUID uuidOfUser) throws GarDataserviceException, URISyntaxException {
        logger.info("Creating a gar for user: " + uuidOfUser.toString());

        Gar response = this.garService.createAGar(uuidOfUser);

        //Creating the redirection location URI
        URI redirectLocation = uriLocationUtilities.getGarURI(response.getGarUuid());

        //Creating the response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(redirectLocation);

        return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);
    }

    @ApiOperation(value = "Retrieve a gar linked to a user",
            notes = "Retrieve a gar linked to a user",
            response = Gar.class)
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = Gar.class),
            @ApiResponse(
                    code = 400,
                    message = SWAGGER_MESSAGE_BAD_REQUEST),
            @ApiResponse(
                    code = 401,
                    message = SWAGGER_MESSAGE_UNAUTHORISED)})
    @GetMapping(path = PathConstants.PATH_GAR,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Gar getSingleGar(
            @RequestHeader(USER_HEADER_NAME) UUID uuidOfUser,
            @PathVariable("gar_uuid") UUID garUuid) throws GarDataserviceException {
        logger.info("Retrieving gar " + garUuid.toString() + " for user: " + uuidOfUser.toString());

        return garService.getSingleGar(garUuid, uuidOfUser);
    }

    @ApiOperation(value = "Update a gar linked to a user",
            notes = "Update a gar linked to a user")
    @ApiResponses(value = {
            @ApiResponse(
                    code = 303,
                    message = SWAGGER_MESSAGE_SUCCESSFUL_UPDATE_REDIRECT_KEY),
            @ApiResponse(
                    code = 400,
                    message = SWAGGER_MESSAGE_BAD_REQUEST),
            @ApiResponse(
                    code = 401,
                    message = SWAGGER_MESSAGE_UNAUTHORISED)})
    @PostMapping(path = PathConstants.PATH_GAR,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.SEE_OTHER)
    @Override
    public ResponseEntity<Void> updateSingleGar(
            @RequestHeader(USER_HEADER_NAME) UUID uuidOfUser,
            @PathVariable("gar_uuid") UUID garUuid,
            @RequestBody Gar gar) throws GarDataserviceException, URISyntaxException {
        logger.info("Updating gar " + garUuid.toString() + " for user: " + uuidOfUser.toString());
        Gar response = garService.updateAGar(garUuid, uuidOfUser, gar);

        //Creating the redirection location URI
        URI redirectLocation = uriLocationUtilities.getGarURI(response.getGarUuid());

        //Creating the response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(redirectLocation);

        return new ResponseEntity<Void>(responseHeaders, HttpStatus.SEE_OTHER);

    }
    //---------------------------------------------------------------------------------------------------------
    
    /**
     * A get endpoint that bulk retrieves a list of GARs
     * -------------------------------------------------------------------------------------------
     * @throws GarDataserviceException 
     */
    
    
    @Override
    @ApiOperation(value = "Bulk retrieve a list of GAR summaries.",
            notes = "Retrieve a list of existing General Aviation Report in summary for a user")
    @ApiResponses(value = {
    		@ApiResponse(
                    code = 200,
                    message = SWAGGER_MESSAGE_SUCCESSFUL_RETRIEVED_KEY,
                    response = Gar[].class),
            @ApiResponse(
                    code = 401,
                    message = SWAGGER_MESSAGE_UNAUTHORISED)})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = PathConstants.PATH_BULK,
    			consumes = MediaType.APPLICATION_JSON_VALUE,
           		produces = MediaType.APPLICATION_JSON_VALUE)
    public Gar[] bulkRetrieveGARs(@RequestHeader(AuthValues.USERID_HEADER) UUID uuidOfUser, 
    									   @RequestBody List<UUID> garList) {
    	
    	return garService.getBulkGars(uuidOfUser,garList);
    }
    

    //	@ApiOperation(value = "Delete a gar linked to a user",
//			notes = "Delete a gar linked to a user")
//	@ApiResponses(value = { 
//			@ApiResponse(
//					code = 202,
//					message = "accepted"),
//			@ApiResponse(
//					code = 400,
//					message = "not found"),
//			@ApiResponse(
//					code = 401,
//					message = "unauthorised",
//					response = Exception.class)})// TODO correct thus exception
//	@DeleteMapping(path=PathConstants.PATH_GAR, 
//	produces= MediaType.APPLICATION_JSON_VALUE)
//	@ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteASingleGar(
            @RequestHeader(USER_HEADER_NAME) UUID uuidOfUser,
            @PathVariable("uuid") UUID garUuid) throws GarDataserviceException {
        // TODO Auto-generated method stub
    }

}
