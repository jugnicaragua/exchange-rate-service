FROM maven:3.6.1-jdk-8 AS SERVICE
RUN curl -fsSL -o /tmp/exchange-rate-client.zip https://github.com/jugnicaragua/exchange-rate-client/archive/master.zip \
    && unzip /tmp/exchange-rate-client.zip -d /tmp/ \
    && rm -f /tmp/exchange-rate-client.zip
RUN cd /tmp/exchange-rate-client-master \
    && mvn install \
    && rm -rf /tmp/exchange-rate-client-master
COPY pom.xml /tmp
COPY src /tmp/src/
WORKDIR /tmp
RUN mvn package -DskipTests

FROM openjdk:8
COPY --from=SERVICE /tmp/target/exchange-rate-service-*.jar /tmp/exchange-rate-service.jar
ENV APP_USER jugni
ENV APP_PASSWORD jugni
ENV PG_SERVER pg11
ENV EXCHANGE_RATE_DB_DATABASE exchangerate
ENV EXCHANGE_RATE_DB_USER exchangerate
ENV EXCHANGE_RATE_DB_PASSWORD exchangerate
ENV EXCHANGE_RATE_EMAIL_USER test
ENV EXCHANGE_RATE_EMAIL_PASSWORD test@test.com
EXPOSE 8080
WORKDIR /tmp
