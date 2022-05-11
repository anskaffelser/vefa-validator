FROM openjdk:8u332-slim-bullseye

ADD target/vefa-validator /vefa-validator

ENTRYPOINT ["sh", "/vefa-validator/bin/run.sh"]