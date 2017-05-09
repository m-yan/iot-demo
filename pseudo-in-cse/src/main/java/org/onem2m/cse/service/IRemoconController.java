package org.onem2m.cse.service;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.onem2m.cse.util.ApplicationProperties;
import org.onem2m.mca.mqtt.client.MqttConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class IRemoconController {
	
	private static final Logger logger = LoggerFactory.getLogger(IRemoconController.class);
	
	@Autowired
	MqttConnectionManager mqttConnectionManager;
	
	@Autowired
	private ApplicationProperties prop;
	
	public void sendInfrared(int infraredId) {
		String command = String.format("*is;%d", infraredId);
		this.sendCommand(command);
	}
	
	public void learnInfrared(int infraredId) {
		String command = String.format("*ic;%d", infraredId);
		this.sendCommand(command);
	}
	
	private void sendCommand(String command) {
		String topic = prop.getTopicForPublish();
		byte[] payload = command.getBytes();
		MqttConnection mqttConnection = mqttConnectionManager.getConnection();
		try {
			mqttConnection.publish(topic, payload);
		} catch (MqttException e) {
			logger.error("Failed to send command. There is some problem in communication with CSE.");
		}
	}
	
}
