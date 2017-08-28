package com.hpe.ha.ipe.valueobject;

import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Data;
import lombok.NoArgsConstructor;

@JsonRootName(value = "m2m:nev")
@Data
@NoArgsConstructor
public class Notification {
	private Representation rep;
	
	@Data
	@NoArgsConstructor
	public static class Representation {
		private String con;
	}

}
