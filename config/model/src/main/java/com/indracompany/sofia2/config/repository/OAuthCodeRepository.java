package com.indracompany.sofia2.config.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.indracompany.sofia2.config.model.OAuthCode;

@Transactional
public interface OAuthCodeRepository extends JpaRepository<OAuthCode, String> {

}
