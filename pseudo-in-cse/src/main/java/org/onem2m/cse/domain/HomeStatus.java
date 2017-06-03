package org.onem2m.cse.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "home_status")
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HomeStatus {
	@Id
	@NotNull
	@NonNull
	private String id;
	
	@Column
	private Integer monitoringMode = 1;
	
	@Column
	private Integer motionDetectionStatus = 2;

	@Column
	private Float temperature = 25.0f;

	@Column
	private Integer humidity = 60;
	
	@Column
	private Integer illuminance = 1000;
	
	public void setEnvironmentalData(HomeStatus homeStatus) {
		this.temperature = homeStatus.getTemperature();
		this.humidity = homeStatus.getHumidity();
		this.illuminance = homeStatus.getIlluminance();
	}
	
}
