package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indracompany.sofia2.config.model.TwitterListener;
import com.indracompany.sofia2.config.model.User;

public interface TwitterListenerRepository extends JpaRepository<TwitterListener,String>{
	
	TwitterListener findById(String id);
	

}
