# Worst

```sql
select A.GeoAirQuality.stationName as stationName,
(Case when {$measure} = "o3" then avg(A.GeoAirQuality.o3) 
      when {$measure} = "so2" then avg(A.GeoAirQuality.so2)
      when {$measure} = "no2" then avg(A.GeoAirQuality.no2) else  0 end) as measure
from GeoAirQuality as A
where A.GeoAirQuality.timestamp >= DATE({$date}) AND A.GeoAirQuality.timestamp <= DATE({$date})
group by A.GeoAirQuality.stationName
order by measure desc
limit 1
``` 

# Best

```sql
select A.GeoAirQuality.stationName as stationName,
(Case when {$measure} = "o3" then avg(A.GeoAirQuality.o3) 
      when {$measure} = "so2" then avg(A.GeoAirQuality.so2)
      when {$measure} = "no2" then avg(A.GeoAirQuality.no2) else  0 end) as measure
from GeoAirQuality as A
where A.GeoAirQuality.timestamp >= DATE({$date}) AND A.GeoAirQuality.timestamp <= DATE({$date})
group by A.GeoAirQuality.stationName
order by measure
limit 1
```

```sql
select avg(c.GeoAirQuality.o3) as o3, avg(c.GeoAirQuality.no2) as no2, avg(c.GeoAirQuality.so2) as so2 from GeoAirQuality as c where c.GeoAirQuality.timestamp >= DATE({$date}) AND c.GeoAirQuality.timestamp <= DATE({$date}) AND c.GeoAirQuality.stationName = {$stationName} group by GeoAirQuality.stationName
```

