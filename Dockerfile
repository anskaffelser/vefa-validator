FROM maven:3.3.9-jdk-8 AS mvn

ADD . $MAVEN_HOME

RUN cd $MAVEN_HOME \
 && mvn -B clean package \
 && mv $MAVEN_HOME/target/vefa-validator /vefa-validator



FROM java:8-jre-alpine

COPY --from=mvn /vefa-validator /vefa-validator

VOLUME /src /vefa-validator/workspace

ENTRYPOINT ["sh", "/vefa-validator/bin/run.sh"]