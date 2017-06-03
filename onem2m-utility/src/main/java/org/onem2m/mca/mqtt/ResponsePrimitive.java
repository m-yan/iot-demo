package org.onem2m.mca.mqtt;

import java.io.IOException;

import org.onem2m.mca.datatype.URIList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * oneM2M ResponsePrimitiveを扱うオブジェクト。<br>
 * 
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ResponsePrimitive extends Primitive {
	
	private static final Logger logger = LoggerFactory.getLogger(ResponsePrimitive.class);
	
	// リソース操作の結果を表すコード。（HTTP StatusCodeに類似） 必須
	@JsonProperty("rsc")
	private int responseStatusCode;
	
	public ResponsePrimitive(ResponseStatus responseStatus, RequestPrimitive request) {
		this.requestId = request.getRequestId();
		this.responseStatusCode = responseStatus.getStatusCode();
		this.content = null;
	}

	/**
	 * JSONシリアライズされたResponsePrimitiveをparseしてインスタンスを生成するスタティックファクトリメソッド
	 * 
	 * @param jsonResponse
	 *            JSONシリアライズされたResponse
	 * @return jsonResponseをparseして得たインスタンスを返す。
	 *         jsonResponseが不正などでparseに失敗した場合はnullを返す。
	 */
	public static ResponsePrimitive valueOf(String jsonResponse) {
		try {
			return mapper.readValue(jsonResponse, ResponsePrimitive.class);
		} catch (JsonParseException e) {
			logger.warn("Received JSON format is invalid.");
			return null;
		} catch (JsonMappingException e) {
			logger.warn("Received Resource does not conform to the terms of oneM2M.");
			logger.warn(e.getMessage());
			return null;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	@JsonIgnore
	public URIList getContentCastedByURIList() {
		try {
			String jsonPrimitiveContent = mapper.writeValueAsString(this.content);
			return mapper.readValue(jsonPrimitiveContent, URIList.class);
		} catch (IOException e) {
			logger.warn("Failed to cast to the specified type.");
		}
		return null;
	}
	
}
