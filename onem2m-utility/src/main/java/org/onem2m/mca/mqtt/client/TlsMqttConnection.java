package org.onem2m.mca.mqtt.client;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * MqttConnectionの実装クラス。 TLS接続用。
 * 接続オプションのみ、スーパークラスと異なる。
 */
class TlsMqttConnection extends PlainMqttConnection {
	
	TlsMqttConnection(String brokerUrl) {
		super(brokerUrl);

		try {
			char[] keyPass = "testtest".toCharArray();
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(this.getClass().getResourceAsStream("/crt.jks"), keyPass);
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(keyStore, keyPass);
			
			KeyStore trustStore = KeyStore.getInstance("JKS");
			trustStore.load(this.getClass().getResourceAsStream("/crt.jks"), keyPass);
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(trustStore);

			SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
			connOpt.setSocketFactory(sslContext.getSocketFactory());
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | CertificateException
				| IOException | UnrecoverableKeyException e) {
			logger.error("TLS socket generation failed.");
		}
	}

}
