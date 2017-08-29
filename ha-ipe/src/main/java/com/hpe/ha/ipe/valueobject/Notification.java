package com.hpe.ha.ipe.valueobject;

import org.onem2m.resource.ContentInstance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "m2m:sgn")
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
			private ContentInstance cin;
		}
		
	}
	
	

}
