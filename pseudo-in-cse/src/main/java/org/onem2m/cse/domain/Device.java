package org.onem2m.cse.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "devices")
@Data
@NoArgsConstructor
public class Device {
	@Id
	@NotNull
	@NonNull
	private String id;
	
	@Column
	private int motionDetectionStatus = 2;

	@Column
	private int monitoringMode = 1;
	
}
