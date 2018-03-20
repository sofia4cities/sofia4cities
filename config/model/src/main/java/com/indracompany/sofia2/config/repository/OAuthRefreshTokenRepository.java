package com.indracompany.sofia2.config.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.indracompany.sofia2.config.model.OAuthRefreshToken;

@Transactional
public interface OAuthRefreshTokenRepository extends JpaRepository<OAuthRefreshToken, String> {

}
