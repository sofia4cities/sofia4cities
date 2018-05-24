server:
   port: ${serverPort}
   contextPath: ${serverContextPath}

spring:
   application.name: ${applicationName}

api.key: ${apiKey}

device:
   id: ${deviceId}
   rest:
     local:
        schema: ${deviceRestLocalSchema}
        network:
           interface: ${deviceLocalInterface}
           ipv6: ${deviceIpv6?c}
     basepath: ${serverContextPath}
   register.fail.retry.seconds: 60
   ping.interval.seconds: 10
   logic.main.loop.delay.seconds: 5

sofia2.digitaltwin.broker.rest: ${sofia2BrokerEndpoint}
