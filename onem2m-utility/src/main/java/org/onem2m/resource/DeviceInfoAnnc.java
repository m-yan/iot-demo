package org.onem2m.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonRootName(value = "m2m:dviA")
public class DeviceInfoAnnc extends Resource {

	@JsonProperty("mgd")
	@NonNull
	private Integer mgmtDefinition;
	
	@JsonProperty("dlb")
	@NonNull
	private String deviceLabel;

	@JsonProperty("man")
	@NonNull
	private String manufacturer;
	
	@JsonProperty("dty")
	@NonNull
	private String deviceType;
	
	@JsonProperty("swv")
	@NonNull
	private String swVersion;
	
	@Override
	public ResourceType getResourceType() {
		return null;
	}

}
