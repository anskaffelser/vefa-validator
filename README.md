[![GitHub Packages](https://img.shields.io/badge/GitHub%20Packages-2.4.2-blue)](https://github.com/orgs/anskaffelser/packages)
[![Docker](https://img.shields.io/badge/ghcr.io-anskaffelser%2Fvalidator-blue)](https://ghcr.io/anskaffelser/validator)


# VEFA Validator 2.x

This repository contains the code of the validation library for Java which may be used to validate documents related to eProcurement. The library is intended to be included in your software where you need support for document validation – it is not possible to perform validation by simply compiling the project.

This library does not contain validation rules for any of the eProcurement documents supported. If you have issues related to specific types of documents, please make sure to create those issues in the respective repository, e.g. [ehf-postaward-g3](https://github.com/anskaffelser/ehf-postaward-g3) for Post-Award documents or [eforms-sdk-nor](https://github.com/anskaffelser/eforms-sdk-nor) for eForms.


## Features

* **Very easy to use.**
* Supports **rendering documents**.
* Very **low footprint** in your code.
* **Pooling** of resources.
* Supports **different lifecycles** of validation artifacts.
* **[Configurable](https://github.com/anskaffelser/vefa-validator/blob/master/doc/configurations.md)** to fit multiple sizes.


## Getting started

Artifacts are published to [GitHub Packages](https://github.com/orgs/anskaffelser/packages). You need a GitHub account and a personal access token with `read:packages` scope.

### 1. Add credentials

Add this to `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>anskaffelser</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

Create a token at [github.com/settings/tokens](https://github.com/settings/tokens) with scope `read:packages`.

### 2. Add repository and dependency

In your `pom.xml`:

```xml
<repositories>
  <repository>
    <id>anskaffelser</id>
    <url>https://maven.pkg.github.com/anskaffelser/maven</url>
  </repository>
</repositories>

<dependency>
  <groupId>no.difi.vefa</groupId>
  <artifactId>validator-core</artifactId>
  <version>2.4.2</version>
</dependency>
```

### 3. Start validating

```java
// Create a new validator using validation artifacts from DFØ.
Validator validator = ValidatorBuilder.newValidator().build();

// Validate business document.
Validation validation = validator.validate(Paths.get("/path/to/document.xml"));

// Print result of validation.
System.out.println(validation.getReport().getFlag());
```

Create the validator once and reuse it – it is expensive to initialize.
