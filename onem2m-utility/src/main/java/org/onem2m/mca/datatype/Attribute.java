package org.onem2m.mca.datatype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Attribute {
	@JsonProperty("nm")
	private String name;
	
	@JsonProperty("val")
	private String value; 
}