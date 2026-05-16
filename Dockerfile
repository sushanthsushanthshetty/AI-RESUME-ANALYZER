FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -q -DskipTests

FROM tomcat:9-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy JSTL jars directly into Tomcat lib
# to ensure they are always available
RUN apt-get update && apt-get install -y wget && \
    wget -q https://repo1.maven.org/maven2/javax/servlet/jstl/1.2/jstl-1.2.jar \
    -O /usr/local/tomcat/lib/jstl-1.2.jar && \
    wget -q https://repo1.maven.org/maven2/taglibs/standard/1.1.2/standard-1.1.2.jar \
    -O /usr/local/tomcat/lib/standard-1.1.2.jar

COPY --from=build /app/target/ai-resume-analyzer.war \
     /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]