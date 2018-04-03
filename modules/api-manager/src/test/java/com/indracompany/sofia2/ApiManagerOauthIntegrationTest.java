/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indracompany.sofia2;


import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiManagerApplication.class,webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApiManagerOauthIntegrationTest {

    @Autowired
    private WebApplicationContext wac;
    
    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;
  
    private static final String CLIENT_ID = "sofia2_s4c";
    private static final String CLIENT_SECRET = "sofia2_s4c";

    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";
    

    @Before
    public void setup() {
    	 this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
    }

    private String obtainAccessToken(String username, String password) throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", username);
        params.add("password", password);
        
        byte[] enconded= Base64.encodeBase64((CLIENT_ID+":"+CLIENT_SECRET).getBytes());

        // @formatter:off

        ResultActions result = mockMvc.perform(post("/oauth/token")
                               .params(params)
                               .header("Authorization", "Basic " +  new String(enconded))
                               .accept(CONTENT_TYPE)).andDo(print())
                               .andExpect(status().isOk())
                               .andExpect(content().contentType(CONTENT_TYPE)); 
        // @formatter:on

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }
    
    private String obtainRefreshToken(String username, String password) throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", username);
        params.add("password", password);
        
        byte[] enconded= Base64.encodeBase64((CLIENT_ID+":"+CLIENT_SECRET).getBytes());

        // @formatter:off

        ResultActions result = mockMvc.perform(post("/oauth/token")
                               .params(params)
                               .header("Authorization", "Basic " +  new String(enconded))
                               .accept(CONTENT_TYPE)).andDo(print())
                               .andExpect(status().isOk())
                               .andExpect(content().contentType(CONTENT_TYPE));
        // @formatter:on

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("refresh_token").toString();
    }
    
  
    
    private String obtainRefreshTokenS2(String token,String username, String password) throws Exception {
  
    	byte[] enconded= Base64.encodeBase64((username+":"+password).getBytes());
        // @formatter:off
        ResultActions result = mockMvc.perform(post("/sofia2-oauth/renewToken")
        						.content(token)
        						 .header("Authorization", "Basic " +  new String(enconded))
                               .accept(CONTENT_TYPE)).andDo(print())
                               .andExpect(status().isOk())
                               .andExpect(content().contentType(CONTENT_TYPE));
        // @formatter:on

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("token").toString();
    }
    
    @Test
    public void testGetToken() throws Exception {
    	final String accessToken = obtainAccessToken("administrator", "changeIt!");
    	System.out.println("testGetToken -> token:" + accessToken);
  
    	assertTrue(accessToken.length()>0);
       
    }
    
    @Test
    public void testGetRefreshDefaultToken() throws Exception {
    	final String accessToken = obtainRefreshToken("administrator", "changeIt!");
    	System.out.println("testGetRefreshDefaultToken -> token:" + accessToken);
  
    	assertTrue(accessToken.length()>0);
       
    }
    
    @Test
    public void testGetRefreshToken() throws Exception {
    	final String accessToken = obtainAccessToken("administrator", "changeIt!");
    	System.out.println("testGetRefreshTokenS2 -> token:" + accessToken);
  
    	final String refreshToken = obtainRefreshTokenS2(accessToken,"administrator", "changeIt!");
    	
    	assertTrue(refreshToken.length()>0);
       
    }

}