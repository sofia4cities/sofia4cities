package com.indracompany.sofia2.digitaltwin.config.controller;

import java.io.InputStreamReader;
import java.io.Reader;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/config")
public class ConfigRestController {

	@RequestMapping(value = "/getWot", method = RequestMethod.GET)
	public Response getWot() {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			final int bufferSize = 1024;
			final char[] buffer = new char[bufferSize];
			final StringBuilder out = new StringBuilder();
			Reader in = new InputStreamReader(classLoader.getResource("static/json/wot.json").openStream());
			for (;;) {
				int rsz = in.read(buffer, 0, buffer.length);
				if (rsz < 0)
					break;
				out.append(buffer, 0, rsz);
			}
			return Response.ok(out.toString()).build();

		} catch (Exception e) {
			return Response.status(Status.FORBIDDEN).build();
		}
	}

}
