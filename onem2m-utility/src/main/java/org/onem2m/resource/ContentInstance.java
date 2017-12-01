package org.onem2m.resource;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@JsonRootName(value = "m2m:cin")
public class ContentInstance extends Resource {
	
	@JsonProperty("cnf")
	private String contentInfo;
	
	@JsonProperty("cs")
	private String contentSize;

	@JsonProperty("conr")
	private String contentRef;

	@JsonProperty("or")
	private String ontologyRef;
	
	@JsonProperty("con")
	@NonNull
	private String content;

	@Override
	public ResourceType getResourceType() {
		return ResourceType.contentInstance;
	}
	
	public ContentInstance(@NonNull String content) {
		super();
		this.content = content;
	}

	public static ContentInstance parse(String jsonResource) {
		try {
			return mapper.readValue(jsonResource, ContentInstance.class);
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
	
}
