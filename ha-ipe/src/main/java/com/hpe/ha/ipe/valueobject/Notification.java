package com.hpe.ha.ipe.valueobject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonRootName(value = "m2m:sgn")
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification {
	private NotifcationEvent nev;
	
	@Data
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class NotifcationEvent {
		private Representation rep;
		
		@Data
		@NoArgsConstructor
		@JsonIgnoreProperties(ignoreUnknown = true)
		public static class Representation {
			private String con;
		}
		
	}
	
	

}
