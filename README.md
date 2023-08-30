[![Docker](https://img.shields.io/docker/pulls/anskaffelser/validator.svg)](https://hub.docker.com/r/anskaffelser/validator/)

# VEFA Validator 3.x

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