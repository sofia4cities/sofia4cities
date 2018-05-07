# Curl commands


## Insert subscription

Esto devuelve código 200 y un id si todo va bien.

Ejemmplo de datos para subscribirse:

```json
{
        "Subscription": {
            "stationName": "Helsinki",
            "email": "cfsanchez@minsait.com",
            "no2Threshold": 0,
            "o3Threshold": 0,
            "so2Threshold": 0
        }
}
```

Invocación del servicio usando curl. Importante la opción --insecure para evitar la verificación SSL ya que el certificado del servidor es autogenerado. Esto lo tendréis que tener en cuenta también en el Chatbot.
También hay que fijarse en la cabecera "X-SOFIA2-APIKey: 1a17b5502a3b49228b4ce97a16a23abf". Este es el token para autorizar la operación.

```bash
curl -X POST --insecure "https://s4citiespro.westeurope.cloudapp.azure.com/api-manager/server/api/v1/ChatBotSubscriptionAPI/" -H "accept: text/plain" -H "X-SOFIA2-APIKey: 1a17b5502a3b49228b4ce97a16a23abf" -H "Content-Type: application/json" -d "{ \"Subscription\": { \"stationName\": \"Helsinki\", \"email\": \"cfsanchez@minsait.com\", \"no2Threshold\": 0, \"o3Threshold\": 0, \"so2Threshold\": 0 }}"
```

## Obtain data of a district in a date.

La url sin codificar para http:

```url
https://s4citiespro.westeurope.cloudapp.azure.com/api-manager/server/api/v1/ChatBotAPI/AirQuality?$date[0]={date}&$date[1]={date}&$stationName={stationName}&queryType=SQLLIKE&targetdb=BDTR&query=select avg(c.GeoAirQuality.o3) as o3, avg(c.GeoAirQuality.no2) as no2, avg(c.GeoAirQuality.so2) as so2 from GeoAirQuality as c where c.GeoAirQuality.timestamp >= DATE({$date}) AND c.GeoAirQuality.timestamp <= DATE({$date}) AND c.GeoAirQuality.stationName = {$stationName} group by GeoAirQuality.stationName&date=2018-03-16&stationName=Helsinki
```
Sólo hay que modificar los parámetros date y stationName.
Invocación del servicio usando curl. Como antes es importante fijarse en el --insecure y en el token para autenticar.

```bash
curl --insecure -X GET "https://s4citiespro.westeurope.cloudapp.azure.com/api-manager/server/api/v1/ChatBotAPI/AirQuality?%24date%5B0%5D=%7Bdate%7D&%24date%5B1%5D=%7Bdate%7D&%24stationName=%7BstationName%7D&queryType=SQLLIKE&targetdb=BDTR&query=select%20avg(c.GeoAirQuality.o3)%20as%20o3%2C%20avg(c.GeoAirQuality.no2)%20as%20no2%2C%20avg(c.GeoAirQuality.so2)%20as%20so2%20from%20GeoAirQuality%20as%20c%20where%20c.GeoAirQuality.timestamp%20%3E%3D%20DATE(%7B%24date%7D)%20AND%20c.GeoAirQuality.timestamp%20%3C%3D%20DATE(%7B%24date%7D)%20AND%20c.GeoAirQuality.stationName%20%3D%20%7B%24stationName%7D%20group%20by%20GeoAirQuality.stationName&date=2018-03-16&stationName=Helsinki" -H "accept: text/plain" -H "X-SOFIA2-APIKey: 1a17b5502a3b49228b4ce97a16a23abf" -H "Cacheable: false"
```

## Obtain the worst district based on date and type of measure.

La url sin codificar para http:

```url
https://s4citiespro.westeurope.cloudapp.azure.com/api-manager/server/api/v1/ChatBotAPI/WorstDistrict?$measure[0]={measure}&$measure[1]={measure}&$measure[2]={measure}&$date[0]={date}&$date[1]={date}&queryType=SQLLIKE&targetdb=BDTR&query=select A.GeoAirQuality.stationName as stationName, (Case when {$measure} = "o3" then avg(A.GeoAirQuality.o3)        when {$measure} = "so2" then avg(A.GeoAirQuality.so2)       when {$measure} = "no2" then avg(A.GeoAirQuality.no2) else  0 end) as measure from GeoAirQuality as A where A.GeoAirQuality.timestamp >= DATE({$date}) AND A.GeoAirQuality.timestamp <= DATE({$date}) group by A.GeoAirQuality.stationName order by measure desc limit 1&measure=o3&date=2018-03-16
```

