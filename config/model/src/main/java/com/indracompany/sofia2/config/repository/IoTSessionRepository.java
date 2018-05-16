package com.indracompany.sofia2.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indracompany.sofia2.config.model.IoTSession;

public interface IoTSessionRepository extends JpaRepository<IoTSession, String> {

	IoTSession findBySessionKey(String sessionKey);

}
