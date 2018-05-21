## How to execute the platform in your computer
#######################

(NOTE: This document refers to Github, if you are using Gitlab then you need to change repository info: https://sofia2-devops.westeurope.cloudapp.azure.com/gitlab/sofia2-projects/sofia2-s4c/)

### 1. How to install Dev Environment in Windows
This paragraph show you how to install the platform development environment in a Windows computer. This env has all the tools needed to execute the platform (JDK, Maven, MySQL, Mongo,...) so simplifies you develop with the platform.
1. Go to [Platform Downloads Page](http://sofia2.com/downloads/index.html), inside the folder select the last version available for Windows.
2. Download the version (with a name like onesaitPlatform_DevEnv_Win_(11052018).zip) to a local folder.
3.  Unzip the file into a directory (we recommend to use C:\S4C_ENV\ or D:\S4C_ENV\) using as password to decompress "sofia2".
3. Go to the directory when you decompress the ZIP and execute **start.bat**, this create virtual unit S:
4. Go to **S:\scripts\setenv.bat** and config proxy values in JAVA_OPTS (if you are not behind a proxy you can delete these variables)
5. If you are behind a proxy go to **S:\tools\maven\conf\settings.xml** and edit proxy (protocol, username, password) (if you are not behind a proxy you can comment this element)

### 2. How to download platform code with Dev Environment
1. If you have the DevEnv launch **S:\start.bat** and in the command line go to **S:\sources\**. In other case install git.
2. Execute >**git config --list**
3. If you don´t have the property http.sslverify=false then you have to create it with **git config --global http.sslverify false**
4. Also configure your name and email:
	1. git config --global user.name "my_name"
	2. git config --global user.email "my_mail"
5. (From **S:\sources\**) execute >**git clone https://github.com/sofia4cities/sofia4cities**. This creates directory **s:\sources\sofia4cities\** with the master branch of the platform repository.

### 3. How to compile and prepare platform with Dev Environment in Windows:
1. (If you don´t have another command line) Launch S:\start.bat, in the command line created go to **S:\sources\sofia4cities\**.
	1. Execute >**mvnnoTest clean install** to check all the code compiles (the first time you execute this task it downloads all libraries needed from Maven repositories so it can need a time).
	3. Execute >**mvn eclipse:eclipse** in order to generate the Eclipse projects (if you launch the command from S:\sources\sofia4cities\ then the projects created have the dependency to other projects in Eclipse)
2. Execute **Eclipse IDE** from command **S:\scripts\eclipse.bat**:
	1. In File>Switch Workspace make sure you are using workspace **S:\tools\eclipse_workspace\**
	2. In Windows>Preferences>Java>Installed JRES select **s:\tools\jdk\** and select as default JRE
	3. In Windows>Preferences>Maven>User Settings select **s:\tools\maven\conf\settings.xml**
	4. In Windows>Preferences>General>Network Connections review you have correctly configured your proxy
	5. In File>Import>General>Existing Projects into Workspace select Root Directory **S:\sources\sofia4cities\** and import all projects.
3. Start Sofia2 ConfigDB with **S:\scripts\ConfigDB_start.bat**. We are going to create schemas, tables an data needed (when launched if all is OK you´ll have a command line opened with ConfigDB).
	1. Execute **S:\scripts\ConfigDB_browser.bat** to manage the database (use server host localhost, port 3306, username root and no password)
	2. Into the program verify there is a schema called **sofia2_s4c**. If it is not created, do it.
	3.  Launch **S:\start.bat**, in the command line go to **S:\sources\sofia4cities\config\init** and execute **mvn spring-boot:run**. This execute this project as Spring Boot and creates all the tables and master data in the ConfigDB. The program close when create all tables.
	1. Into the program launched by ConfigDB_browser.bat refresh **sofia2_s4c** schema and verify the schema sofia2_s4c has a group of tables like ontology and data into it.

### 4. How to execute Platform modules with Dev Environment in Windows 
(Before you can execute Sofia2 you have to follow steps 1,2 and 3 described before)
1. (If you don´t have virtual drive S:) Launch **start.bat** from the folder in which you decompress the DevEnv to create unit **S:**
2. Start **ConfigDB** with **S:\scripts\ConfigDB_start.bat** (if all is OK you´ll have a command line opened with ConfigDB):
3. Start **RealTime Database** with **S:\scripts\RTDB_start.bat** 
(if all is OK you´ll have a command line opened with RealTimeDB):
	1. To view data you can execute **S:\scripts\RTDB_console.bat**. In the console enter:
		1. show databases;
		2. use sofia2_s4c;
		3. show collections;
4. Start **RealTime Database SQL-Service** with command **S:\scripts\RTDB_HTTPServer_start.bat**
5. **To execute ControlPanel**:
	1. From a console created with S:\start.bat go to **S:\sources\sofia4cities\modules\control-panel** and execute >**mvn spring-boot:run**.
	2. This execute ControlPanel in [http://localhost:18000/controlpanel/].
	3. You can access as developer with username/password **developer/changeIt!** or as administrator with **administrator/changeIt!**.

6. **To execute IoTBroker**:
	1. From a console created with S:\start.bat go to **S:\sources\sofia4cities\modules\iotbroker\sofia2-iotbroker-boot\** and execute >**mvn spring-boot:run**.
	2. You can access IoTBroker REST Gatway at  [http://localhost:19000/iotbroker/].

6. **To execute APIManager**:
	1. From a console created with S:\start.bat go to **S:\sources\sofia4cities\modules\api-manager\** and execute >**mvn spring-boot:run**.
	2. You can access API Manager Index Page at  [http://localhost:19100/api-manager/].

7. That´s all! Try it!!!