package org.onem2m.mca.mqtt.client;

import java.util.HashMap;
import java.util.Map;

/**
 * com.kddi.telematics.mqttclient.MqttConnectionの実装クラス用のファクトリ兼インスタンス管理クラス。<br>
 * 
 * MQTT接続は1つのブローカーに対して1つあればいいため、<br>
 * 本クラスのスタティックファクトリメソッドによってcom.kddi.telematics.mqttclient.MqttConnectionの利用クラスに1ブローカーあたり1インスタンスの制約を強制する。<br>
 * 
 */
public final class MqttConnections {

	private static final Map<String, MqttConnection> connectionList = new HashMap<>();

	private MqttConnections() {
	}

	/**
	 * 指定されたMQTTブローカーとの接続を扱うcom.kddi.telematics.mqttclient.MqttConnectionの実装クラスのインスタンスを返すスタティックファクトリメソッド。<br>
	 * 1ブローカーあたり1接続の制約で生成したインスタンスを返す。<br>
	 * 
	 * @return 指定されたMQTTブローカーとの接続を扱うcom.kddi.telematics.mqttclient.MqttConnectionのインスタンス
	 */
	public static synchronized MqttConnection getConnectionTo(String mqttBrokerURL) {
		MqttConnection connection = connectionList.get(mqttBrokerURL);
		if (connection == null) {
			if (mqttBrokerURL.contains("ssl")) {
				connection = new TlsMqttConnection(mqttBrokerURL);
			} else {
				connection = new PlainMqttConnection(mqttBrokerURL);
			}

			connectionList.put(mqttBrokerURL, connection);
		}
		return connection;
	}
}
