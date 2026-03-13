1.Populate src/main/resources/application.properties with the following values and input your password as the datasource password:

spring.application.name=demo

spring.datasource.url=jdbc:postgresql://localhost:5432/CurrencyTracker
spring.datasource.username=postgres
spring.datasource.password=   ***Enter your postgres user password***
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

2. In pgAdmin create a new database called CurrencyTracker
3. Run backend first, it will initially pull all the data from the API to have values, and if left running it will pull and check if values have changed every day on the 17th hour.
4. Run frontend with ng serve
5. Enjoy the app