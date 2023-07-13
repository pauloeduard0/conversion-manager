FROM openjdk:17-oracle
EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} workout-manager-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","conversion-manager-0.0.1-SNAPSHOT.jar"]
#pega esse .jar do pom
#<artifactId>-<version>.jar