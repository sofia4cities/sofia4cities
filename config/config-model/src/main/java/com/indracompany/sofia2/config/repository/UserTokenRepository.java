package com.indracompany.sofia2.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.model.UserToken;

public interface UserTokenRepository extends JpaRepository<UserToken, String>{
	
	public UserToken findByUserId(User userId);

}
