# Getting Started

### Accessing the H2 Console
Once your application is running, you can access the H2 console in your browser by navigating to:

```
http://localhost:8080/h2-console
```

- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

### Running the app
```
curl -X POST http://localhost:8080/api/images/scrape
```
or
```
curl -X POST http://localhost:8080/api/images/scrape?url=yoururlofthewebsitewithpictures
```

```
curl -X GET "http://localhost:8080/api/images/open-images?color=%23383232"
```
it will return command to run in GitBash terminal, that will open all the links in the default browser on the windows OS.


additional commands:

```
curl -X GET "http://localhost:8080/api/images?color=%23FFFFFF"
```

get all scraped dominant colors
```
curl -X GET http://localhost:8080/api/images/colors
```

get all scraped dominant colors grouped
```
curl -X GET http://localhost:8080/api/images/color-groups
```


### Building 
```
mvn clean package
```

```
java -jar target/colorart-0.0.1-SNAPSHOT.jar
```