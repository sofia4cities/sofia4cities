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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.indracompany.sofia2.config.model.KPContainerType;
import com.indracompany.sofia2.config.model.Note;
import com.indracompany.sofia2.config.repository.KPContainerTypeRepository;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Luis Miguel Gracia
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class KPContainerTypeIntegrationTest {
	
	@Autowired
	KPContainerTypeRepository repository;
	
	private boolean noData=false;
	
	@Before
	public void setUp() {
		List<KPContainerType> types = this.repository.findAll();
		if (types.isEmpty()) {
			log.info("No types en tabla.Adding...");
			noData=true;
			KPContainerType type=new KPContainerType();
			type.setId(1);
			type.setType("Python");
			repository.save(type);
			type=new KPContainerType();
			type.setId(2);
			type.setType("Java");
			repository.save(type);
			type=new KPContainerType();
			type.setId(3);
			type.setType("URL");
			repository.save(type);		
		}
	}
	
	@Test
	public void test1_Count() { 
		Assert.assertTrue(this.repository.count()==3);		
	}

	@Test
	public void test2_GetAll() {
		Assert.assertTrue(this.repository.findAll().size()==3);
	}


}
