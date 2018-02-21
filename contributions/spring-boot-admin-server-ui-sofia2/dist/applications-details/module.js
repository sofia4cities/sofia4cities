webpackJsonp([2],{0:function(t,n,e){(function(t){"use strict";var n=e(1),a=n.module("sba-applications-details",["sba-applications"]);t.sbaModules.push(a.name),a.controller("detailsCtrl",e(92)),a.filter("humanBytes",e(93)),a.filter("joinArray",e(94)),a.filter("timeInterval",e(95)),a.component("sbaHealthStatus",e(87)),a.component("sbaUptime",e(91)),a.component("sbaMemoryStats",e(89)),a.component("sbaJvmStats",e(88)),a.component("sbaGcStats",e(86)),a.component("sbaServletContainerStats",e(90)),a.component("sbaDatasourceStats",e(85)),a.component("sbaCacheStats",e(84)),a.config(["$stateProvider",function(t){t.state("applications.details",{url:"/details",templateUrl:"applications-details/views/details.html",controller:"detailsCtrl"})}]),a.run(["ApplicationViews","$sce",function(t,n){t.register({order:0,title:n.trustAsHtml('<i class="fa fa-info fa-fw"></i>Details'),state:"applications.details"})}])}).call(n,function(){return this}())},35:function(t,n){},84:function(t,n,e){"use strict";var a=e(1);t.exports={bindings:{metrics:"<metrics"},controller:["$filter",function(t){"ngInject";var n=this;n.$onChanges=function(){n.caches=[],a.forEach(n.metrics,function(e,a){var s=/cache\.(.+)\.size/.exec(a);null!==s&&n.caches.push({name:s[1],size:e,hitRatio:t("number")(100*n.metrics["cache."+s[1]+".hit.ratio"],2),missRatio:t("number")(100*n.metrics["cache."+s[1]+".miss.ratio"],2)})})},n.getBarClass=function(t){return t<75?"bar-success":t>=75&&t<=95?"bar-warning":"bar-danger"}}],template:e(144)}},85:function(t,n,e){"use strict";var a=e(1);t.exports={bindings:{metrics:"<metrics"},controller:["$filter",function(t){"ngInject";var n=this;n.$onChanges=function(){n.datasources=[],a.forEach(n.metrics,function(e,a){var s=/datasource\.(.+)\.active/.exec(a);null!==s&&n.datasources.push({name:s[1],active:e,usage:t("number")(100*n.metrics["datasource."+s[1]+".usage"],2)})})},n.getBarClass=function(t){return t<75?"bar-success":t>=75&&t<=95?"bar-warning":"bar-danger"}}],template:e(145)}},86:function(t,n,e){"use strict";var a=e(1);t.exports={bindings:{metrics:"<metrics"},controller:function(){var t=this;t.$onChanges=function(){t.gcs=[],a.forEach(t.metrics,function(n,e){var a=/gc\.(.+)\.time/.exec(e);null!==a&&t.gcs.push({name:a[1],time:n+" ms",count:t.metrics["gc."+a[1]+".count"]})})}},template:e(146)}},87:function(t,n,e){"use strict";e(35),t.exports={bindings:{health:"<health"},controller:["$scope",function(t){"ngInject";var n=this;n.isHealthDetail=function(t,n){return"status"!==t&&null!==n&&(Array.isArray(n)||"object"!=typeof n)},n.isChildHealth=function(t,n){return null!==n&&!Array.isArray(n)&&"object"==typeof n},n.$onChanges=function(){t.health=n.health,t.name="application"}}],template:"<ng-include src=\"'applications-details/views/templates/health-status.html'\"></ng-include>"}},88:function(t,n,e){"use strict";t.exports={bindings:{metrics:"<metrics"},controller:["$filter",function(t){"ngInject";var n=this;n.$onChanges=function(){n.metrics["systemload.average"]&&(n.systemload=t("number")(n.metrics["systemload.average"],2))}}],template:e(147)}},89:function(t,n,e){"use strict";t.exports={bindings:{metrics:"<metrics"},controller:["$filter",function(t){"ngInject";var n=this;n.$onChanges=function(){n.memory={total:n.metrics.mem,used:n.metrics.mem-n.metrics["mem.free"],unit:"K"},n.memory.percentUsed=t("number")(n.memory.used/n.memory.total*100,2),n.heap={total:n.metrics["heap.committed"],used:n.metrics["heap.used"],init:n.metrics["heap.init"],max:n.metrics["heap.max"]||n.metrics.heap,unit:n.metrics["heap.max"]?"B":"K"},n.heap.percentUsed=t("number")(n.heap.used/n.heap.total*100,2),n.nonheap={total:n.metrics["nonheap.committed"],used:n.metrics["nonheap.used"],init:n.metrics["nonheap.init"],max:n.metrics.nonheap,unit:"K"},n.nonheap.percentUsed=t("number")(n.nonheap.used/n.nonheap.total*100,2)},n.getBarClass=function(t){return t<75?"bar-success":t>=75&&t<=95?"bar-warning":"bar-danger"}}],template:e(148)}},90:function(t,n,e){"use strict";t.exports={bindings:{metrics:"<metrics"},controller:function(){var t=this;t.$onChanges=function(){t.sessions={active:t.metrics["httpsessions.active"],max:t.metrics["httpsessions.max"]<0?"unbounded":t.metrics["httpsessions.max"]}}},template:e(149)}},91:function(t,n){"use strict";t.exports={bindings:{value:"<value"},controller:["$interval","$filter",function(t,n){"ngInject";var e=this,a=null,s=0;e.$onChanges=function(){e.clock=n("timeInterval")(e.value),s=Date.now(),null!==a&&(t.cancel(a),a=null),a=t(function(){e.clock=n("timeInterval")(e.value+Date.now()-s)},1e3)}}],template:"{{$ctrl.clock}} [d:h:m:s]"}},92:function(t,n,e){"use strict";var a=e(1);t.exports=["$scope","application","$interval",function(t,n,e){"ngInject";t.application=n,t.metrics=null,t.refreshInterval=1,t.refresher=null,t.refresh=function(){n.getInfo().then(function(n){t.info=n.data}).catch(function(n){t.error=n.data}),n.getHealth().then(function(n){t.health=n.data}).catch(function(n){t.health=n.data}),n.getMetrics().then(function(n){t.metrics=n.data,t.hasDatasources=!1,t.hasCaches=!1,a.forEach(t.metrics,function(n,e){!t.hasDatasources&&e.startsWith("datasource.")&&(t.hasDatasources=!0),!t.hasCaches&&e.startsWith("cache.")&&(t.hasCaches=!0)})}).catch(function(){t.metrics=null})},t.start=function(){t.refresher=e(function(){t.refresh()},1e3*t.refreshInterval)},t.stop=function(){null!==t.refresher&&(e.cancel(t.refresher),t.refresher=null)},t.toggleAutoRefresh=function(){null===t.refresher?t.start():t.stop()},t.$on("$destroy",function(){t.stop()}),t.refresh()}]},93:function(t,n){"use strict";t.exports=function(){var t={B:Math.pow(1024,0),K:Math.pow(1024,1),M:Math.pow(1024,2),G:Math.pow(1024,3),T:Math.pow(1024,4),P:Math.pow(1024,5)};return function(n,e){n=n||0,e=e||"B";var a=n*(t[e]||1),s="B";for(var r in t)t[s]<t[r]&&a>=t[r]&&(s=r);return(a/t[s]).toFixed(1).replace(/\.0$/,"").replace(/,/g,"")+s}}},94:function(t,n){"use strict";t.exports=function(){return function(t,n){return Array.isArray(t)?t.join(n):t}}},95:function(t,n){"use strict";t.exports=function(){function t(t,n){for(var e=t+"";e.length<n;)e="0"+e;return e}return function(n){var e=n||0,a=t(Math.floor(e/864e5),2),s=t(Math.floor(e%864e5/36e5),2),r=t(Math.floor(e%36e5/6e4),2),c=t(Math.floor(e%6e4/1e3),2);return a+":"+s+":"+r+":"+c}}},144:function(t,n){t.exports='<table class="table">\n\t<tr ng-repeat-start="cache in $ctrl.caches track by cache.name">\n\t\t<td rowspan="{{cache.hitRatio || cache.missRatio ? 2 : 1}}" ng-bind="cache.name"></td>\n\t\t<td>size</td>\n\t\t<td ng-bind="cache.size">\n\t</tr>\n\t<tr ng-repeat-end ng-if="cache.hitRatio || cache.missRatio">\n\t\t<td colspan="2">\n\t\t\t<div class="progress" style="margin-bottom: 0px;">\n\t\t\t\t<div ng-if="cache.hitRatio" class="bar bar-success" ng-style="{width: cache.hitRatio + \'%\'}">{{cache.hitRatio}}% hits</div>\n\t\t\t\t<div ng-if="cache.missRatio" class="bar bar-danger" ng-style="{width: cache.missRatio + \'%\'}">{{cache.missRatio}}% misses</div>\n\t\t\t</div>\n\t\t</td>\n\t</tr>\n</table>'},145:function(t,n){t.exports='<table class="table">\n\t<tr ng-repeat-start="datasource in $ctrl.datasources track by datasource.name">\n\t\t<td rowspan="2" ng-bind="datasource.name"></td>\n\t\t<td>active connections</td>\n\t\t<td ng-bind="datasource.active"></td>\n\t</tr>\n\t<tr ng-repeat-end>\n\t\t<td colspan="2">\n\t\t\t<div class="progress" style="margin-bottom: 0px;">\n\t\t\t\t<div class="bar" ng-class="$ctrl.getBarClass(datasource.usage)" ng-style="{width: datasource.usage + \'%\'}">{{datasource.usage}}%</div>\n\t\t\t</div>\n\t\t</td>\n\t</tr>\n</table>'},146:function(t,n){t.exports='<table class="table">\n    <tr ng-repeat-start="gc in $ctrl.gcs track by gc.name">\n        <td rowspan="2" ng-bind="gc.name"></td>\n        <td>Count</td>\n        <td ng-bind="gc.count"></td>\n    </tr>\n    <tr ng-repeat-end>\n        <td>Time</td>\n        <td ng-bind="gc.time"></td>\n    </tr>\n</table>\n'},147:function(t,n){t.exports='<table class="table">\n  <tr>\n    <td>Uptime</td>\n    <td colspan="2">\n      <sba-uptime value="$ctrl.metrics.uptime"></sba-uptime>\n    </td>\n  </tr>\n  <tr ng-if="$ctrl.systemload">\n    <td>Systemload</td>\n    <td colspan="2">{{$ctrl.systemload}} (last min. &#x2300; runq-sz)</td>\n  </tr>\n  <tr>\n    <td>Available Processors</td>\n    <td colspan="2" ng-bind="$ctrl.metrics.processors"></td>\n  </tr>\n  <tr>\n    <td rowspan="3">Classes</td>\n    <td>current loaded</td>\n    <td ng-bind="$ctrl.metrics.classes"></td>\n  </tr>\n  <tr>\n    <td>total loaded</td>\n    <td ng-bind="$ctrl.metrics[\'classes.loaded\']"></td>\n  </tr>\n  <tr>\n    <td>unloaded </td>\n    <td ng-bind="$ctrl.metrics[\'classes.unloaded\']"></td>\n  </tr>\n  <tr>\n    <td rowspan="4">Threads</td>\n    <td>current</td>\n    <td ng-bind="$ctrl.metrics.threads"></td>\n  </tr>\n  <tr>\n    <td>total started</td>\n    <td ng-bind="$ctrl.metrics[\'threads.totalStarted\']"></td>\n  </tr>\n  <tr>\n    <td>daemon</td>\n    <td ng-bind="$ctrl.metrics[\'threads.daemon\']"></td>\n  </tr>\n  <tr>\n    <td>peak</td>\n    <td ng-bind="$ctrl.metrics[\'threads.peak\']"></td>\n  </tr>\n</table>\n'},148:function(t,n){t.exports='<table class="table">\n\t<tr>\n\t\t<td colspan="2"> <span>Memory ({{ $ctrl.memory.used | humanBytes:$ctrl.memory.unit }} / {{ $ctrl.memory.total | humanBytes:$ctrl.memory.unit }})</span>\n\t\t\t<div class="progress" style="margin-bottom: 0px;">\n\t\t\t\t<div class="bar" ng-class="$ctrl.getBarClass($ctrl.memory.percentUsed)" ng-style="{width: $ctrl.memory.percentUsed + \'%\'}">{{$ctrl.memory.percentUsed}}%</div>\n\t\t\t</div>\n\t\t</td>\n\t</tr>\n\t<tr>\n\t\t<td colspan="2"> <span>Heap Memory ({{ $ctrl.heap.used | humanBytes:$ctrl.heap.unit }} / {{ $ctrl.heap.total | humanBytes:$ctrl.heap.unit }})</span>\n\t\t\t<div class="progress" style="margin-bottom: 0px;">\n\t\t\t\t<div class="bar" ng-class="$ctrl.getBarClass($ctrl.heap.percentUsed)" ng-style="{width: $ctrl.heap.percentUsed +\'%\'}">{{$ctrl.heap.percentUsed}}%</div>\n\t\t\t</div>\n\t\t</td>\n\t</tr>\n\t<tr>\n\t\t<td>Initial Heap</td>\n\t\t<td>{{$ctrl.heap.init | humanBytes:$ctrl.heap.unit }}</td>\n\t</tr>\n\t<tr>\n\t\t<td>Maximum Heap</td>\n\t\t<td>{{$ctrl.heap.max | humanBytes:$ctrl.heap.unit }}</td>\n\t</tr>\n\t<tr>\n\t\t<td colspan="2"> <span>Non-Heap Memory ({{ $ctrl.nonheap.used | humanBytes:$ctrl.nonheap.unit }} / {{ $ctrl.nonheap.total | humanBytes:$ctrl.nonheap.unit }})</span>\n\t\t\t<div class="progress" style="margin-bottom: 0px;">\n\t\t\t\t<div class="bar" ng-class="$ctrl.getBarClass($ctrl.nonheap.percentUsed)" ng-style="{width: $ctrl.nonheap.percentUsed + \'%\'}">{{$ctrl.nonheap.percentUsed}}%</div>\n\t\t\t</div>\n\t\t</td>\n\t</tr>\n\t<tr>\n\t\t<td>Initial Non-Heap</td>\n\t\t<td>{{$ctrl.nonheap.init | humanBytes:$ctrl.nonheap.unit }}</td>\n\t</tr>\n\t<tr>\n\t\t<td>Maximum Non-Heap</td>\n\t\t<td ng-show="$ctrl.nonheap.max > 0">{{$ctrl.nonheap.max | humanBytes:$ctrl.nonheap.unit }}</td>\n\t\t<td ng-show="$ctrl.nonheap.max <= 0">unbounded</td>\n\t</tr>\n</table>'},149:function(t,n){t.exports='<table class="table">\n    <tr>\n        <td rowspan="2">Http sessions</td>\n        <td>active</td>\n        <td ng-bind="$ctrl.sessions.active"></td>\n    </tr>\n    <tr>\n        <td>maximum</td>\n        <td ng-bind="$ctrl.sessions.max"></td>\n    </tr>\n</table>\n'}});