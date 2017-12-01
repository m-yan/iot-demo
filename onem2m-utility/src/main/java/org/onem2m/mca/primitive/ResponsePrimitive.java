package org.onem2m.mca.primitive;

import java.io.IOException;
import java.util.Map;

import org.onem2m.mca.complexdatatype.URIList;
import org.onem2m.resource.Resource;
import org.onem2m.resource.Resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * oneM2M ResponsePrimitiveを扱うオブジェクト。<br>
 * 
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ResponsePrimitive extends Primitive {
	
	private static final Logger logger = LoggerFactory.getLogger(ResponsePrimitive.class);
	
	// リソース操作の結果を表すコード。（HTTP StatusCodeに類似） 必須
	@JsonProperty("rsc")
	@NonNull
	private Integer responseStatusCode;

	// リクエストのID
	@JsonProperty("rqi")
	@NonNull
	private String requestId;
	
	// メッセージ本文
	@JsonProperty("pc")
	private Object content;
	
	public ResponsePrimitive(@NonNull ResponseStatus responseStatus, @NonNull RequestPrimitive request) {
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
	public static ResponsePrimitive parse(String jsonResponse) {
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
	
	
	/**
	 * Contentに指定されたリソースのタイプを返す。該当するものがない場合はnullを返す。
	 * @return	Contentに指定されたリソースのタイプ
	 */
	@JsonIgnore
	public ResourceType getContentResourceType() {
		@SuppressWarnings("unchecked")
		Map<String, Object> temp = (Map<String, Object>) this.content;
		
		ResourceType[] types = ResourceType.values();
		for (ResourceType type : types) {
			if (temp.containsKey(type.getShortname())) {
				return type;
			}
		}
		return null;
	}
	
	/**
	 * PrimitiveContent（メッセージ本文）を指定の型に変換して返す
	 * @param type		変換後の型
	 * @return			typeに指定された型に変換されたPrimitiveContent. 変換に失敗した場合はnullを返す
	 * 
	 */
	@JsonIgnore
	public <T extends Resource> T getContentCastedBy(Class<T> type) {
		try {
			String jsonPrimitiveContent = mapper.writeValueAsString(this.content);
			return Resource.parse(jsonPrimitiveContent, type);
		} catch (JsonProcessingException e) {
			logger.warn("Failed to cast to the specified type.");
		}
		return null;
	}
	
	/**
	 * PrimitiveContent（メッセージ本文）をURIList型に変換して返す
	 * @return			URIList型に変換されたPrimitiveContent. 変換に失敗した場合はnullを返す
	 * 
	 */
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
