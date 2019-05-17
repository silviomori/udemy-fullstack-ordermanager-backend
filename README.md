# Order Manager

This is a **Full Stack** project develop based on Nelio Alves' Udemy course:  
https://www.udemy.com/spring-boot-ionic/  

This project put together some technologies, such as:

* **Backend**: [https://github.com/silviomori/udemy-ionic-ordermanager-backend](https://github.com/silviomori/udemy-ionic-ordermanager-backend)  
	* Java 8
	* Spring Boot 2.0.2
	* RESTful Web Services
	* JWT - JSON Web Tokens
	* Apache Tomcat Server
	* Maven
* **Frontend**: [https://github.com/silviomori/udemy-ionic-ordermanager-frontend](https://github.com/silviomori/udemy-ionic-ordermanager-frontend)
	* Ionic Framework
	* Angular Platform
	* Node.js
	* TypeScript

## Dependencies
### Configure Your Environment
- Java JDK 8: [https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- Spring Tools 4 for Eclipse: [https://spring.io/tools](https://spring.io/tools)

###  External Tools
- Apiary: [https://apiary.io/](https://apiary.io/)
- Astah UML [http://astah.net/](http://astah.net/)

## Project Building
After you have cloned this repository in your system, open STS and choose the workspace directory as being the folder containing the repository you have just cloned. In the example below, choose `workspace`:

- `/home/workspace/udemy-fullstack-ordermanager-backend`

Open the Maven project navigating on the menu bar to `File > Open Projects from File System...`.  
Click on `Directory` and select the folder where the code for this project is. In the example above, choose `udemy-fullstack-ordermanager-backend`.  
Then, click on `Select Folder` and `Finish`.

Wait until Maven has its dependencies downloaded.

Look for Lombok library in the Maven Dependencies, do a right-click on it and choose `Run As` and `Java Application`. That will launch the Lombok installer.  
Select the STS executable file in the IDEs section, then click on `Install / Update` and `Quit Installer`.  
Rebuild the project in STS to have all the compilation problems fixed.

## Launch the Application
Navigate on the project files to `br.com.technomori.ordermanager`, do a right-click on `OrderManagerApplication.java` and choose `Run As` and `Spring Boot App`.