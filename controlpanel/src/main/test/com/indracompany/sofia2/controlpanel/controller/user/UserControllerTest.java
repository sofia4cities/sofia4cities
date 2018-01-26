package com.indracompany.sofia2.controlpanel.controller.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Matchers;
import org.mockito.Mock;
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
import com.indracompany.sofia2.config.repository.UserRepository;
import com.indracompany.sofia2.controlpanel.Sofia2ControlPanelWebApplication;
import com.indracompany.sofia2.service.user.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Sofia2ControlPanelWebApplication.class)
public class UserControllerTest {


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
	public void testCreateUser() throws Exception
	{
		User user= this.mockUser();
		//mock userService
		userService= Mockito.mock(UserService.class);
		
//		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
//        Authentication authentication = new
//                UsernamePasswordAuthenticationToken("admin", "admin", grantedAuthorities);
//		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
//		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
//		SecurityContextHolder.setContext(securityContext);
		
		SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy");
		
		mockMvc.perform(post("/users/create")
				.param("fullName",user.getFullName())
				.param("active", "true")
				.param("password",user.getPassword())
				.param("userId", user.getUserId())
				.param("email", user.getEmail())
				.param("dateCreated", formatter.format(user.getDateCreated()))
				.param("roleTypeId.name", user.getRoleTypeId().getName())).andDo(print())
		.andExpect(status().is3xxRedirection());
	
	}
	
	public User mockUser()
	{
		User user=new User();
		user.setActive(true);
		user.setEmail("admin@gmail.com");
		RoleType role= new RoleType();
		role.setName("ROLE_ADMINISTRATOR");
		user.setRoleTypeId(role);
		user.setPassword("somePass");
		user.setDateCreated(new java.util.Date());
		user.setRoleTypeId(role);
		user.setUserId("admin");
		user.setEmail("some@email.com");
		user.setFullName("Admin s4c");
		return user;
		
	}
}
