package com.indracompany.sofia2.api.rule;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;
import org.springframework.stereotype.Component;

@Component
@Rule
public class SuspiciousRequestRule2 {

    static final String SUSPICIOUS = "suspicious";

    
    @Priority
    public int getPriority() {
    	return 1;
    }
    
    @Condition
    public boolean isSuspicious(Facts facts) {
        // criteria of suspicious could be based on ip, user-agent, etc.
        // here for simplicity, it is based on the presence of a request parameter 'suspicious'
       return true;
    }
    
    @Action
    public void setSuspicious(Facts facts) {
        System.out.println("1");
    }
}