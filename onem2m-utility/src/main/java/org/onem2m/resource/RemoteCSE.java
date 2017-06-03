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
@JsonRootName(value = "m2m:csr")
public class RemoteCSE extends Resource {

	@JsonProperty("poa")
	private String pointOfAccess;
	
	@JsonProperty("cb")
	@NonNull
	private String cseBase;
	
	@JsonProperty("csi")
	@NonNull
	private String cseID;
	
	@JsonProperty("rr")
	@NonNull
	private Boolean requestReachability;
	
	@JsonProperty("nl")	
	private String nodeLink;
	
	@Override
	public ResourceType getResourceType() {
		return ResourceType.remoteCSE;
	}

}
