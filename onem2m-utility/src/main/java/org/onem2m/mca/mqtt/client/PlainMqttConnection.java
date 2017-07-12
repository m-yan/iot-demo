package org.onem2m.mca.mqtt.client;

import java.util.Arrays;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;

/**
 * MqttConnectionの実装クラス。 oneM2Mの仕様に準拠する実装になっている。
 * パッケージ外から本クラスへの依存性を持たせないようにするため可視性はデフォルトとする。
 * 
 */
class PlainMqttConnection implements MqttConnection {

	protected static final Logger logger = LoggerFactory.getLogger(MqttConnection.class);
	private static final MqttConnectionProperties mqttConnProp = MqttConnectionProperties.getInstance();
	protected final MqttConnectOptions connOpt = new MqttConnectOptions();

	private MqttMessageProcessable recevier = null;
	private MqttClient mqttClient = null;

	@Getter
	private final String brokerUrl;
	
	private String[] subscribingTopics;
	
	private boolean isClosed = false;

	// 1ブローカーあたり1接続の制約を守るため、
	// パッケージ外からは、com.kddi.telematics.mqttclient.MqttConnectionsのスタティックファクトリメソッドからのインスタンス取得させるものとし、
	// 可視性はデフォルトとして、パッケージ外からの直接のインスタンス化を制限する。
	PlainMqttConnection(String brokerUrl) {
		this.brokerUrl = brokerUrl;

		// oneM2Mの以下の仕様に準拠して接続オプションを設定
		// ・A client shall set the "Clean Session" flag in the CONNECT packet to
		// false.
		// ・A client shall not set the "Will Flag".
		// ・so Will Messages are not enabled A client may choose to provide a
		// non-zero MQTT KeepAlive value or to provide a KeepAlive of 0.
		// ・The MQTT server may require that a client provides a User Name and a
		// password (or other credential).
		// ※PolicyNetはuser/passでの認証は行わない
		connOpt.setCleanSession(false);
		connOpt.setKeepAliveInterval(mqttConnProp.getKeepAliveInterval());
	}

	@Override
	public synchronized void connect() throws MqttException, MqttSecurityException {
		if (mqttClient != null && mqttClient.isConnected()) {
			// 接続済の場合は何もしない
			return;
		}

		// oneM2MではClientIDのフォーマット仕様（A::<IN-AE-ID>で固定）が規定されており、同じIN-AEのClientIDは同じになる。
		// Brokerは同じClientIDからの複数の接続は許可しないため、、
		// oneM2MのClientID仕様に準拠すると、IN-AEを分散アプリケーションにするとBrokerとの接続に問題が生じるケースが出てきてしまう。
		// そのため、CSE側との接続に支障がないかぎりは、ClientIDは仕様に準拠しないものとする。
		mqttClient = new MqttClient(brokerUrl, MqttClient.generateClientId(), new MemoryPersistence());
		mqttClient.setCallback(this);

		logger.info("Connecting to {} ....", mqttClient.getServerURI());
		mqttClient.connect(connOpt);
		logger.info("Connected to {}", mqttClient.getServerURI());
		isClosed = false;

	}

	@Override
	public void subscribe(String topic, MqttMessageProcessable recevier) throws MqttException {
		if (mqttClient == null || !mqttClient.isConnected()) {
			// 未接続の場合は何もしない
			return;
		}
		this.recevier = recevier;
		
		mqttClient.subscribe(topic, mqttConnProp.getQos());
		logger.info("Subscribing topic. " + topic);
		
		// 切断➝再接続時の再Subscribeのためトピックを保持
		this.subscribingTopics = new String[1];
		this.subscribingTopics[0] = topic;
	}

	@Override
	public void subscribe(String[] topics, MqttMessageProcessable recevier) throws MqttException {
		if (mqttClient == null || !mqttClient.isConnected()) {
			// 未接続の場合は何もしない
			return;
		}
		this.recevier = recevier;
		int[] qos = new int[topics.length];
		Arrays.fill(qos, mqttConnProp.getQos());
		mqttClient.subscribe(topics, qos);
		Arrays.asList(topics).stream().forEach(s -> logger.info("Subscribing topic. " + s));
		
		// 切断➝再接続時の再Subscribeのためトピックを保持
		this.subscribingTopics = topics;
	}

	@Override
	public void publish(String topic, byte[] payload) throws MqttException {
		// 未接続の場合は何もしない
		if (mqttClient == null || !mqttClient.isConnected()) {
			return;
		}
		MqttTopic mqttTopic = mqttClient.getTopic(topic);
		MqttMessage message = new MqttMessage(payload);

		// oneM2Mの以下の仕様したMQTT publishを行う。
		// ・MQTT's "QoS 1" message reliability level.
		// ・It does not use the following features: Retained Messages.<br>
		message.setQos(mqttConnProp.getQos());
		message.setRetained(false);

		// Publishは、失敗しても指定回数リトライする。
		int maxRetryCount = mqttConnProp.getMaxRetryCount();
		int retryInterval = mqttConnProp.getRetryInterval();
		MqttDeliveryToken token = null;
		for (int i = 0; i <= maxRetryCount; i++) {
			// closeが呼ばれた場合は処理を中断する
			if (isClosed) {
				return;
			}

			try {
				token = mqttTopic.publish(message);
				token.waitForCompletion();
				logger.debug("The message was published successfully.");
				return;
			} catch (MqttException e) {
				logger.error("MQTT PUBLISH failed. " + e.getMessage());
				try {
					Thread.sleep(retryInterval * 1000);
				} catch (InterruptedException e1) {
					return;
				}
			}
		}
		logger.error("Failed to retry PUBLISH.");
		throw new MqttException(new Throwable());
	}

	@Override
	public synchronized void close() throws MqttException {
		isClosed = true;
		if (mqttClient != null && mqttClient.isConnected()) {
			mqttClient.disconnect();
			mqttClient.close();
			logger.info("MQTT connection close.");
		}
	}

	@Override
	public void messageArrived(String topic, MqttMessage msg) {
		if (recevier != null) {
			recevier.process(topic, msg.getId(), msg.getQos(), msg.getPayload());
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		logger.error("Connection to {} was lost. Reconnecting..........", mqttClient.getServerURI());

		// 接続がロストした場合は、接続できるまで再接続を試みる
		int reconnectInterval = mqttConnProp.getReconnectInterval();
		while (true) {
			// closeが呼ばれた場合はループを抜ける
			if (isClosed) {
				return;
			}

			try {
				if (!mqttClient.isConnected()) {
					mqttClient.connect(connOpt);
					logger.info("Reconnected to " + mqttClient.getServerURI());
					this.subscribe(this.subscribingTopics, this.recevier);
				}
				return;
			} catch (MqttException e) {
				logger.error("Failed to reconnect to the broker. " + e.getMessage());
				try {
					Thread.sleep(reconnectInterval * 1000);
				} catch (InterruptedException e1) {
					return;
				}
			}
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		logger.debug("Client delivery completed.");
	}

	@Override
	public boolean isConnected() {
		if (mqttClient != null) {
			return mqttClient.isConnected();
		} else {
			return false;
		}
	}

}
