package org.onem2m.mca.mqtt;

import org.onem2m.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * oneM2MのRequestPrimitiveとResponsePrimitiveの共通部分を抽出した抽象オブジェクト
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class Primitive {
	
	private static final Logger logger = LoggerFactory.getLogger(Primitive.class);
	protected static final ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
	
	// リクエストのID
	@JsonProperty("rqi")
	@NonNull
	protected String requestId;
		
	// メッセージ本文
	@JsonProperty("pc")
	protected Object content;
	
	
	/**
	 * @return	JSONシリアライズされたインスタンス
	 */
	public String toJson() {
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
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
			return Resource.valueOf(jsonPrimitiveContent, type);
		} catch (JsonProcessingException e) {
			logger.warn("Failed to cast to the specified type.");
		}
		return null;
	}
	
	
	// 各種DataType
	
	public static enum Operation {
		CREATE(1), RETRIEVE(2), UPDATE(3), DELETE(4), NOTIFY(5);

		@Getter
		private final Integer value;

		Operation(final Integer value) {
			this.value = value;
		}
	}

	public static enum ResultContent {
		NOTHING(0),
		ATTRIBUTES(1),
		HIERARCHICAL_ADDRESS(2),
		HIERARCHICAL_ADDRESS_AND_ATTRIBUTES(3),
		ATTRIBUTES_AND_CHILD_RESOURCES(4),
		ATTRIBUTES_AND_CHILD_RESOURCE_REFERENCES(5),
		CHILD_RESOURCE_REFERENCES(6),
		ORIGINAL_RESOURCE(7),
		CHILD_RESOURCES(8);

		@Getter
		private final Integer value;

		ResultContent(final Integer value) {
			this.value = value;
		}
	}
	
	public static enum ResponseStatus {
		OK(2000), CREATED(2001), BAD_REQUEST(4000), INTERNAL_SERVER_ERROR(5000);

		@Getter
		private final int statusCode;

		ResponseStatus(final int statusCode) {
			this.statusCode = statusCode;
		}
	}
	
	public static enum FilterUsage {
		DISCOVERY_CRITERIA(1),
		CONDITIONAL_RETRIEVAL(2),
		IPE_ON_DEMAND_DISCOVERY(3);

		@Getter
		private final Integer value;

		FilterUsage(final Integer value) {
			this.value = value;
		}
	}
}
