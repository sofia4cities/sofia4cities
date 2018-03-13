Sofia4Cities IoT Platform Quality
============================

In this section you can know more about some of the tools we use for maintain the quality of the platform.

## Unit and Integration Testing
All the projects that compose the platform has their own unit and integration tests.

As we use Spring Boot for development we use the capabilities the Testing framework capabilities ([link](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html)), specially:

- JUnit: The de-facto standard for unit testing Java applications)
- Spring Test & Spring Boot Test :Utilities and integration test support for Spring Boot applications) 
- Mockito: A Java mocking framework. 

## Code Quality
We do continuous inspection of the code, not only to show health of the platform  but also to highlight issues newly introduced. 

With a Quality Gate in place the development team fix the leak and improve code quality systematically.

We use SonarQube for this labour, we have a SonarQube Server hosted in [https://www.sofia4cities.com/sonar  ](https://www.sofia4cities.com/sonar) that monitorice each day the quality of the code of the platform.

## Continuous Integration and Continuous Delivery
Each night using CI & CD we generate and deploy a new version of the platform with the snapshot of the day-work.

We use Jenkins in order to:

- Compile the Maven projects of the platform (you can see the parent pom here: https://github.com/sofia4cities/sofia4cities/blob/master/pom.xml).
- Execute the JUnit Tests of the projects.
- Generate the dockerfiles of each module.
- Put up an instance of the platform with Docker Compose.

Of there are some errors the development team receives a mail with their problems.

You can see one of the jenkins_pipelines at [Jenkins Pipelines](jenkins_pipelines/) 

## Automated Tests of the UIs
We use [Katalon](https://www.katalon.com) as a automation tool for the Web ControlPanel. 
Katalon has a friendly UI for users to quickly create, execute and maintain tests without programming.
You can see one of the automated tests for Katalon at [ControlPanel-AutomatedTest](ControlPanel-AutomatedTest/)  
Here you can find an example of a report with a problem in one of the steps: [Report with error](ControlPanel-AutomatedTest/20180309_174733.pdf) and another with all steps correctly executed [Report OK](ControlPanel-AutomatedTest/20180313_193621.pdf)
