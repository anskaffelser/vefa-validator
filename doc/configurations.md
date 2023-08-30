# Configurations

The validator supports multiple configurations to make it fit the needs.

Default values are defined in [ValidatorDefaults.java](https://github.com/difi/vefa-validator/blob/master/src/main/java/no/difi/vefa/validator/ValidatorDefaults.java).


## Features

### Expectation

Functionality for reading first comment in files to check errors and warnings against a predefined list of expected warnings and errors.  

* **feature.expectation** (default: false)
* **feature.suppress_notloaded** (default: false)
* **feature.nesting** (default: false)


## Pools

### Checker

* **pools.checker.size** (default: 250)
* **pools.checker.expire** (default: 1440 *minutes*)
