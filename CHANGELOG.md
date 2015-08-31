# Changelog

## Next release

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
