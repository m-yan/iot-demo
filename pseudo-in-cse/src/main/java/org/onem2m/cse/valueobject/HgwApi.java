package org.onem2m.cse.valueobject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "Device")
@Data
@NoArgsConstructor
public class HgwApi {
	private Modules modules;

	@XmlElement(name = "Modules")
	public Modules getModules() {
		return modules;
	}

	@Data
	@NoArgsConstructor
	public static class Modules {
		private boolean operationStatus;
		private HgwDataPoints hgwDataPoints;

		@XmlElement(name = "hgwDataPoints")
		public HgwDataPoints getHgwDataPoints() {
			return hgwDataPoints;
		}

		@Data
		@NoArgsConstructor
		public static class HgwDataPoints {
			private Data data;
			
			@XmlElement(name = "Data")
			public Data getData() {
				return data;
			}

			@lombok.Data
			@NoArgsConstructor
			public static class Data {

				private int monitoringMode;

				@XmlElement(name = "monitoringMode")
				public int getMonitoringMode() {
					return monitoringMode;
				}
			}
		}
	}
}
