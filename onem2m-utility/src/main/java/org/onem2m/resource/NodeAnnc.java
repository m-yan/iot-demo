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
@JsonRootName(value = "m2m:aeA")
public class NodeAnnc extends Resource{

	@JsonProperty("ni")
	@NonNull
	private String nodeID;
	
	@Override
	public ResourceType getResourceType() {
		return ResourceType.nodeAnnc;
	}

}
