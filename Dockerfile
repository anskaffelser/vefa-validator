FROM maven:3-jdk-8

ADD . $MAVEN_HOME

RUN cd $MAVEN_HOME && mvn clean package && mv $MAVEN_HOME/target/vefa-validator /vefa-validator && rm -r $MAVEN_HOME

VOLUME /src

ENTRYPOINT ["sh", "/vefa-validator/bin/run.sh"]

