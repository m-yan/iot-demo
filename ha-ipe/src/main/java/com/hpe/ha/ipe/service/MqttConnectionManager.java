package com.hpe.ha.ipe.service;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.onem2m.mca.mqtt.TopicReference;
import org.onem2m.mca.mqtt.client.MqttConnection;
import org.onem2m.mca.mqtt.client.MqttConnections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpe.ha.ipe.util.ApplicationProperties;

@Service
public class MqttConnectionManager {

	private static final Logger logger = LoggerFactory.getLogger(MqttConnectionManager.class);
	private static MqttConnection mqttConnection = null;

	@Autowired
	private ApplicationProperties prop;

	@Autowired
	private MessageProcessor messageProcessor;
	
	public void connect() {
		String brokerURL = prop.getCseBrokerUrl();
			mqttConnection = MqttConnections.getConnectionTo(brokerURL);
			String topic = TopicReference.getTopicForRequestToCseFrom(prop.getAeId());

			try {
				if (!mqttConnection.isConnected()) {
					mqttConnection.connect();
					mqttConnection.subscribe(topic, messageProcessor);
				}
			} catch (MqttException e) {
				logger.error("Failed to connect to the broker {}", brokerURL);
			}		
	}

	public void close() {
			try {
				if (mqttConnection.isConnected()) {
					mqttConnection.close();
				}
			} catch (MqttException e) {
				logger.error("Failed to close the connection to the broker {}", mqttConnection.getBrokerUrl());
			}
		
	}

	public MqttConnection getConnection() {
		return mqttConnection;
	}
}
