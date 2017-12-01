package org.onem2m.mca.mqtt;

/**
 * oneM2M Mca binding MQTTの仕様に準拠したトピック名を返すユーティリティ。<br>
 * 
 */
public final class TopicReference {

	private TopicReference() {}
	
	/**
	 * @param inAeId	IN-CSEから払い出されたIN-AE-ID
	 * @return	IN-CSE→IN-AEに送信されるMca Requestのトピック名。これを指定してMQTT subscribeする。
	 */
	public static String getTopicForRequest(String from, String to) {
		return new StringBuilder().append("oneM2M/req/").append(from).append("/")
				.append(to).append("/json").toString();
	}
	
	/**
	 * @param inAeId	IN-CSEから払い出されたIN-AE-ID
	 * @return	IN-AE→IN-CSEに送信するMca Responseのトピック名。これを指定してMQTT publishする。
	 */
	public static String getTopicForResponse(String from, String to) {
		return  new StringBuilder().append("oneM2M/resp/").append(to).append("/")
				.append(from).append("/json").toString();
	}
	
}
