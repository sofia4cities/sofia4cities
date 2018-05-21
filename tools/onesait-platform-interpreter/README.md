onesait platform interpreter for Apache Zeppelin 0.8.0



For generate interpreter:

• 	Generate jar with "mvn clean install" in this project (library zeppelin-interpreter is required for compilation, can be changed to maven dependency when 0.8.0 version will be release)

•	Create a folder called in interpreter folder (in zeppelin instalation directory) called “onesaitplatform" and paste the generated jar "zeppelin-onesait-platform-0.8.0.jar" inside

•	Edit the file zeppelin_site.xml and include in the property “zeppelin.interpreters” a new entry (comma separeted) called org.apache.zeppelin.onesaitplatform.OnesaitPlatformInterpreter

•	Restart Zeppelin

•	Create a new onesaitplatform interpreter
