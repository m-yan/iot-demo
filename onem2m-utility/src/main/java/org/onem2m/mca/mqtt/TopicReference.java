package org.onem2m.mca.mqtt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * oneM2M Mca binding MQTTの仕様に準拠したトピック名を返すユーティリティ。<br>
 * 
 */
public final class TopicReference {

	private TopicReference() {
	}

	/**
	 * @param originator
	 *            リクエストの送信元
	 * @param receiver
	 *            リクエストの送信先
	 * 
	 * @return fromからtoに向けてリクエストを送信する際に利用するoneM2M準拠のMQTTのトピック名。
	 */
	public static String generateTopicForRequest(String originator, String receiver) {
		return new StringBuilder().append("oneM2M/req/").append(originator).append("/").append(receiver).append("/json").toString();
	}

	/**
	 * @param originator
	 *            レスポンスの送信元
	 * @param receiver
	 *            レスポンスの送信先
	 * 
	 * @return fromからtoに向けてレスポンスを送信する際に利用するoneM2M準拠のMQTTのトピック名。
	 */
	public static String generateTopicForResponse(String originator, String receiver) {
		return new StringBuilder().append("oneM2M/resp/").append(originator).append("/").append(receiver).append("/json").toString();
	}

	/**
	 * @param topicForRequest
	 *            リクエストを送信する際に利用するoneM2M準拠のMQTTのトピック名。
	 * 
	 * @return topicForRequestで送信されたリクエストに対するレスポンスを送信する際に利用するoneM2M準拠のMQTTのトピック名。
	 */
	public static String generateTopicForResponse(String topicForRequest) {
		String regex = "oneM2M/req/(.*)/(.*)/json";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(topicForRequest);

		if (matcher.find()) {
			String originator = matcher.group(1);
			String receiver = matcher.group(2);
			return new StringBuilder().append("oneM2M/resp/").append(receiver).append("/").append(originator).append("/json").toString();
		} else {
			return null;
		}
	}

}
