package com.hpe.ha.ipe.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.onem2m.mca.mqtt.TopicReference;
import org.onem2m.mca.mqtt.client.MqttConnection;
import org.onem2m.mca.mqtt.client.MqttConnections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpe.ha.ipe.util.ApplicationProperties;
import com.hpe.ha.ipe.util.RoundRobinIterator;

@Service
public class MqttConnectionManager {

	private static final Logger logger = LoggerFactory.getLogger(MqttConnectionManager.class);
	private static List<MqttConnection> mqttConnections = new ArrayList<>();
	private static Iterator<MqttConnection> roundRobinIterator;

	@Autowired
	private ApplicationProperties prop;

	@Autowired
	private MessageProcessor messageProcessor;

	public void connectAll() {
		String topic = TopicReference.getTopicForRequestToCseFrom(prop.getAeId());
		List<String> brokerURLs = prop.getCseBrokerUrls();
		brokerURLs.forEach(url -> {
			MqttConnection mqttConnection = MqttConnections.getConnectionTo(url);
			try {
				if (!mqttConnection.isConnected()) {
					mqttConnection.connect();
					mqttConnection.subscribe(topic, messageProcessor);
					mqttConnections.add(mqttConnection);
				}
			} catch (MqttException e) {
				logger.error("Failed to connect to the broker {}", url);
				// TODO: 接続失敗時の対処
			}
		});
		roundRobinIterator = new RoundRobinIterator<MqttConnection>(mqttConnections);
	}

	public void closeAll() {
		mqttConnections.forEach(mqttConnection -> {
			try {
				if (mqttConnection.isConnected()) {
					mqttConnection.close();
				}
			} catch (MqttException e) {
				logger.error("Failed to close the connection to the broker {}", mqttConnection.getBrokerUrl());
			}
		});
		messageProcessor.shutdown();
	}

	public List<String> getConnectedMqttBrokerList() {
		return mqttConnections.stream().filter(c -> c.isConnected()).map(c -> c.getBrokerUrl()) // filterで接続中の接続を抽出し、mapでMqttConnectionをbrokerUrl（String）に変換
				.collect(Collectors.toList());
	}

	public MqttConnection getConnection() {
		return roundRobinIterator.next();
	}
}
