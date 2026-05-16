FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -q -DskipTests

FROM tomcat:9-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*

RUN apt-get update -q && \
    apt-get install -y -q wget && \
    wget -q \
    "https://repo1.maven.org/maven2/javax/servlet/jstl/1.2/jstl-1.2.jar" \
    -O /usr/local/tomcat/lib/jstl-1.2.jar && \
    wget -q \
    "https://repo1.maven.org/maven2/taglibs/standard/1.1.2/standard-1.1.2.jar" \
    -O /usr/local/tomcat/lib/standard-1.1.2.jar && \
    apt-get remove -y wget && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/ai-resume-analyzer.war \
     /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]