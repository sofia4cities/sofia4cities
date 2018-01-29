/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
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
package com.indracompany.sofia2.service.user;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.config.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
	
	@Mock
	UserRepository userRepository;
	@InjectMocks
	private UserService userService;
	
	@Test
	public void testUserExist()
	{
		User user= Mockito.mock(User.class);
		userRepository= Mockito.mock(UserRepository.class);
		Mockito.when(userRepository.findByUserId(Matchers.anyString())).thenReturn(user);
		boolean result=userService.userExists(user);
		assertEquals(true,result);
	}

}
