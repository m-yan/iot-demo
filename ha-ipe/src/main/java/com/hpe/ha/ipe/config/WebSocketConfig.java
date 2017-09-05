package com.hpe.ha.ipe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.hpe.ha.ipe.util.ApplicationProperties;
import com.hpe.ha.ipe.web.ServerPushBroker;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	
	@Autowired
	ServerPushBroker serverPushBroker;
	
	@Autowired
	ApplicationProperties prop;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registory) {
		registory.addHandler(serverPushBroker, prop.getWsEnvironmentalInfo()).setAllowedOrigins("*");
		registory.addHandler(serverPushBroker, prop.getWsMonitoringMode()).setAllowedOrigins("*");
		registory.addHandler(serverPushBroker, prop.getWsEvents()).setAllowedOrigins("*");
	}

}
