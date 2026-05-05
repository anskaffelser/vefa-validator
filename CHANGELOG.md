# Changelog

## Next release

## 2.4.0

* Upgraded Java compile and runtime target from 8 to 21 (LTS).
* Replaced `<source>`/`<target>` with `<release>` in maven-compiler-plugin for stricter cross-compilation.
* Updated maven-compiler-plugin from 3.2 to 3.13.0.
* Updated Dockerfile base image from `openjdk:8u332-slim-bullseye` to `eclipse-temurin:21-jre`.
* Updated CI build matrix from `[8, 11, 17]` to `[17, 21]`; Docker image now built and pushed on JDK 21.
* Added `validator-build/scripts/rotate-keystore.sh` for rotating the self-signed ASiC-E signing certificate.
* Rotated expired self-signed keystore (`keystore-self-signed.jks`); fixes `Unable to verify signature` during `make validator` in rule repos such as `eforms-sdk-nor`.
* Extended `Makefile` with `help`, `test`, `rotate_keystore`, `docker_run` and `version` targets; removed obsolete `DOCKER_CLI_EXPERIMENTAL` flag.
* Updated Lombok `1.18.28` → `1.18.36` (Java 21 compatibility – `JCTree$JCImport.qualid` removed in Java 21).
* Updated JaCoCo `0.8.8` → `0.8.12` (Java 21 compatibility – class file major version 65 support).
* Updated dependencies.


## 2.3.0

* Docker base image changed from `openjdk:8u332-slim-bullseye` to `eclipse-temurin:17-jre`.
* Checker and renderer caches now use soft references (`.softValues()`) to allow GC to reclaim memory under pressure.
* Removed `BlockingURIResolver` from Saxon configuration.
* Removed two UBL declaration tests for invalid whitespace in element names (`invalidSpaces`, `invalidTabs`).
* Updated dependencies:
  * Saxon-HE `10.8` → `12.3`
  * Guice `5.1.0` → `7.0.0`
  * Guava `31.1-jre` → `32.1.2-jre`
  * SLF4J `1.7.36` → `2.0.7`
  * Logback `1.2.11` → `1.3.8`
  * commons-io `2.11.0` → `2.13.0`
  * commons-codec `1.15` → `1.16.0`
  * Gson `2.9.1` → `2.10.1`
  * Mockito `4.5.1` → `4.11.0`
  * Lombok `1.18.24` → `1.18.28`
  * TestNG `7.5` → `7.5.1`
  * Removed `commons-pool2` dependency.


## 2.1.0

* Support for PEPPOL BIS/EHF Billing 3.0.
* Changed visibility of no.dfo.anskaffelser.vefa.validator.ValidationImpl (earlier Validation).
* Allow multiple directories when using DirectorySource and SimpleDirectorySource.
* Refactored ValidatorBuilder to use ValidatorPlugin to declare functionality.
* Nested validation for document types supporting embedded content (currently SBDH and ASiC-E).
* Introducing flags FUTURE_ERROR (not currently in use) and UNKNOWN.
* Allow overriding properties used during validation per validation.
* Adding EspdDeclaration.
* Support for ASiC-E.
* Support for triggers.
* Support for namespace in single quotes.
* Support for test scope.
* Support for unit tests.
* Support for infourl.


## 2.0.2

* Rewrite of Declaration.
* Rewrite of Expectation.
* api.no.dfo.anskaffelser.vefa.validator.Document.getDeclaration() returns a string, not an object.
* Fixing methods in ValidatorBuilder returning void.
* Adding support for Piwik in sample application. Not turned on by default.
* Refactoring of validator-build.
* Adding SbdhDeclaration.
* Updating to [Saxon 9.7](http://www.saxonica.com/products/latest.xml#saxon9-7).
* Close Jimfs when closing the validator. [#22](https://github.com/difi/vefa-validator/pull/22)


## 2.0.1

* Adding annotation XmlTransient to flagFilterer in api.no.dfo.anskaffelser.vefa.validator.Section. [#14](https://github.com/difi/vefa-validator/issues/14)
* XsltChecker changed name to SvrlXsltChecker.
* Changed signature of api.no.dfo.anskaffelser.vefa.validator.Source.createInstance(...).
* Loading necessary modules when initiating JimFS. [#9](https://github.com/difi/vefa-validator/issues/9)


## 2.0.0

* Allow tracing successful tests.
* Adding FlagType.SUCCESS in API.
* Added more logging.
* Updating version of no.difi.commons:commons-schematron.
* Functionality to sign validation artifacts defined by parameters.
* Fixing UTF-8 thing in presentation of rendered documents in sample application.
* Allow override of implementations of checker og renderer to use.


## 2.0.0-RC2

* DirectorySourceInstance looks for artifacts.xml in directory.
* Testing and fixing of detection of CustomizationID and ProfileID.
* Functionality for configuring validator.
* Supporting parameters for stylesheets.
* Better defaults in the sample application (validator-web).
* ValidatorException is moved from no.dfo.anskaffelser.vefa.validator to no.dfo.anskaffelser.vefa.validator.api.
* Cleaner use of exceptions in exposed classes.
* Renaming 'Presenter' and associated to 'Renderer' for better communication. 
* Moving no.dfo.anskaffelser.vefa.validator.api to module validator-api.
* More Javadoc in code.


## 2.0.0-RC1

Initial publishing of validator.
