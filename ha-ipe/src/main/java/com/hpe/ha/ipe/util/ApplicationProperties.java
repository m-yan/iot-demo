package com.hpe.ha.ipe.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties
@Getter
@Setter
public class ApplicationProperties {
	private String cseBrokerUrl;
	private String inCseId;
	private String aeId;
	private String aeAuthToken;
	private String davHostname;
	private Integer davPort;
}
