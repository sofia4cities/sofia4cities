# How to compile Sofia4Cities IoT Platform

## Prerequisites
In order to compile  sources of the Platform you need to have in your system:
- Java: we recommend Java 1.8
- Maven: version 3

Besides this, if you want to launch our integration test you need to have executing:
- MongoDB: version 3.4 or more. 
- MySQL/MariaDB or other SQL database with JDBC driver.

## Basic compilation


## Using the Sofia4Cities Development Environment
In order to simplify the compilation and local use of the platform we provide a Development Environment (DevEnv) with all the tools needed to build, test and extend the platform.
This Env includes these tools:
	- eclipse: eclipse-jee-oxygen
	- git
	- jdk: jdk1.8
	- maven: apache-maven-3.5
	- mongo: MongoDB-3.4
	- mysql: MySQL-5

At the moment we provides this environment for Windows OS, in future releases we also includes Linux and Macs Env.

### What contains the DevEnv
The DevEnvironment is a ZIP that includes all the tools needed to develop with the platform.
This environment is composed of these folders:
- m2_repo: Path to the configured Repo for Maven3
- sources: path where (preferred) put your projects
- scripts: scripts  to start the environment
- tools: Tools included int the Environment:
	- eclipse: eclipse-jee-oxygen
	- git
	- jdk: jdk1.8
	- maven: apache-maven-3.5
	- mongo: MongoDB-3.4
	- mysql: MySQL-5

### Install of the Environment and first compilation
The first time you use the Environment you have to follow these steps:

1. Download last version of **Sofia2Open_Env.zip** from https://sofia2.com/downloads/Sofia4Cities_env_v5.0.zip
2. Unzip into a directory (we recommend to use C:\S2_ENV\ or D:\S2_ENV\)
3. Go to the directory when you decompress the ZIP and execute start.bat, this create virtual unit S:
4. Go to **S:\scripts\setenv.bat** and establish correctly proxy values (if you are not behind a proxy you can delete these variables)
5. Go to **S:\tools\maven\conf\settings.xml** and edit <proxy> (of you are not behind a proxy you can comment this element)
6. Launch S:\start.bat, in the command line go to S:\sources\
	1. Execute >git config --list
	2. If you don´t have the property http.sslverify=false then you have to create it with **git config --global http.sslverify false**
	3. Also config your name and email:
		1. git config --global user.name my_name
		2. git config --global user.email my_mail 
	4. Execute >**git clone https://github.com/sofia4cities/sofia4cities.git**. This creates directory s:\sources\sofia4cities\ with the master branch of repository sofia4cities
	5. From s:\sources\sofia4cities\ execute >**git checkout develop** (you can execute git branch to make sure you are in develop branch)
	6. From s:\sources\sofia4cities\ execute >**git** 
7. (If you don´t have another command line) Launch S:\start.bat, in the command line go to S:\sources\sofia4cities\ 
	1. Execute >**mvnnoTest clean install** to check all the code compiles (the first time you execute this task it downloads all libraries needed from Maven repositories so it can need a time).
	3. Execute >**mvn eclipse:eclipse** in order to generate the Eclipse projects (if you launch the command from S:\sources\sofia2-s4c\ then the projects created have the dependency to other projects in Eclipse)
7. Execute **Eclipse IDE** with command **S:\scripts\eclipse.bat**:
	1. In File>Switch Workspace make sure you are using workspace **S:\tools\eclipse_workspace\**
	2. In Windows>Preferences>Java>Installed JRES select **s:\tools\jdk\** and select as default JRE
	3. In Windows>Preferences>Maven>User Settings select **s:\tools\maven\conf\settings.xml**
	4. In Windows>Preferences>General>Network Connections review you have correctly configured your proxy
	5. In File>Import>General>Existing Projects into Workspace select Root Directory **S:\sources\sofia4cities\** and import all projects.
8. Start Sofia2 ConfigDB with **S:\scripts\ConfigDB_start.bat**. We are going to create schemas, tables an data needed (when launched if all is OK you´ll have a command line opened with ConfigDB).
	1. Execute **S:\scripts\ConfigDB_browser.bat** to manage the database (use server host localhost, port 3306, username root and no password)
	2. Into the program verify there is a schema called sofia2_s4c. If it is not created, do it.
	3.  Launch **S:\start.bat**, in the command line go to **S:\sources\sofia4cities\config\init** and execute **mvn spring-boot:run**. This execute this project as Spring Boot and creates all the tables and master data in the ConfigDB. (you need to wait until the projects starts, then it stop automatically)
	4. Into the program launched by ConfigDB_browser.bat refresh **sofia2_s4c schema** and verify the schema sofia2_s4c has a group of tables like client_connection and data into it.			
	5. Stop Sofia2 ConfigDB with **S:\scripts\ConfigDB_stop.bat**

### Execute Sofia4Cities IoT Platform
(Before you can execute Sofia4Cities you have to follow "Install of the Environment and first compilation")
1. (If you don´t have virtual drive S:) Launch **S:\start.bat** to create S:
2. Start **ConfigDB** with** S:\scripts\ConfigDB_start.bat** (if all is OK you´ll have a command line opened with ConfigDB):
3. Start **RealTime Database** with **S:\scripts\RTDB_start.bat** 
(if all is OK you´ll have a command line opened with RealTimeDB):
	1. Execute **S:\scripts\RTDB_console.bat**. In the console enter:
		1. show databases; 
		2. If it is not listed >use sofia2_s4c
)
4. Start **REST Interface 4 RealTime Database (Quasar)** with S:\scripts\RTDB_HTTPServer_start.bat
(if all is OK you´ll have a command line opened indicating "Server started listening on port 18200")

