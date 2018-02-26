'use strict';

var gauge = require('canvas-gauges');

module.exports = function () {
  return {
    restrict: 'E',
    scope: {
      metric: '=forMetric',
      globalMax: '=?globalMax'
    },
    link: function (scope, element) {

      //color levels configuration of the highlights based on the globalMax
      var offset = (scope.globalMax % 3) != 0 || scope.globalMax == 0; 
      var greenLevel = ((Math.trunc(scope.globalMax/3) * 1 ) + offset);
      var orangeLevel = ((Math.trunc(scope.globalMax/3) * 2 ) + offset);

      //colors definitions for each level
      var greenColor = 'rgba(50,168,45,1)';
      var orangeColor = 'rgba(214,118,39,1)';
      var redColor = 'rgba(224,52,52,1)';

      //highlights configuration based on the levels.
      var highlights;
      if (scope.globalMax < 3) {
        highlights = [];
      } else {
        highlights = [
          { from: 0, to: greenLevel, color: greenColor },
          { from: greenLevel, to: orangeLevel, color: orangeColor },
          { from: orangeLevel, to: scope.globalMax, color: redColor }
        ];
      }

      //logic followed to select the color of the showed data in the gauge
      var colorBarProgress = redColor;
      if (scope.metric.value < orangeLevel) {
        if (scope.metric.value < greenLevel){
          colorBarProgress = greenColor;
        } else {
          colorBarProgress = orangeColor;
        }
      }

      //configuration of the gauge
      var radial = new gauge.RadialGauge({
        animationRule: 'bounce',
        animationDuration: 500,
        barStrokeWidth: 0,
        barWidth: 25, 
        borders: false,
        borderMiddleWidth: 0,
        borderOuterWidth: 0,
        borderInnerWidth: 0,
        borderShadowWidth: 0,
        colorBar: '#444444',
        colorBarProgress: colorBarProgress,
        colorPlate: '#34302D',
        colorMajorTicks: '#f5f5f5',
        colorMinorTicks: '#dddddd',
        colorTitle: '#ffffff',
        colorUnits: '#cccccc',
        colorNumbers: '#34302D',
        colorNeedle: 'rgba(240, 128, 128, 1)',
        colorNeedleEnd: 'rgba(255, 160, 122, 1)',
        colorStrokeTicks: '#34302D',
        colorValueBoxBackground: '#34302D',
        colorValueBoxRect: '#34302D', 
        colorValueBoxShadow: '#34302D',
        colorValueText: '#f2f3f8',
        fontValue: '"Varela Round", sans-serif',
        fontValueWeight: 'bold',
        fontValueSize: 36,
        fontNumbers: '"Varela Round", sans-serif',
        fontNumbersWeight: 'normal',
        fontNumbersSize: 14,
        height: 250,
        highlights: highlights,
        highlightsWidth: 5,
        minValue: 0,
        maxValue: scope.globalMax,
        majorTicks: [],
        minorTicks: 0,
        needle: false,
        numbersMargin: 10,
        renderTo: document.createElement('canvas'),
        strokeTicks: false,
        title: '',
        units: '',
        value: scope.metric.value,
        valueBox: true,
        valueBoxStroke: 0,
        valueDec: 0,
        valueInt: 3,
        width: 250        
      });

      element[0].getElementsByClassName('sofia2-metric-gauge')[0].appendChild(radial.options.renderTo);
      radial.update();
    },
    template: require('./canvasGaugeMetric.tpl.html')
  };
};