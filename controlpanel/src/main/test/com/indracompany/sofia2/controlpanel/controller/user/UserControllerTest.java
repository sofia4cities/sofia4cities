package com.indracompany.sofia2.controlpanel.controller.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Matchers;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.mockito.MockitoAnnotations;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.indracompany.sofia2.config.model.RoleType;
import com.indracompany.sofia2.config.model.User;
import com.indracompany.sofia2.controlpanel.Sofia2ControlPanelWebApplication;
import com.indracompany.sofia2.service.user.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Sofia2ControlPanelWebApplication.class)
public class UserControllerTest {

	private MvcResult mvcResult;
	private MockMvc mockMvc;
	@Autowired
	WebApplicationContext context;
	@Autowired
	UserService userService;
	
	@Before
    public void initTests() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

	@Test
	public void testShowUser() throws Exception
	{
		User user=new User();
		user.setActive(true);
		user.setEmail("admin@gmail.com");
		RoleType role= new RoleType();
		role.setName("ROLE_ADMINISTRATOR");
		user.setPassword("somePass");
		user.setCreatedAt(new java.util.Date());
		user.setRoleTypeId(role);
		user.setUserId("admin");
		user.setEmail("some@email.com");
		user.setFullName("Admin s4c");
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
        Authentication authentication = new
                UsernamePasswordAuthenticationToken("admin", "admin", grantedAuthorities);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		//Mockito.when(this.userService.getUser(Matchers.anyString())).thenReturn(user);
		SecurityContextHolder.setContext(securityContext);
		mockMvc.perform(post("/users/create").param("password", user.getFullName())
				.param("fullName",user.getFullName())
				.param("password",user.getPassword())
				.param("userId", user.getUserId())
				.param("email", user.getEmail()).param("createdAt", user.getCreatedAt().toString())
				.param("roleTypeId", user.getRoleTypeId().getName())).andExpect(status().isOk());
	
	}
}
