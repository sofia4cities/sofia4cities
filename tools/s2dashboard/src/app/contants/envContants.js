var env = {};

// Import variables if present (from env.js)
if(window && window.__env){
  Object.assign(env, window.__env);
}
else{//Default config
  env.socketEndpointConnect = '/dashboardengine/dsengine/solver';
  env.socketEndpointSend = '/dsengine/solver';
  env.socketEndpointSubscribe = '/dsengine/broker';
  env.endpointSofia2ControlPanel = '/controlpanel';
  env.endpointSofia2DashboardEngine = '/dashboardengine';
  env.enableDebug = false;
  env.dashboardEngineUsername = '';
  env.dashboardEnginePassword = '';
  env.dashboardEngineLoginRest = '/loginRest';
  env.sofia2RestUrl = 'http://rancher.sofia4cities.com/iotbroker/rest/client';
}

angular.module('s2DashboardFramework').constant('__env', env);
