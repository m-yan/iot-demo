package org.onem2m.mca.datatype;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class URIList {
	@JsonProperty("uril")
	private List<String> list;
}
