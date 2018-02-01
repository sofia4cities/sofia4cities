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
package com.indracompany.sofia2.api.rule;
import javax.servlet.http.HttpServletRequest;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;
import org.springframework.stereotype.Component;

@Component
@Rule
public class SuspiciousRequestRule {

    static final String SUSPICIOUS = "suspicious";

    @Condition
    public boolean isSuspicious(@Fact("request") HttpServletRequest request) {
        // criteria of suspicious could be based on ip, user-agent, etc.
        // here for simplicity, it is based on the presence of a request parameter 'suspicious'
        return request.getParameter(SUSPICIOUS) != null;
    }
    
    @Action
    public void setSuspicious(@Fact("request") HttpServletRequest request) {
        request.setAttribute(SUSPICIOUS, true);
        System.out.println("1");
    }
    
    @Priority
    public int getPriority() {
    	return 2;
    }
}