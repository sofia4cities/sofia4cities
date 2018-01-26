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
