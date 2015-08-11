# Configurations

The validator supports multiple configurations to make it fit the needs.

Default values are defined in [ValidatorDefaults.java](https://github.com/difi/vefa-validator/blob/master/validator-core/src/main/java/no/difi/vefa/validator/ValidatorDefaults.java).

## Features

### Expectation

Functionality for reading first comment in files to check errors and warnings against a predefined list of expected warnings and errors.  

* feature.expectation (default: false)

## Pools

### Checker

* pools.checker.blockerWhenExhausted (default: GenericKeyedObjectPoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED)
* pools.checker.lifo (default: GenericKeyedObjectPoolConfig.DEFAULT_LIFO)
* pools.checker.maxTotal (default: GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL)
* pools.checker.maxTotalPerKey (default: GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL_PER_KEY)

### Presenter

* pools.presenter.blockerWhenExhausted (default: GenericKeyedObjectPoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED)
* pools.presenter.lifo (default: GenericKeyedObjectPoolConfig.DEFAULT_LIFO)
* pools.presenter.maxTotal (default: GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL)
* pools.presenter.maxTotalPerKey (default: GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL_PER_KEY)
