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
@JsonRootName(value = "csr")
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
