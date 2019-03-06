<p align="center">
  <a src='https://www.sofia4cities.com/'>
    <img src='docs/images/s4c_logo.png'/>
  </a>
</p>

Sofia4Cities Platform 
============================

Sofia4Cities is an multi-purpose, enterprise and open-source platform for building complete end-to-end solutions, connected applications, and smart products. 
The platform provides an open toolkit for the IoT/Smart product development that reduces associated cost, risks, and time-to-market.
If you want to have a quickview of what offers the platform you can see this video: [a View of the Capabilities of the Platform](http://sofia2.org/owncloud/public.php?service=files&t=15977406e85c0faf71977a29936026a0)
![](docs/images/video_quickview.png)

## Project Roadmap for 2018

You can view the roadmap of the Platform for year 2018 here: [Roadmap](docs/roadmap/) 
<p align="center">
  <a src='https://www.sofia4cities.com/'>
    <img src='docs/roadmap/Sofia4Cities-roadmap.png'/>
  </a>
</p>

## Technology

Sofia4Cities IoT Platform is built mainly on Java technology, developed as a  Spring-powered microservices architecture and deploy by Spring Boot.

You can discover the modules of the platform in the [Architecture Overview](docs/architecture-overview/) 

## Project Structure

The project follows this skeleton:

* [Client Libraries](client-libraries/) contains the source code of the SDKs for access the platform from different languages (Java, Javascript, Android, Python,...) 
*  [Contributions](contributions/) contains the open-source project that we have personalized.
*  [Docker-deployment](docker-deployment/) with the Dockerfiles, docker-compose.yml and scripts to generate images for all the modules.
*  [Docs](docs/) contains the open documentation of the platform.
*  [Config](config/) source code for everything related to the configuration of the platform (JPA Entities, initialization of the database, Services for the acccess to the configuration of the platform).
*  [Examples](examples/) contains different examples that help us to develop with and to extend the platform.
*  [Libraries](libraries/) source code to different utilities and services of the platform used on the differente deployable modules (mail, twitter, commons,...)
*  [Modules](modules/) source code of the different modules of the platform (API Manager, IoTBroker, Control Panel, ...)
* [Persistence](persistence/) source code for everything related to the persistence in the platform (Mongo as RI Persistence, abstraction services,...)
* [Quality](quality/) projects, configurations, reports for all quality labours, including automated tests for the ControlPanel UI
* [Security](security/) source code for everything related to the security in the platform. We include reference implementations based on ConfigDB.
 
 
  
## Getting started with the platform


For the quick start with Sofia4Cities platform you can:


* Compile and Execute the Plataform in the Development Environment [this way](docs/how-to-execute-windows)
* You can deploy Sofia4Cities into your own environment with Docker [following these steps](docs/how-to-execute-docker/README.md)

## Getting help

If you face some troubles with Sofia4Cities platform you can create an issue  in [Github Issues of the project](https://github.com/sofia4cities/sofia4cities/issues), 

## Documentation

Sofia4Cities documentation is a part of Sofia4Cities source code and is located in the [`docs/`](docs) folder. 

## How to contribute

To contribute to Sofia4Cities platform, please visit [How to contribute](docs/how-to-contribute/README.md) guide.

## Where to report issues

You can report an issue by creating a corresponding ticket in [Github Issues of the project](https://github.com/sofia4cities/sofia4cities/issues), 

## License

Sofia4Cities platform is licensed under [Apache Software License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

See [`LICENSE`](LICENSE-full) and [`copyright.txt`](copyright.txt) for details.

## Thanks to

As a open source project, Sofia4Cities is using the [Open Source license of jProfiler (Java Profiler)](https://www.ej-technologies.com/products/jprofiler/overview.html)
![](https://www.ej-technologies.com/images/product_banners/jprofiler_medium.png)
