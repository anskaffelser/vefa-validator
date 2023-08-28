FROM eclipse-temurin:17-jre AS target

ADD target /target

RUN mv /target/validator-core-*-full/vefa-validator /vefa-validator


FROM eclipse-temurin:17-jre

COPY --from=target /vefa-validator /vefa-validator

ENTRYPOINT ["sh", "/vefa-validator/bin/run.sh"]