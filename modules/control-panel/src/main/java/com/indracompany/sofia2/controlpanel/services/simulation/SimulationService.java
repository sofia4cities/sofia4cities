package com.indracompany.sofia2.controlpanel.services.simulation;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface SimulationService {

	void simulate(String json) throws JsonProcessingException, IOException;
}
