# Sofia2 Node-RED

*Ver en otros idiomas: [Inglés](README.md), [Español](README.es.md).*

## Updates:

* **Diciembre 2017**
	* Componentes soportan conexión HTTPS 
	* Se añaden logs
		
## Copyright

© 2013-17 Minsait

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

## API documentation

Antes de utilizar el API SSAP por primera vez, le recomendamos que se familiarice con los conceptos básicos de la plataforma Sofia2. Están incluidos en la
documentación de desarrollo de Sofia2, que puede descargarse desde http://sofia2.com/desarrollador.html.

El Api viene con una test suite en la que se pueden ver ejemplos de uso de cada una de las consultas posibles.

## Contenido del repositorio

Este repositorio contiene los siguientes nodos para consultar a Sofia2 desde Node-RED:

* **sofia2-connection-config**: Los nodos de configuración tienen un alcance global por defecto, esto significa que el estado se compartirá entre flujos. Este nodo representa una conexión compartida con un sistema remoto. En este caso, el nodo de configuración es responsable de crear la conexión (REST o MQTT) y ponerla a disposición de los nodos que utilizan el nodo de configuración. Los siguientes parámetros son necesarios para definir la conexión:
    * Protocol: El protocolo puede ser REST o MQTT. Para conexiones MQTT es necesario indicar la IP y el número del puerto, mientras que para conexiones REST sólo es necesario el endpoint.
    * ThinKP y Instance: Esto sirve para indicar la Instancia KP a la que hace referencia.
    * Token: Número de identificación del ThinKP.
    * Renovate session: Tiempo de regeneración de la sesión
    
* **sofia2-delete**: Este nodo borra datos de una ontología de acuerdo con la query, se necesitan los siguientes parámetros:
    * Ontology: Nombre de la ontología.
    * Query: Query para borrar los datos.
    * Query Type: Tipo de la query, que puede ser SQLLIKE o Nativo.

* **sofia2-insert**: Este nodo inserta datos en una ontología, es necesario indicar el nombre de la ontología, así como los datos que van a ser insertados en formato JSON.

* **sofia2-leave**: Este nodo libera la conexión, es necesario indicar la sessionKey de la conexión que va a ser liberada.

* **sofia2-query**: Este nodo ejecuta una query sobre una ontología, son necesarios los siguientes parámetros:
    * Ontology: Nombre de la ontología.
    * Query: Query a ejecutar.
    * Query Type: Tipo de la query, que puede ser SQLLIKE o Nativo.
    
* **sofia2-update**: Este nodo actualiza los datos de una ontología, es necesario poner los siguientes parámetros:
    * Ontology: Nombre de la ontología.
    * Query: Query para borrar los datos.
    * Query Type: Tipo de la query, que puede ser SQLLIKE o Nativo.
    
Este repositorio contiene también los siguientes directorios:

* [lib](lib): Este directorio contiene los archivos necesarios para construir el mensaje SSAP y el KP nevesario para la conexión MQTT.
* [icons](icons): Este nodo contiene los iconos usados en los nodos.

## Información de contacto

Si necesita recibir soporte, puede contactar con nosotros en www.sofia2.com o enviando un correo electrónico a [plataformasofia2@indra.es](mailto:plataformasofia2@indra.es).

Además, si desea contribuir al API, no dude en enviarnos una pull request.
