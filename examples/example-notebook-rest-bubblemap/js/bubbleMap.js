var loadBubbleMap = function(data){
	$('.map').removeClass('hide');
    jQuery.when(
        jQuery.getScript('https://cdnjs.cloudflare.com/ajax/libs/proj4js/2.3.6/proj4.js'),
        jQuery.getScript('https://code.highcharts.com/maps/highmaps.js'),
        jQuery.getScript('https://code.highcharts.com/maps/modules/exporting.js'),
        jQuery.getScript('https://code.highcharts.com/mapdata/custom/world.js'),
        jQuery.getScript('https://cdn.rawgit.com/sdecima/javascript-detect-element-resize/master/jquery.resize.js'),
        jQuery("#map").ready,
        jQuery.Deferred(function( deferred ){
            jQuery( deferred.resolve );
        })
        ).done(function(){		
        var H = Highcharts,
            map = H.maps['custom/world'],
            chart;

        chart = Highcharts.mapChart('map', {

            title: {
                text: 'Flights from ICN'
            },

            tooltip: {
                pointFormat: '{point.city}({point.IATA}), {point.country}<br>' +
                    '# of Arrival: {point.z}'
            },
            
            mapNavigation: {
                enabled: true,
                buttonOptions: {
                    verticalAlign: 'bottom'
                }
            },

            series: [{
                name: 'Basemap',
                mapData: map,
                borderColor: '#606060',
                nullColor: 'rgba(200, 200, 200, 0.2)',
                showInLegend: false
            }, {
                type: 'mapbubble',
                dataLabels: {
                    enabled: true,
                    format: '{point.city}'
                },
                name: 'Cities',
                data: data,
                maxSize: '10%',
                color: H.getOptions().colors[0]
            }]
        });
        });
};