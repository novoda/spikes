FROM openjdk:8-jre

ENV WIREMOCK_VERSION 2.26.0

RUN mkdir -p /var/wiremock/mappings
RUN wget https://repo1.maven.org/maven2/com/github/tomakehurst/wiremock-jre8-standalone/$WIREMOCK_VERSION/wiremock-jre8-standalone-$WIREMOCK_VERSION.jar \
    -O /var/wiremock/wiremock-jre8-standalone.jar

WORKDIR /var/wiremock

EXPOSE 8080

CMD java -jar /var/wiremock/wiremock-jre8-standalone.jar
