package com.indracompany.sofia2.controlpanel.config;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;

import com.indracompany.sofia2.config.services.oauth.JWTService;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CheckSecurityFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CheckSecurityFilter.class);

    private static final boolean CONDITION = true;
    
    private TokenExtractor tokenExtractor = new BearerTokenExtractor();
    
   
	@Autowired(required=false)
	private JWTService jwtService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("Initiating CheckSecurityFilter >> ");
    }
    
    

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException,
            ServletException {
    	Authentication info = null;
    	
        if (CONDITION == true) {
          
        	HttpServletRequest req = (HttpServletRequest) request;
        	CheckSecurityWrapper requestWrapper = new CheckSecurityWrapper(req);
            
            Authentication authentication = tokenExtractor.extract(req);
            
            if (authentication ==null) {
            	chain.doFilter(requestWrapper, response); // Goes to default servlet.
            }
            
            else {
            	info = getInfo(authentication,req);
            	
            	if (info==null  ) {

            		((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            		((HttpServletResponse) response).setContentType("application/json;charset=UTF-8");
            		((HttpServletResponse) response).getWriter().write("{\"error\": \"Token Corrupted\"}");
            		((HttpServletResponse) response).getWriter().flush();
            		((HttpServletResponse) response).getWriter().close();
            	}
            	
            	else 
            		chain.doFilter(requestWrapper, response); // Goes to default servlet.
            		logout(req);
            		
            	
            	}
            	
            }
        
        else {
        	chain.doFilter(request, response);
            //((HttpServletResponse) response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
          
    }
    
    
    Authentication getInfo(Authentication authentication,HttpServletRequest req) {
    	OAuth2Authentication info = null; 
    	try {
         	
         	if (authentication!=null) {
         		 info = (OAuth2Authentication)jwtService.getAuthentication((String)authentication.getPrincipal());
         		 // Authenticate the user
         		 UsernamePasswordAuthenticationToken authRequest = (UsernamePasswordAuthenticationToken)info.getUserAuthentication();
         		 
         	   // Authentication tt = authenticationManager.authenticate(authRequest);
         	    SecurityContext securityContext = SecurityContextHolder.getContext();
         	    securityContext.setAuthentication(authRequest);

         	    // Create a new session and add the security context.
         	    HttpSession session = req.getSession(true);
         	    session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
         		
         		return info.getUserAuthentication();
				
         	}
         	
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				
			}
    	return null;
    }
	
    
    void logout(HttpServletRequest req) {

    	try {
         	

         		 
         	    // Create a new session and add the security context.
         	    HttpSession session = req.getSession(true);
         	    session.removeAttribute("SPRING_SECURITY_CONTEXT");
         		

				
 
         	
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				
			}
    
    }

    @Override
    public void destroy() {
        logger.debug("CheckSecurityFilter WebFilter >> ");
    }



	public TokenExtractor getTokenExtractor() {
		return tokenExtractor;
	}



	public void setTokenExtractor(TokenExtractor tokenExtractor) {
		this.tokenExtractor = tokenExtractor;
	}



    
    
}