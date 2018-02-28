package com.indracompany.sofia2.plugin.iotbroker.security;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class IoTSession {
	private String sessionKey;
	private String clientPlatform;
	private String clientPlatformInstance;
	private String token;
	private String userID;
	private String userName;
	private ZonedDateTime lastAccess;
	private long expiration;
}
