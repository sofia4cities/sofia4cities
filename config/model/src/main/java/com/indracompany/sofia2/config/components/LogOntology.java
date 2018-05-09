package com.indracompany.sofia2.config.components;

import java.awt.geom.Point2D;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

@JsonIgnoreProperties
@JsonDeserialize(using = LogOntologyDeserializer.class)
@Data
public class LogOntology {

	private String device;
	private Point2D.Double location;
	private String extraOptions;
	private String level;
	private String message;
	private String status;
	private Date timestamp;

	public LogOntology(String device, Point2D.Double location, String extraOptions, String level, String message,
			String status, Date timestamp) {
		this.device = device;
		this.location = location;
		this.extraOptions = extraOptions;
		this.level = level;
		this.message = message;
		this.status = status;
		this.timestamp = timestamp;
	}

}
