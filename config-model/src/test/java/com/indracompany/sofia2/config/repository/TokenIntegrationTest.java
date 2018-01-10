/*******************************************************************************
 * © Indra Sistemas, S.A.
 * 2013 - 2018  SPAIN
 *
 * All rights reserved
 ******************************************************************************/


package com.indracompany.sofia2.config.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indracompany.sofia2.config.model.ClientPlatform;
import com.indracompany.sofia2.config.model.DataModel;
import com.indracompany.sofia2.config.model.Token;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Javier Gomez-Cornejo
 */
@RunWith(SpringRunner.class)

@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class TokenIntegrationTest {

	@Autowired
	TokenRepository repository;
	@Autowired
	ClientPlatformRepository cpRepository;

	@Before
	public void setUp() {
		List<Token> tokens= this.repository.findAll();
		if (tokens.isEmpty()) {
			log.info("No Tokens, adding ...");

			ClientPlatform client= new ClientPlatform();
			client.setId("06be1962-aa27-429c-960c-d8a324eef6d4");
			Set<Token> hashSetTokens=new HashSet<Token>();

			Token token=new Token();
			token.setClientPlatformId(client);
			token.setToken("Token 1");
			token.setActive(new Integer(4));
			hashSetTokens.add(token);
			client.setTokens(hashSetTokens);


			repository.save(token);

		}
	}

	@Test
	public void test_findByClientPlatformId() { 
		Token token=this.repository.findAll().get(0);
		log.info(token.getClientPlatformId().getUserId());
		Assert.assertTrue(this.repository.findByClientPlatformId(token.getClientPlatformId()).size()>0);		
	}

}