La url es algo larga, pero sólo hay que cambiar los dos últimos parámetros measure y date.
El comando curl para probarla es:

```bash
curl --insecure -X GET "https://s4citiespro.westeurope.cloudapp.azure.com/api-manager/server/api/v1/ChatBotAPI/WorstDistrict?%24measure%5B0%5D=%7Bmeasure%7D&%24measure%5B1%5D=%7Bmeasure%7D&%24measure%5B2%5D=%7Bmeasure%7D&%24date%5B0%5D=%7Bdate%7D&%24date%5B1%5D=%7Bdate%7D&queryType=SQLLIKE&targetdb=BDTR&query=select%20A.GeoAirQuality.stationName%20as%20stationName%2C%20(Case%20when%20%7B%24measure%7D%20%3D%20%22o3%22%20then%20avg(A.GeoAirQuality.o3)%20%20%20%20%20%20%20%20when%20%7B%24measure%7D%20%3D%20%22so2%22%20then%20avg(A.GeoAirQuality.so2)%20%20%20%20%20%20%20when%20%7B%24measure%7D%20%3D%20%22no2%22%20then%20avg(A.GeoAirQuality.no2)%20else%20%200%20end)%20as%20measure%20from%20GeoAirQuality%20as%20A%20where%20A.GeoAirQuality.timestamp%20%3E%3D%20DATE(%7B%24date%7D)%20AND%20A.GeoAirQuality.timestamp%20%3C%3D%20DATE(%7B%24date%7D)%20group%20by%20A.GeoAirQuality.stationName%20order%20by%20measure%20desc%20limit%201&measure=o3&date=2018-03-16" -H "accept: application/json" -H "X-SOFIA2-APIKey: 1a17b5502a3b49228b4ce97a16a23abf" -H "Cacheable: false"
```

## Obtain the best district based on date and type of measure 

La url sin condificar para http es la siguiente:

```url
https://s4citiespro.westeurope.cloudapp.azure.com/api-manager/server/api/v1/ChatBotAPI/BestDistrict?$measure[0]={measure}&$measure[1]={measure}&$measure[2]={measure}&$date[0]={date}&$date[1]={date}&targetdb=BDTR&queryType=SQLLIKE&query=select A.GeoAirQuality.stationName as stationName, (Case when {$measure} = "o3" then avg(A.GeoAirQuality.o3)        when {$measure} = "so2" then avg(A.GeoAirQuality.so2)       when {$measure} = "no2" then avg(A.GeoAirQuality.no2) else  0 end) as measure from GeoAirQuality as A where A.GeoAirQuality.timestamp >= DATE({$date}) AND A.GeoAirQuality.timestamp <= DATE({$date}) group by A.GeoAirQuality.stationName order by measure limit 1&measure=o3&date=2018-03-16
```

Aunque es una url algo aparatosa sólo hay que modificar los dos últimos parámetros: measure y date.
El comando curl para probarlo:

```bash
curl --insecure -X GET "https://s4citiespro.westeurope.cloudapp.azure.com/api-manager/server/api/v1/ChatBotAPI/BestDistrict?%24measure%5B0%5D=%7Bmeasure%7D&%24measure%5B1%5D=%7Bmeasure%7D&%24measure%5B2%5D=%7Bmeasure%7D&%24date%5B0%5D=%7Bdate%7D&%24date%5B1%5D=%7Bdate%7D&targetdb=BDTR&queryType=SQLLIKE&query=select%20A.GeoAirQuality.stationName%20as%20stationName%2C%20(Case%20when%20%7B%24measure%7D%20%3D%20%22o3%22%20then%20avg(A.GeoAirQuality.o3)%20%20%20%20%20%20%20%20when%20%7B%24measure%7D%20%3D%20%22so2%22%20then%20avg(A.GeoAirQuality.so2)%20%20%20%20%20%20%20when%20%7B%24measure%7D%20%3D%20%22no2%22%20then%20avg(A.GeoAirQuality.no2)%20else%20%200%20end)%20as%20measure%20from%20GeoAirQuality%20as%20A%20where%20A.GeoAirQuality.timestamp%20%3E%3D%20DATE(%7B%24date%7D)%20AND%20A.GeoAirQuality.timestamp%20%3C%3D%20DATE(%7B%24date%7D)%20group%20by%20A.GeoAirQuality.stationName%20order%20by%20measure%20limit%201&measure=o3&date=2018-03-16" -H "accept: application/json" -H "X-SOFIA2-APIKey: 1a17b5502a3b49228b4ce97a16a23abf" -H "Cacheable: false"
```