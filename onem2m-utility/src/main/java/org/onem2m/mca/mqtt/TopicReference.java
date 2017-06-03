package org.onem2m.mca.mqtt;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * oneM2M Mca binding MQTTの仕様に準拠したトピック名を返すユーティリティ。<br>
 * 
 */
public final class TopicReference {

	private static final Config config = ConfigFactory.load("onem2m");
	private TopicReference() {}
	
	/**
	 * @param inAeId	IN-CSEから払い出されたIN-AE-ID
	 * @return	IN-CSE→IN-AEに送信されるMca Requestのトピック名。これを指定してMQTT subscribeする。
	 */
	public static String getTopicForRequestFromCseTo(String inAeId) {
		return new StringBuilder().append("oneM2M/req/").append(config.getString("IN-CSE-ID")).append("/")
				.append(inAeId).append("/json").toString();
	}
	
	/**
	 * @param inAeId	IN-CSEから払い出されたIN-AE-ID
	 * @return	IN-AE→IN-CSEに送信するMca Responseのトピック名。これを指定してMQTT publishする。
	 */
	public static String getTopicForResponseToCseFrom(String inAeId) {
		return  new StringBuilder().append("oneM2M/resp/").append(config.getString("IN-CSE-ID")).append("/")
				.append(inAeId).append("/json").toString();
	}
	
	/**
	 * @param inAeId	IN-CSEから払い出されたIN-AE-ID
	 * @return	IN-AE→IN-CSEに送信するMca Requestのトピック名。これを指定してMQTT publishする。
	 */
	public static String getTopicForRequestToCseFrom(String inAeId) {
		return new StringBuilder().append("oneM2M/req/").append(inAeId).append("/")
				.append(config.getString("IN-CSE-ID")).append("/json").toString();
	}
	
	/**
	 * @param inAeId	IN-CSEから払い出されたIN-AE-ID
	 * @return	IN-CSE→IN-AEに送信されるMca Responseのトピック名。これを指定してMQTT subscribeする。
	 */
	public static String getTopicForResponseFromCseTo(String inAeId) {
		return new StringBuilder().append("oneM2M/resp/").append(inAeId).append("/")
				.append(config.getString("IN-CSE-ID")).append("/json").toString();
	}
	
	
	public static String getTopicForAERegistraionRequest(String credentialID) {
		return new StringBuilder().append("oneM2M/reg_req/").append(credentialID).append("/")
				.append(config.getString("IN-CSE-ID")).append("/json").toString();
	}
	
	public static String getTopicForAERegistraionResponse(String credentialID) {
		return new StringBuilder().append("oneM2M/reg_resp/").append(credentialID).append("/")
				.append(config.getString("IN-CSE-ID")).append("/json").toString();
	}
}
