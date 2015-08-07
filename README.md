# VEFA Validator 2.0

Current version: **2.0.0-RC1**


## Features

* **Very easy to use.**
* **Much faster** than the old validator.
* Supports parsing documents for **presentation**.
* Very **low footprint** in your code.
* **Pooling** of resources.
* Supports **different lifecycles** of validation artifacts.

Looking for a performance test?

* [Performance test #001](https://github.com/difi/vefa-validator/blob/master/doc/test_performance_001.md)
* [Performance test #002](https://github.com/difi/vefa-validator/blob/master/doc/test_performance_002.md)


## Getting started

Include dependency in your pom.xml:

```xml
<dependency>
	<groupId>no.difi.vefa</groupId>
	<artifactId>validator-core</artifactId>
	<version>2.0.0-RC1</version>
</dependency>
```

Start validating business documents:

```java
// Create a new validator using validation artifacts from Difi.
Validator validator = ValidatorBuilder.newValidator().build();

// Validate business document.
Validation validation = validator.validate(Paths.get("/path/to/document.xml"));

// Print result of validation.
System.out.println(validation.getReport().getFlag());
```

The validator is expensive to create, one instance should be enough.


## Validation artifacts

Artifacts may be found here:

* Production (current): [http://vefa.difi.no/validator/repo/](http://vefa.difi.no/validator/repo/)
* Test (upcoming): [https://test-vefa.difi.no/validator/repo/](https://test-vefa.difi.no/validator/repo/)

Difi does not guarantee the availability of the repositories containing validation artifacts. Local copy is always recommended for production environments.
