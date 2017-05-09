package org.onem2m.mca.mqtt;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;

/**
 * oneM2Mに関する設定値を取り扱うクラス。<br>
 * スタティックファクトリメソッドを使って、シングルトンで運用する。<br>
 * 可視性をデフォルトにし、oneM2Mの詳細をパッケージ外の利用者が意識しなくて済むにようにする。<br>
 * 
 */
@Getter
final class OneM2MMcaProperties {

	private static final OneM2MMcaProperties INSTANCE = new OneM2MMcaProperties();
	// 各プロパティの内容はonem2m-mca.conf参照
	private String inCseId;
	private String inAeId;

	private OneM2MMcaProperties() {
		final Config config = ConfigFactory.load("onem2m-mca");

		inCseId = config.getString("IN-CSE-ID");
		inAeId = config.getString("IN-AE-ID");
	}

	/**
	 * @return	本クラスのインスタンス（シングルトン）を返す
	 */
	public static OneM2MMcaProperties getInstance() {
		return INSTANCE;
	}

}
