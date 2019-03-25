# MonitoringService

A RESTful SpringBoot + SpringMVC + MySql Kotlin microservice that monitors urls in the background and stores last 10 results.

## Setup
*Note: Several configuration options such as port and mysql credentials can be changed in `application.properties` file.*

* Get a MySQL server running on your machine, log in as a priviledged user and execute `scripts/reset_base.sql`. 

* Enter your priviledged credentials into `application.properties` and set `spring.jpa.hibernate.ddl-auto` to `update`. This will allow Spring Hibernate to create the database tables for you.

* Run `gradle build`. Gradle will download dependencies, build the project and run the test. This will also create tables in your database.

* (Optional) Change your credentials in `application.properties` to user credentials (only INSERT, SELECT, UPDATE, DELETE operations required and recommended) and set `spring.jpa.hibernate.ddl-auto` to `none`.

* Run `gradle bootRun`. Gradle will run the application. By default, the server will be listening on port `8080`.

## API
The service provides JSON REST API in the following ways:

Authorization - To authorize a user, include `Authorization` header with value `Bearer TOKEN` with a valid user token in your HTTP request.

GET `/endpoint` - Returns a list of endpoints for authorized user.

GET `/endpoint/name` - Returns endpoint `name` for authorized user.

POST `/endpoint/name` - Creates or update endpoint `name` for authorized user. Accepts `url` and `interval` data parameters. At least one parameter is required when updating an endpoint, both parameters are required when creating an endpoint.

DELETE `/endpoint/name` - Deleted endpoint `name` for authorized user.

GET `/endpoint/name/results` - Returns a list of (at most) last 10 monitoring results for endpoint `name` for authorized user.

GET `/endpoint/name/results/offset` - Returns `offset`th of the last 10 monitoring results for endpoint `name` for authorized user.