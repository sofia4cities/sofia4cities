package com.indracompany.sofia2.config.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.indracompany.sofia2.config.model.OAuthAccessToken;

@Transactional
public interface OAuthAccessTokenRepository extends JpaRepository<OAuthAccessToken, String> {

	Collection<OAuthAccessToken> findByClientId(String clientId);

}
