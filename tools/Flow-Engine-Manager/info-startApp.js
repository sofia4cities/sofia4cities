
var ProgressBar = require('progress');

//var bar = new ProgressBar(':bar', { total: 50 });
var bar = new ProgressBar('  loading [:bar] :percent', {
    complete: '█', 
    incomplete: '▓',
    width: 40,
    total: 5
  });

/*var timer = setInterval(function () {
  bar.tick();
  if (bar.complete) {

    console.log("");
    console.log(infoDate+" - [info] Started PROXY: http://localhost:5050");  
    console.log(infoDate+" - [info] Started APP  : http://localhost:10000");
    console.log("");
    console.log("");

    clearInterval(timer);
  }
}, 100);*/



//Timer
var d = new Date();
var infoHour = d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds();
var infoDate = d.getDate() + "-" + (d.getMonth() + 1) + "-" + d.getFullYear() + " " + infoHour;

console.log("                              ");
console.log("Welcome to MOTOR DE FLUJOS SOFIA 2  [Nodejs-ApiRest] ");
console.log("==================================================== ");
console.log("                                                     ");
console.log(infoDate+" - [info] Nodejs-ApiRest  version: v1.0.0 ");
console.log(infoDate+" - [info] Node-RED        version: v0.14.16 ");
console.log(infoDate+" - [info] Express         version: v4.14.0 ");
console.log(infoDate+" - [info] Http-proxy      version: v1.15.1 ");
console.log(infoDate+" - [info] Progress        version: v1.1.8 ");
console.log(infoDate+" - [info] Started PROXY");  
console.log(infoDate+" - [info] Started APP");
console.log("");


module.exports ="info-startApp";