FROM openjdk:8
ADD target/newshacker.jar /app.jar
EXPOSE 8081
ENTRYPOINT [ "sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]