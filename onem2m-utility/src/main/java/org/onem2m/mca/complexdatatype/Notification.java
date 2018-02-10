package org.onem2m.mca.complexdatatype;

import java.io.IOException;

import org.onem2m.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "m2m:sgn")
public class Notification {
	
	private static final Logger logger = LoggerFactory.getLogger(Notification.class);

	private static final ObjectMapper mapper = new ObjectMapper();	
	static {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
		mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
	}
	
	@NonNull
	private NotifcationEvent nev;
	
	@Data
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class NotifcationEvent {
		@NonNull
		private Object rep;
		
		@JsonIgnore
		public <T extends Resource> T getRepresentationCastedBy(Class<T> type) {
			try {
				String jsonRepresentation = new ObjectMapper().writeValueAsString(this.rep);
				return Resource.parse(jsonRepresentation, type);
			} catch (JsonProcessingException e) {
				logger.warn("Failed to cast to the specified type.");
			}
			return null;
		}
	}
	
	public static Notification parse(String jsonResource) {
		try {
			return mapper.readValue(jsonResource, Notification.class);
		} catch (JsonParseException e) {
			logger.warn("Received JSON format is invalid.");
			return null;
		} catch (JsonMappingException e) {
			logger.warn("Received Resource does not conform to the terms of oneM2M.");
			logger.warn(e.getMessage());
			return null;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	public String toJson() {
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

}
