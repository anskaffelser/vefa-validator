# Installing validator-web

The project 'validator-web' is a sample implementation making it easy to get started with a web interface for functionality provided by the validator.

Validator-web is not created to be an API, and future improvements may break behaviour of the current version.

It is the application made available by Difi to support developers testing files during implementation of supported standards.

## Getting a war

The war-file is compiled and available for download from Maven Central, but may also be created using Maven. For maven, this is the command:

```
mvn clean package
```

After compiling is the war file available in 'PROJECTROOT\validator-web\target'.

## Installing war

The war file is created to run on an application server. Difi uses Tomcat 7.

Please consult documentation of your application server.

## Configuration

The application is looking for this files for configuration:

* classpath:validator.properties
* file:validator.properties

Default settings and instructions on how to configure the application is provided by [validator.default.properties](https://github.com/difi/vefa-validator/blob/master/validator-web/src/main/resources/validator.default.properties).