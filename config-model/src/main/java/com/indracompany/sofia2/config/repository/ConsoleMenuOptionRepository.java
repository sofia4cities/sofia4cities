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
import com.indracompany.sofia2.config.model.ConsoleMenuOption;

public interface ConsoleMenuOptionRepository extends JpaRepository<ConsoleMenuOption,String>{

	ConsoleMenuOption findById(String id);
	List<ConsoleMenuOption> findByConsoleMenuId(ConsoleMenu consoleMenuId);
	List<ConsoleMenuOption> findByOption(String option);
	List<ConsoleMenuOption> findByOptionAndConsoleMenuId(String option,ConsoleMenu consoleMenuId);
}
