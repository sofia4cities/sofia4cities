/**
 * Sofia2Parser
 */
package org.apache.zeppelin.onesaitplatform;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.onesaitplatform.converter.TableConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OnesaitPlatformParser {
    private static final Logger log = LoggerFactory.getLogger(OnesaitPlatformParser.class);

    private static Pattern patternZput = Pattern.compile("^[ ]*z\\.put\\([ ]*\\\"([a-zA-Z0-9_$#-]+)\\\"[ ]*\\,[ ]*([\\*\\: \\{\\}\\(\\)\\:\\,\\\"\\.\\[\\]a-zA-Z0-9_$#-]+[ ]*)\\)[ ]*$");
    private static Pattern patternZget = Pattern.compile("([ ]*z\\.get\\([ ]*\\\"([a-zA-Z0-9_$#-]+)\\\"[ ]*\\)[ ]*)");
    private static Pattern patternInsert = Pattern.compile("^insert\\(\\\"([a-zA-Z0-9_]+)\\\"\\,([\\: \\[\\]\\{\\}\\(\\)\\:\\,\\\"\\.a-zA-Z0-9_$#-]+)\\)[ ]*$");
    private static Pattern patterAsZTable = Pattern.compile("^[ ]*asZTable\\([ ]*([\\*\\: \\{\\}\\(\\)\\:\\,\\\"\\.\\[\\]a-zA-Z0-9_$#-]+[ ]*)\\)[ ]*$");
    private static Pattern patternInitConnection = Pattern.compile("^[ ]*initConnection\\([ ]*\\\"([a-zA-Z0-9_$#-]+)\\\"[ ]*\\,[ ]*\\\"([a-zA-Z0-9_$#-]+)\\\"[ ]*\\)[ ]*$");
    
    public static List<String> parseAndExecute(InterpreterContext context, OnesaitPlatformConnection ospc, String sentence) throws UnsupportedEncodingException {
        String cleanedSentence = cleanSentente(sentence);
        if (cleanedSentence.toLowerCase().startsWith("select")){//SQL Query
        	if(!ospc.isConnected()) {
        		List<String> l = new LinkedList<String>();
        		l.add("Error sql query, please connect to onesait platforn with sentence: initConnection(\"client\",\"token\")");
        		return l;
        	}
            return ospc.doQuery(ospc.generateURLQuery(getOntologyFromSQLQuery(sentence),sentence, "SQL"));
        }
        else if (cleanedSentence.toLowerCase().startsWith("db.")) {//Native Query
        	if(!ospc.isConnected()) {
        		List<String> l = new LinkedList<String>();
        		l.add("Error native query, please connect to onesait platforn with sentence: initConnection(\"client\",\"token\")");
        		return l;
        	}
            return ospc.doQuery(ospc.generateURLQuery(getOntologyFromMongoQuery(sentence),sentence, "NATIVE"));
        }
        else if (cleanedSentence.toLowerCase().startsWith("insert")) {//Native Query
        	if(!ospc.isConnected()) {
        		List<String> l = new LinkedList<String>();
        		l.add("Error insert, please connect to onesait platforn with sentence: initConnection(\"client\",\"token\")");
        		return l;
        	}
            String resourcePoolStr = null;
            log.info("Detected insert query " + cleanedSentence);
            Matcher mzget = patternZget.matcher(cleanedSentence);
            String zGetFunction = null;
            String zVar = null;
            if (mzget.find()) {
                log.info("Pattern z.get detected");
                zGetFunction = mzget.group(1);
                zVar = mzget.group(2);
                log.info("Pattern z.get detected: " + zGetFunction + " with " + zVar);
                
                Object rpool = context.getResourcePool().get(zVar).get();
                log.info("rpool: " + rpool.getClass());
                if(context.getResourcePool().get(zVar).get() instanceof String){
                    log.info("Is String");
                    resourcePoolStr = (String) context.getResourcePool().get(zVar).get();
                }
                else{
                    log.info("Is Array String");
                    resourcePoolStr = Arrays.toString(((String[])context.getResourcePool().get(zVar).get()));
                }
            }
            Matcher mzsentence = patternInsert.matcher(cleanedSentence);
            String ontology = null;
            String instances = null;
            if (mzsentence.find()) {
                ontology = mzsentence.group(1);
                instances = resourcePoolStr!=null?resourcePoolStr:mzsentence.group(2);
                return ospc.doInsert(ontology,instances);   
            }
        }
        else{//Custom sentence
            if(cleanedSentence.startsWith("z.")){ //Zeppelin put/get
            	log.info("Custom sentence " + cleanedSentence);
                ArrayList<String> astr = new ArrayList<String>();
                Matcher mzput = patternZput.matcher(cleanedSentence);
                String keyPut = null;
                String query = null;
                if (mzput.find()) {
                    keyPut = mzput.group(1);
                    query = mzput.group(2);
                    log.info("keyPut: " + keyPut);
                    log.info("query: " + query);
                    try{
                        List<String> res = parseAndExecute(context, ospc, query);
                        log.info("Res:" + res.toString());
                        String [] resArray = new String[res.size()];
                        for(int i=0;i<res.size();i++){
                            resArray[i] = res.get(i);
                        }
                        context.getResourcePool().put(keyPut,resArray);
                        astr.add("Zeppelin Context: Successfully inyected in " + keyPut);
                        return astr;
                    }
                    catch(Exception e){
                        String errorMsg = "Error inyecting in zeppelin context: " + e.getMessage();
                        log.error(errorMsg);
                        astr.add(errorMsg);
                        return astr;
                    }
                }
                else{
                    return astr;
                }

            }
            else if(cleanedSentence.startsWith("asZTable")){ //Transform to zeppelin output table
            	Matcher mztable = patterAsZTable.matcher(cleanedSentence);
            	if (mztable.find()) {
            		String query = mztable.group(1);
            		log.info("Query inside: " + query);
                	List<String> res = parseAndExecute(context, ospc, query);
                	return TableConverter.fromListStr(res);
            	}
            	return null;
            }
            else if(cleanedSentence.startsWith("initConnection")) {
            	Matcher mzinitconnect = patternInitConnection.matcher(cleanedSentence);
            	if (mzinitconnect.find()) {
            		String client = mzinitconnect.group(1);
            		String token = mzinitconnect.group(2);
            		log.info("Join with client: " + client + " , token: " + token);
            		if(ospc.isConnected()) {
            			ospc.doLeave();
            		}
            		List<String> l = new LinkedList<String>();
                	if(ospc.doJoin(client, token)) {
                		l.add("Connected with client: " + client + " , token: " + token);
                	}
                	else {
                		l.add("Error connection with client: " + client + " , token: " + token);
                	}
                	return l;
                	
            	}
            	return null;
            }
            return null;
        }
        return null;
    }
    
    

    private static String cleanSentente(String sentence) {
        String cleanedSentence = sentence.trim();
        if(cleanedSentence.endsWith(";")){
            cleanedSentence = cleanedSentence.substring(0, -1);
        }
        return cleanedSentence;
    }

    private static String getOntologyFromSQLQuery(String sqlQuery){
        Pattern p=Pattern.compile("from\\s+(?:\\w+\\.)*(\\w+)(\\s*$|\\s+(LIMIT|AS|WHERE|JOIN|START\\s+WITH|ORDER\\s+BY|GROUP\\s+BY))",Pattern.CASE_INSENSITIVE);
        Matcher m=p.matcher(sqlQuery);
        String ontology = null;
        while(m.find()){
            ontology = m.group(1);
            break;
        }
        return ontology;
    }
    
    private static String getOntologyFromMongoQuery(String mongoQuery){
        String[] lqm =  mongoQuery.split("\\.");
        if(lqm.length>1) {
        	return lqm[1];
        }
        return "";
    }
}

