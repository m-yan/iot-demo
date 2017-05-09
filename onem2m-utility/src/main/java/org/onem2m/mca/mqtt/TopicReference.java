package org.onem2m.mca.mqtt;

/**
 * oneM2M Mca binding MQTTの仕様に準拠したトピック名を返すユーティリティ。<br>
 * スタティックファクトリメソッドを使って、シングルトンで運用する。<br>
 * 
 */
public final class TopicReference {

	private static final OneM2MMcaProperties prop = OneM2MMcaProperties.getInstance();
	private static final TopicReference INSTANCE = new TopicReference();

	private final String topicForRequestFromCSE;
	private final String topicForResponseToCSE;
	private final String topicForRequestToCSE;
	private final String topicForResponseFromCSE;
	
	private TopicReference() {
		topicForRequestFromCSE = new StringBuilder().append("oneM2M/req/").append(prop.getInCseId()).append("/")
				.append(prop.getInAeId()).append("/json").toString();
		topicForResponseToCSE = new StringBuilder().append("oneM2M/resp/").append(prop.getInCseId()).append("/")
				.append(prop.getInAeId()).append("/json").toString();
		topicForRequestToCSE = new StringBuilder().append("oneM2M/req/").append(prop.getInAeId()).append("/")
				.append(prop.getInCseId()).append("/json").toString();
		topicForResponseFromCSE = new StringBuilder().append("oneM2M/resp/").append(prop.getInAeId()).append("/")
				.append(prop.getInCseId()).append("/json").toString();
	}

	
	/**
	 * @return	本クラスのインスタンス（シングルトン）
	 */
	public static TopicReference getInstance() {
		return INSTANCE;
	}

	
	/**
	 * @return	IN-CSE→IN-AEに送信されるMca Requestのトピック名。これを指定してMQTT subscribeする。
	 */
	public String getTopicForRequestFromCSE() {
		return topicForRequestFromCSE;
	}
	
	/**
	 * @return	IN-AE→IN-CSEに送信するMca Responseのトピック名。これを指定してMQTT publishする。
	 */
	public String getTopicForResponseToCSE() {
		return topicForResponseToCSE;
	}
	
	
	/**
	 * @return	IN-AE→IN-CSEに送信するMca Requestのトピック名。これを指定してMQTT publishする。
	 */
	public String getTopicForRequestToCSE() {
		return topicForRequestToCSE;
	}
	
	/**
	 * @return	IN-CSE→IN-AEに送信されるMca Responseのトピック名。これを指定してMQTT subscribeする。
	 */
	public String getTopicForResponseFromCSE() {
		return topicForResponseFromCSE;
	}
}
