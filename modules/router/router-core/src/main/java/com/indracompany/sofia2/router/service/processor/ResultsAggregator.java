package com.indracompany.sofia2.router.service.processor;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;



public class ResultsAggregator implements AggregationStrategy {
 
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        // put order together in old exchange by adding the order from new exchange
 
        if (oldExchange == null) {
            // the first time we aggregate we only have the new exchange,
            // so we just return it
        	return newExchange;
        }
 
        String alarms = oldExchange.getIn().getHeader("alarms",String.class);
        String newAlarm = newExchange.getIn().getHeader("alarms",String.class);
 
        // put orders together separating by semi colon
        alarms = alarms + newAlarm;
        // put combined order back on old to preserve it
        oldExchange.getIn().setHeader("alarms", alarms);
 
        // return old as this is the one that has all the orders gathered until now
        return oldExchange;
    }
}