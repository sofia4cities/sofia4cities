#!/bin/sh

echo "Sustituyendo variables de entorno en ficheros de propiedades de sofia2"
		
grep -rl '${SERVERNAME}' /opt/nodeRed/Flow-Engine-Manager | xargs sed -i 's/${SERVERNAME}/'"$SERVERNAME"'/g'
			
echo "Arrancando Tomcat..."	
java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=docker -jar /app.jar

exit 0