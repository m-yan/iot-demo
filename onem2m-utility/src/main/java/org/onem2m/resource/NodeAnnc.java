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
