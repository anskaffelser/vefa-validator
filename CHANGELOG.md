# Changelog

## Next release

* Changes visibility of no.difi.vefa.validator.ValidationImpl (earlier Validation).

## 2.0.2

* Rewrite of Declaration.
* Rewrite of Expectation.
* no.difi.vefa.validator.api.Document.getDeclaration() returns a string, not an object.
* Fixing methods in ValidatorBuilder returning void.
* Adding support for Piwik in sample application. Not turned on by default.
* Refactoring of validator-build.
* Adding SbdhDeclaration.
* Updating to [Saxon 9.7](http://www.saxonica.com/products/latest.xml#saxon9-7).
* Close Jimfs when closing the validator. [#22](https://github.com/difi/vefa-validator/pull/22)

## 2.0.1

* Adding annotation XmlTransient to flagFilterer in no.difi.vefa.validator.api.Section. [#14](https://github.com/difi/vefa-validator/issues/14)
* XsltChecker changed name to SvrlXsltChecker.
* Changed signature of no.difi.vefa.validator.api.Source.createInstance(...).
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
* ValidatorException is moved from no.difi.vefa.validator to no.difi.vefa.validator.api.
* Cleaner use of exceptions in exposed classes.
* Renaming 'Presenter' and associated to 'Renderer' for better communication. 
* Moving no.difi.vefa.validator.api to module validator-api.
* More Javadoc in code.

## 2.0.0-RC1

Initial publishing of validator.
