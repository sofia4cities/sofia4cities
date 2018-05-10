#!/bin/sh

echo "Substituting environment variables in Quasar properties"	
	
grep -rl '${REALTIMEDB}' /usr/local/onesait/quasar | xargs sed -i 's/${REALTIMEDB}/'"$REALTIMEDB"'/g'			
	
echo "Executing Quasar..."	
java $JAVA_OPTS -jar quasar-web-assembly-14.2.6.jar -c config.json