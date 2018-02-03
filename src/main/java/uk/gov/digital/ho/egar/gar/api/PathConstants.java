package uk.gov.digital.ho.egar.gar.api;

import uk.gov.digital.ho.egar.constants.ServicePathConstants;

public interface PathConstants {

	String ROOT_SERVICE_NAME = "GARs";
	String ROOT_PATH = ServicePathConstants.ROOT_PATH_SEPERATOR + ServicePathConstants.ROOT_SERVICE_API
			+ ServicePathConstants.ROOT_PATH_SEPERATOR + ServicePathConstants.SERVICE_VERSION_ONE
			+ ServicePathConstants.ROOT_PATH_SEPERATOR + ROOT_SERVICE_NAME;
	String PATH_GARS = "/";
	String PATH_GAR = "{gar_uuid}/";

}