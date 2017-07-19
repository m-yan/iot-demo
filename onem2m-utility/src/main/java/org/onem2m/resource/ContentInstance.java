package org.onem2m.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
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

	
}
