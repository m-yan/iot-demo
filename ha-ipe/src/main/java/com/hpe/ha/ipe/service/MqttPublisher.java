package com.hpe.ha.ipe.service;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.onem2m.mca.mqtt.client.MqttConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqttPublisher {
	
	private static final Logger logger = LoggerFactory.getLogger(MqttPublisher.class);
	
	@Autowired
	MqttConnectionManager mqttConnectionManager;
	
	public boolean sendMessage(String topic, String strPayload) {
		byte[] bytePayload = strPayload.getBytes();
		MqttConnection mqttConnection = mqttConnectionManager.getConnection();
		try {
			mqttConnection.publish(topic, bytePayload);
			logger.info("MQTT message sended. topic: [{}]  payload: [{}]", topic, strPayload);
			return true;
		} catch (MqttException e) {
			logger.error("Failed to send MQTT message.");
			return false;
		}
	}

}
