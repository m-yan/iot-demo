package org.onem2m.cse.util;


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
	private String topicForSubscribe;
	private String topicForPublish;
}
