package org.onem2m.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@JsonRootName(value = "m2m:aeA")
@NoArgsConstructor
public class AEAnnc extends Resource {

	@JsonProperty("api")
	@NonNull
	private String appID;
	
	@JsonProperty("aei")
	@NonNull
	private String aeID;

	@JsonProperty("rr")
	@NonNull
	private Boolean requestReachability;
	
	@JsonProperty("apn")
	private String appName;
	
	@Override
	public ResourceType getResourceType() {
		return ResourceType.AEAnnc;
	}

}
