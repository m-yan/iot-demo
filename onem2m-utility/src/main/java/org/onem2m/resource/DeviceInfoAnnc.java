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
@JsonRootName(value = "dviA")
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
