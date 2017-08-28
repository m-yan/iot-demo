package com.hpe.ha.ipe;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.hpe.ha.ipe.service.MqttConnectionManager;

@SpringBootApplication
public class IPE {

	private static final Logger logger = LoggerFactory.getLogger(IPE.class);
	
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
		SpringApplication.run(IPE.class, args);
	}
}
