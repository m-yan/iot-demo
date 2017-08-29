package com.hpe.ha.ipe.util;


import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties
@Getter
@Setter
public class ApplicationProperties {
	private List<String> cseBrokerUrls;
	private String aeId;
	private String aeAuthToken;
	private String irCommandsContainerUri;
	private String davHostname;
	private Integer davPort;
}
