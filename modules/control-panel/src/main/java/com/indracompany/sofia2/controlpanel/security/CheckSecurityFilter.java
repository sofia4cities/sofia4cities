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
package com.indracompany.sofia2.controlpanel.security;


import java.io.IOException;
import java.util.Arrays;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.web.util.UrlPathHelper;

import com.indracompany.sofia2.config.services.oauth.JWTService;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CheckSecurityFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CheckSecurityFilter.class);

    private static final boolean CONDITION = true;
    
    private TokenExtractor tokenExtractor = new BearerTokenExtractor();
    
	@Autowired(required=false)
	private JWTService jwtService;
	
	String[] presets = {"management"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("Initiating CheckSecurityFilter >> ");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException,
            ServletException {
    	Authentication info = null;
    	String firstResult = null;
    	
        	HttpServletRequest req = (HttpServletRequest) request;
        	CheckSecurityWrapper requestWrapper = new CheckSecurityWrapper(req);
        	
        	String path = new UrlPathHelper().getPathWithinApplication(req);
        	String[] states = path.split("/");
        	if (states.length>0) {
        		String firstPath = states[1];
        		firstResult = Arrays.stream(presets)
            	                           .filter(x -> x.equalsIgnoreCase(firstPath))
            	                           .findFirst()
            	                           .orElse(null);
        	}
        	
            Authentication authentication = tokenExtractor.extract(req);
            
            if (authentication ==null && firstResult==null) {
            	chain.doFilter(requestWrapper, response); // Goes to default servlet.
            }
            
            else if (authentication ==null && firstResult!=null) {
            	((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        		((HttpServletResponse) response).setContentType("application/json;charset=UTF-8");
        		((HttpServletResponse) response).getWriter().write("{\"error\": \"Path needs to be Authenticated, but no Authentication Header was found\"}");
        		((HttpServletResponse) response).getWriter().flush();
        		((HttpServletResponse) response).getWriter().close();
            }
            
            else if (authentication !=null && firstResult!=null)
            {
            	info = getInfo(authentication,req);
            	
            	if (info==null  ) {

            		((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            		((HttpServletResponse) response).setContentType("application/json;charset=UTF-8");
            		((HttpServletResponse) response).getWriter().write("{\"error\": \"Incorrect or Expired Authorization Header, Status is UnAuthorized\"}");
            		((HttpServletResponse) response).getWriter().flush();
            		((HttpServletResponse) response).getWriter().close();
            	}
            	
            	else {
            		chain.doFilter(requestWrapper, response); // Goes to default servlet.
            		logout(req);

            	}
            }
            
            else if (authentication !=null && firstResult==null) {
            	((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        		((HttpServletResponse) response).setContentType("application/json;charset=UTF-8");
        		((HttpServletResponse) response).getWriter().write("{\"error\": \"Incorrect State, Path not need to be Authenticated but Authorization Header was Found\"}");
        		((HttpServletResponse) response).getWriter().flush();
        		((HttpServletResponse) response).getWriter().close();
            }
            	
    }

    
    Authentication getInfo(Authentication authentication,HttpServletRequest req) {
    	OAuth2Authentication info = null; 
    	try {
         	
         	if (authentication!=null) {
         		 info = (OAuth2Authentication)jwtService.getAuthentication((String)authentication.getPrincipal());
         		 UsernamePasswordAuthenticationToken authRequest = (UsernamePasswordAuthenticationToken)info.getUserAuthentication();
         		 
         	    SecurityContext securityContext = SecurityContextHolder.getContext();
         	    securityContext.setAuthentication(authRequest);

         	    // Create a new session and add the security context.
         	    HttpSession session = req.getSession(true);
         	    session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
         		
         		return info.getUserAuthentication();
         	}
         	else {
         		logger.error("Authentication is not correct");
         	}
         	
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				
			}
    	return null;
    }
	
    
    void logout(HttpServletRequest req) {

    	try {
         	    HttpSession session = req.getSession();
         	    session.removeAttribute("SPRING_SECURITY_CONTEXT");
         	    
         	    logger.info("Session Disconnected");
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