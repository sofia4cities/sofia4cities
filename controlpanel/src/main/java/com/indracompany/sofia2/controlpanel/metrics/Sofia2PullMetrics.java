package com.indracompany.sofia2.controlpanel.metrics;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

@Service
class Sofia2PullMetrics implements PublicMetrics {

    @Autowired
    private SessionRegistry sessionRegistry;
    
    @Override
    public Collection<Metric<?>> metrics() {
    	final List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        Metric<?> metric1 = new Metric<Integer>("_sofia2.:controlpanel.userslogged", allPrincipals.size(), new Date());
        Metric<?> metric2 = new Metric<Integer>("_sofia2:controlpanel.other", 1, new Date());
        //Metric<?> metric2 = new Metric<String>("com.indracompany.sofia2.controlpanel.metric.userslogged.names", allPrincipals.get(0).toString(),new Date());
        HashSet<Metric<?>> set = new HashSet<Metric<?>>();
        set.add(metric1);
        set.add(metric2);
        return set;
    }
}