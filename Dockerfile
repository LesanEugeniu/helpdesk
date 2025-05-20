FROM amazoncorretto:21

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

CMD apt-get update -y

CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "app.jar"]
