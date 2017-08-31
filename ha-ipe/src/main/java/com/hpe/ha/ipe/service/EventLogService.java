package com.hpe.ha.ipe.service;

import org.onem2m.resource.ContentInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventLogService {
	
	@Autowired
	private UIoTClientService uiotClient;
	
	public void writeLog(String message) {
		ContentInstance eventLog = new ContentInstance(message);
		eventLog.setContentInfo("text/plain:0");
		uiotClient.sendRequest("/HPE_IoT/hgw01/default/", eventLog.toJson());
	}
}
