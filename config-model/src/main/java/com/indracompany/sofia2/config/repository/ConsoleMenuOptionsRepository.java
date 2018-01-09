/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/
package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indracompany.sofia2.config.model.ConsoleMenu;
import com.indracompany.sofia2.config.model.ConsoleMenuOptions;

public interface ConsoleMenuOptionsRepository extends JpaRepository<ConsoleMenuOptions,String>{
	
	ConsoleMenuOptions findById(String id);
	List<ConsoleMenuOptions> findByConsoleMenuId(ConsoleMenu consoleMenuId);
	List<ConsoleMenuOptions> findByOption(String option);
	List<ConsoleMenuOptions> findByOptionAndConsoleMenuId(String option,ConsoleMenu consoleMenuId);
}
