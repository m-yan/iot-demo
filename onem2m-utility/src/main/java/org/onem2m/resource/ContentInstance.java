package org.onem2m.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@JsonRootName(value = "m2m:cin")
@NoArgsConstructor
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
	
	public ContentInstance(String content) {
		super();
		this.content = content;
	}

	
}
