package org.onem2m.mca.datatype;

import org.onem2m.mca.mqtt.Primitive.FilterUsage;
import org.onem2m.resource.Resource.ResourceType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FilterCriteria {
	@JsonProperty("ty")
	private Integer resourceType;
	
	@JsonProperty("atr")
	private Attribute attribute;
	
	@JsonProperty("fu")
	private Integer filterUsage;
	
	public FilterCriteria(ResourceType resourceType, Attribute attribute, FilterUsage fiterUsage) {
		this.resourceType = resourceType.getValue();
		this.attribute = attribute;
		this.filterUsage = fiterUsage.getValue();
	}
}
