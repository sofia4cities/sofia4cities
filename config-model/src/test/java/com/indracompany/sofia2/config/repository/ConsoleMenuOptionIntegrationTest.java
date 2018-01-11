/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/

package com.indracompany.sofia2.config.repository;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.config.model.ConsoleMenu;
import com.indracompany.sofia2.config.model.ConsoleMenuOption;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Javier Gomez-Cornejo
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j

public class ConsoleMenuOptionIntegrationTest {

	@Autowired 
	ConsoleMenuOptionRepository repository;
	@Autowired 
	ConsoleMenuRepository cmrepository;

	@Before
	public void setUp()
	{

		List<ConsoleMenuOption> options=this.repository.findAll();
		if(options.isEmpty())
		{
			log.info("No menu options found...adding");
			ConsoleMenuOption option=new ConsoleMenuOption();
			option.setId("menu_item_ontologias_listar_label");
			option.setOption("menu_item_ontologias_listar_label");
			ConsoleMenu menu=this.cmrepository.findById("menu_category_ontologias_label");
			if(menu==null){
				menu=new ConsoleMenu();
				menu.setId("menu_category_ontologias_label");
				menu.setName("menu_category_ontologias_label");
				this.cmrepository.save(menu);
			}
			option.setConsoleMenuId(menu);
			this.repository.save(option);



		}

	}

	@Test
	public void test_findByConsoleMenuId()
	{
		ConsoleMenuOption option=this.repository.findAll().get(0);
		Assert.assertTrue(this.repository.findByConsoleMenuId(option.getConsoleMenuId()).size()>0);

	}


}
