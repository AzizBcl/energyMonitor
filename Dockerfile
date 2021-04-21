FROM openjdk:8-jre-alpine
WORKDIR /

ADD energyMonitor.jar energyMonitor.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "energyMonitor.jar"]
