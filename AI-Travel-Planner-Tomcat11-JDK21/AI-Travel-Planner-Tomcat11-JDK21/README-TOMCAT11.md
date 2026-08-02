# Tomcat 11 / JDK 21 Setup

1. Eclipse: File > Import > Maven > Existing Maven Projects.
2. Select this project folder.
3. Right-click project > Maven > Update Project.
4. Properties > Project Facets:
   - Java 21
   - Dynamic Web Module 6.1
5. Properties > Targeted Runtimes:
   - Apache Tomcat v11.0
6. Edit DatabaseConnection.java and set the MySQL password.
7. Run As > Run on Server.

URL:
http://localhost:8080/AI-Travel-Planner/

You may write Java, JSP, HTML, CSS, JavaScript and SQL files in Eclipse.
MySQL Server still runs as a separate program/service.
