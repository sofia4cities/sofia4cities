package org.apache.zeppelin.onesaitplatform;

import java.io.UnsupportedEncodingException;

public class MainTest {
    public static void main(String[] args) throws UnsupportedEncodingException {
        OnesaitPlatformConnection opc = new OnesaitPlatformConnection("s4citiespro.westeurope.cloudapp.azure.com", 80, "/iotbroker", 60000l);
        OnesaitPlatformParser.parseAndExecute(null, opc, "initConnection(\"clientAnalytics\",\"a5a9ea12c4d742ef80105f3e9b7271d1\")");
        OnesaitPlatformParser.parseAndExecute(null, opc, "db.testClientjs.find()");
        OnesaitPlatformParser.parseAndExecute(null, opc, "select c.borough, c.grades[0].score from Restaurants AS c limit 1");
        OnesaitPlatformParser.parseAndExecute(null, opc, "insert(\"testClientjs\",{\"EmptyBase\":{\"test\":\"zeppelinTestS1\"}})");
        OnesaitPlatformParser.parseAndExecute(null, opc, "insert(\"testClientjs\",[{\"EmptyBase\":{\"test\":\"zeppelinTestM1\"}},{\"EmptyBase\":{\"test\":\"zeppelinTestM2\"}}])");
        OnesaitPlatformParser.parseAndExecute(null, opc, "z.put(\"testZ\",select * from testClientjs limit 1)");
        OnesaitPlatformParser.parseAndExecute(null, opc, "asZTable(select c.borough, c.grades[0].score from Restaurants AS c limit 1)");
    }
}