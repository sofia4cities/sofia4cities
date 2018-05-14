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
package com.indracompany.sofia2.controlpanel.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

public class URlPriorFilter extends GenericFilterBean {
	private final String LOGIN = "login";
	private final String PAGE_403 = "403";
	private final String BLOCK_PRIOR_LOGIN = "block_prior_login";
	private final String URL_PRIOR_LOGIN = "url_prior_login";
	private final String[] LIST_SAVE_URL = { "dashboards/view/" };

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String uri = request.getRequestURI();
		String block = (String) request.getSession().getAttribute(BLOCK_PRIOR_LOGIN);
		String url_prior = (String) request.getSession().getAttribute(URL_PRIOR_LOGIN);

		// save prior url
		if (block == null && url_prior != null && (uri.indexOf(LOGIN) >= 0 || uri.indexOf(PAGE_403) >= 0)) {
			request.getSession().setAttribute(BLOCK_PRIOR_LOGIN, url_prior);
		}

		if (null != uri && uri.indexOf(LOGIN) < 0 && uri.indexOf(PAGE_403) < 0 && findOnList(uri)) {
			request.getSession().setAttribute(URL_PRIOR_LOGIN, request.getRequestURI());
		}

		chain.doFilter(request, response);
	}

	private boolean findOnList(String uri) {

		if (uri != null) {
			for (String str : LIST_SAVE_URL) {
				if (uri.indexOf(str) >= 0) {
					return true;
				}
			}
		}
		return false;
	}
}
