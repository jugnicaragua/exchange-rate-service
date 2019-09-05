FROM postgres:11.3
ENV EXCHANGE_RATE_DB_USER=exchangerate
ENV EXCHANGE_RATE_DB_PASSWORD=exchangerate
ENV EXCHANGE_RATE_DB_DATABASE=exchangerate
COPY *.sh /docker-entrypoint-initdb.d/
RUN chmod 755 /docker-entrypoint-initdb.d/*.sh
