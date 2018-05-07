# Worst

This is the sql to create a query in the API REST to obtain the worst district in the city.

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

This is the SQL to create the query of the API REST to obtain the best district in the city.

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

# Air quality of a day

This query allows to get information about an specific district in a specific date.
Due to there are several measures in a day, it returns the average of all the measures for that day in the district.

```sql
select avg(c.GeoAirQuality.o3) as o3, avg(c.GeoAirQuality.no2) as no2, avg(c.GeoAirQuality.so2) as so2 from GeoAirQuality as c where c.GeoAirQuality.timestamp >= DATE({$date}) AND c.GeoAirQuality.timestamp <= DATE({$date}) AND c.GeoAirQuality.stationName = {$stationName} group by GeoAirQuality.stationName
```

