package org.onem2m.cse;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.onem2m.cse.service.MqttConnectionManager;

@SpringBootApplication
public class PseudoInCse {

	private static final Logger logger = LoggerFactory.getLogger(PseudoInCse.class);
	
	@Autowired
	MqttConnectionManager mqttConnectionManager;
	
	@PostConstruct
	public void init() {
		logger.info("Init process started.");
		mqttConnectionManager.connectAll();
	}

	@PreDestroy
	public void exit() {
		mqttConnectionManager.closeAll();
		logger.info("Exit process finished.");
	}

	public static void main(String[] args) {
		SpringApplication.run(PseudoInCse.class, args);
	}
}
