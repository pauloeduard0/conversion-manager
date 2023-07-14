FROM openjdk:17-oracle
EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} conversion-manager-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","conversion-manager-0.0.1-SNAPSHOT.jar"]