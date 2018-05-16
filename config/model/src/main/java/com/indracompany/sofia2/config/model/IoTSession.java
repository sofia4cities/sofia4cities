package com.indracompany.sofia2.config.model;

import java.time.ZonedDateTime;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;

import com.indracompany.sofia2.config.model.base.AuditableEntityWithUUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "IOT_SESSION")
@Configurable
@Cacheable(true)
public class IoTSession extends AuditableEntityWithUUID {

	@Column(name = "SESSION_KEY", unique = true)
	@NotNull
	@Getter
	@Setter
	private String sessionKey;

	@Column(name = "CLIENT_PLATFORM")
	@NotNull
	@Getter
	@Setter
	private String clientPlatform;

	@Column(name = "CLIENT_PLATFORM_ID")
	@NotNull
	@Getter
	@Setter
	private String clientPlatformID;

	@Column(name = "DEVICE")
	@NotNull
	@Getter
	@Setter
	private String device;
	@Column(name = "TOKEN")
	@NotNull
	@Getter
	@Setter
	private String token;
	@Column(name = "USER_ID")
	@NotNull
	@Getter
	@Setter
	private String userID;
	@Column(name = "USER_NAME")
	@NotNull
	@Getter
	@Setter
	private String userName;
	@Column(name = "EXPIRATION")
	@NotNull
	@Getter
	@Setter
	private long expiration;
	@Column(name = "LAST_ACCESS")
	@NotNull
	@Getter
	@Setter
	private ZonedDateTime lastAccess;

}
