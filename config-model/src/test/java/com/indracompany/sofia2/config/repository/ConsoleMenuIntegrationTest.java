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

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Javier Gomez-Cornejo
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class ConsoleMenuIntegrationTest {

	@Autowired 
	ConsoleMenuRepository repository;


	@Before
	public void setUp() {

		List<ConsoleMenu> menus=this.repository.findAll();

		if(menus.isEmpty())
		{
			log.info("No menu elements found...adding");
			ConsoleMenu menu=new ConsoleMenu();
			menu.setId("menu_category_ontologias_label");
			menu.setName("menu_category_ontologias_label");
			this.repository.save(menu);

		}
	}

	@Test
	public void test_findById()
	{
		ConsoleMenu menu=this.repository.findAll().get(0);
		Assert.assertTrue(this.repository.findById(menu.getId())!=null);
	}


}
