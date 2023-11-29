[![Docker](https://img.shields.io/docker/pulls/anskaffelser/validator.svg)](https://hub.docker.com/r/anskaffelser/validator/)

# VEFA Validator 3.x

This repository contains the code of the validation library for Java of which may be used to validate document related to eProcurement. The library is intended to be included in your software where you need support for document validation, it is not prossible to perform validation by simply compiling the project.

This library does not contain validation rules for any of the eProcurement documents supported. If you have issues related to specific types of documents, please make sure to create those issues in the respective repository, e.g. [ehf-postaward-g3](https://github.com/anskaffelser/ehf-postaward-g3) for Post-Award documents or [eforms-sdk-nor](https://github.com/anskaffelser/eforms-sdk-nor) for eForms.


## Getting started

Include dependency in your pom.xml:

```xml
<dependency>
  <groupId>no.difi.vefa</groupId>
  <artifactId>validator-core</artifactId>
  <version>3.0.0</version>
</dependency>
```

Start validating business documents:

```java
// Create a new validator using validation artifacts from DFØ.
Validator validator = ValidatorBuilder.newValidator().build();

// Validate business document.
Validation validation = validator.validate(Paths.get("/path/to/document.xml"));

// Print result of validation.
System.out.println(validation.getReport().getFlag());
```

The validator is expensive to create, one instance should be enough.