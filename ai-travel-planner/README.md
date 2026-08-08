# AI Travel Planner Backend

Compatible target:
- Java 11
- Eclipse IDE
- Apache Tomcat 10
- Jakarta Servlet (`jakarta.servlet.*`)
- MySQL

## Import into Eclipse as Maven project
1. File > Import.
2. Maven > Existing Maven Projects.
3. Choose this project folder.
4. Finish.
5. Right-click project > Maven > Update Project.
6. Add Apache Tomcat 10 as the runtime.
7. Edit `DatabaseConnection.java`.
8. Replace `YOUR_MYSQL_PASSWORD`.
9. Make sure the database follows `DATABASE_CONTRACT.md`.

## Dynamic Web Project users
You can copy:
- `src/main/java/com/travelplanner/...` into Java Resources/src/main/java.
- `src/main/webapp/WEB-INF/web.xml` into WebContent/WEB-INF/web.xml.

Also add MySQL Connector/J to:
- Project > Properties > Java Build Path > Libraries.
- Deployment Assembly, if it is not automatically deployed.

## Required frontend form parameter names

Register:
- fullName
- email
- password
- confirmPassword

Login:
- email
- password

Plan trip:
- startingCityId
- destinationCityId
- travelDate
- budget
- transportationId (optional)
- preference (`cheapest`, `fastest`, or `shortest`)

Save trip:
- routeId
- travelDate
- budget
- preference

Delete trip:
- tripId

## JSP files expected later
- login.jsp
- register.jsp
- dashboard.jsp
- recommendation.jsp
- trip-history.jsp
- error.jsp
