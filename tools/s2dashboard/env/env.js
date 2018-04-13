(function (window) {
  window.__env = window.__env || {};
  window.__env.socketEndpointConnect = '/dashboardengine/dsengine/solver';
  window.__env.socketEndpointSend = '/dsengine/solver';
  window.__env.socketEndpointSubscribe = '/dsengine/broker';
  window.__env.endpointSofia2ControlPanel = '/controlpanel';
  window.__env.endpointSofia2DashboardEngine = '/dashboardengine';
  window.__env.dashboardEngineUsername = 'administrator';
  window.__env.dashboardEnginePassword = 'changeIt!';
  window.__env.dashboardEngineLoginRest = '/loginRest';
  window.__env.dashboardEngineLoginRestTimeout = 5000;
  window.__env.enableDebug = false;
}(this));
