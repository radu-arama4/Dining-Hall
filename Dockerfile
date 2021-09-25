FROM openjdk:8
MAINTAINER radu
ADD out/artifacts/Service1_jar/Service1.jar service1.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "service1.jar"]