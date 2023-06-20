FROM eclipse-temurin:17-jre

ADD target/vefa-validator /vefa-validator

ENTRYPOINT ["sh", "/vefa-validator/bin/run.sh"]