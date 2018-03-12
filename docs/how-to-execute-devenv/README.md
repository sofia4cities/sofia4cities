##Sofia4Cities DevEnv (Development Environment)
########################

## Info for Windows DevEnv:
### 1. How to install DevEnv in Windows and download Sofia4Cities code:
1. Go to [Releases](https://github.com/sofia4cities/dev-environment/master/releases/), inside the folder select the last version available.
2. Inside the version go to "windows" folder and download file Sofia4Cities.DevEnv.win.<version>.zip
3.  Unzip the file into a directory (in Windows we recommend to use C:\S4C_ENV\ or D:\S4C_ENV\) using as password to decompress "sofia2".
3. Go to the directory when you decompress the ZIP and execute start.bat, this create virtual unit S:
4. Go to **S:\scripts\setenv.bat** and config proxy values (if you are not behind a proxy you can delete these variables)
5. Go to **S:\tools\maven\conf\settings.xml** and edit <proxy> (of you are not behind a proxy you can comment this element)
6. Launch **S:\start.bat** and in the command line go to **S:\sources\**
	1. Execute >**git config --list**
	2. If you don´t have the property http.sslverify=false then you have to create it with **git config --global http.sslverify false**
	3. Also configure your name and email:
		1. git config --global user.name my_name
		2. git config --global user.email my_mail 
	4. Execute >**git clone https://github.com/sofia4cities/sofia4cities.git**. This creates directory s:\sources\sofia4cities\ with the master branch of repository sofia4cities
	(you need to have an account and access to this GIT repository as a prerequisite first)

### 2. How to compile and prepare Sofia4Cities with DevEnv in Windows 
1. (If you don´t have another command line) Launch S:\start.bat, in the command line created go to S:\sources\sofia4cities\ 
	1. Execute >**mvnnoTest clean install** to check all the code compiles (the first time you execute this task it downloads all libraries needed from Maven repositories so it can need a time).
	3. Execute >**mvn eclipse:eclipse** in order to generate the Eclipse projects (if you launch the command from S:\sources\sofia4cities\ then the projects created have the dependency to other projects in Eclipse)
2. Execute **Eclipse IDE** with command **S:\scripts\eclipse.bat**:
	1. In File>Switch Workspace make sure you are using workspace **S:\tools\eclipse_workspace\**
	2. In Windows>Preferences>Java>Installed JRES select **s:\tools\jdk\** and select as default JRE
	3. In Windows>Preferences>Maven>User Settings select **s:\tools\maven\conf\settings.xml**
	4. In Windows>Preferences>General>Network Connections review you have correctly configured your proxy
	5. In File>Import>General>Existing Projects into Workspace select Root Directory **S:\sources\sofia2-s4c\** and import all projects.
3. Start Sofia2 ConfigDB with **S:\scripts\ConfigDB_start.bat**. We are going to create schemas, tables an data needed (when launched if all is OK you´ll have a command line opened with ConfigDB).
	1. Execute **S:\scripts\ConfigDB_browser.bat** to manage the database (use server host localhost, port 3306, username root and no password)
	2. Into the program verify there is a schema called sofia2_s4c. If it is not created, do it.
	3.  Launch **S:\start.bat**, in the command line go to **S:\sources\sofia4cities\config\init** and execute **mvn spring-boot:run**. This execute this project as Spring Boot and creates all the tables and master data in the ConfigDB. (you need to wait until the projects starts, then you can close with Ctrl+C)
	1. Into the program launched by ConfigDB_browser.bat refresh **sofia2_s4c** schema and verify the schema sofia2_s4c has a group of tables like ontology and data into it.


### 3. How to execute Sofia4Cities ControlPanel with DevEnv in Windows 
(Before you can execute Sofia2 you have to follow steps 1 and s described before)

1. (If you don´t have virtual drive S:) Launch **start.bat** from the folder in which you decompress the DevEnv to create unit **S:**
2. Start **ConfigDB** with **S:\scripts\ConfigDB_start.bat** (if all is OK you´ll have a command line opened with ConfigDB):
3. Start **RealTime Database** with **S:\scripts\RTDB_start.bat** 
(if all is OK you´ll have a command line opened with RealTimeDB):
	1. Execute **S:\scripts\RTDB_console.bat**. In the console enter:
		1. show databases; 
		2. If it is not listed >use sofia2_s4c
3. Start **RealTime Database SQL-Service** with command **S:\scripts\RTDB_SQLServer_start.bat**
4. From a console created with S:\start.bat go to **S:\sources\sofia4cities\modules\control-panel** and execute >**mvn spring-boot:run**.
5. This execute ControlPanel in [http://localhost:18090/controlpanel/)(http://localhost:18080/controlpanel/).
6. You can access as developer with developer/changeIt! or as administrator with administrator/changeIt!.
7. That´s all! Try it!!!