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
@JsonRootName(value = "ae")
public class AE extends Resource {

	@JsonProperty("api")
	@NonNull
	private String appID;
	
	@JsonProperty("aei")
	@NonNull
	private String aeID;

	@JsonProperty("apn")
	private String appName;
	
	@Override
	public ResourceType getResourceType() {
		return ResourceType.AE;
	}

}
