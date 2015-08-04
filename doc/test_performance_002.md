# Performance test #001

## Machine

* OS: Windows 7 Enterprise
* CPU: Intel i7-3770 @ 3.40 GHz
* Memory: 20 GB
* Current software running: 2 instances of IntelliJ and more.

## File

* Size: 4,19 MB
* Lines of XML: 62 254
* Document type: EHF, profile 04, invoice (T10), 2.0

## Configuration

Validation artifacts as presented in [vefa-validator-conf](https://github.com/difi/vefa-validator-conf). Test case 2 distributes validation artifacts in a new way, but the files distributed (XSDs and XSLTs) are exactly the same in both cases.

## Test case 1: [Old validator](https://github.com/difi/vefa-validator-app)

* Version: Current version (HEAD)
* Modifications: Saxon-HE is upgraded to version 9.6.0-6 (same as test case 2).
* Triggering validation: Using validate-cli from IntelliJ.
* Validation: **1 validation**, 1 minutes 20 seconds
* Max heap size: 1,872 GB

![002_1](https://cloud.githubusercontent.com/assets/126939/9066327/3570abd8-3ad7-11e5-8875-65ceff1c2af6.png)


## Test case 2: [This validator](https://github.com/difi/vefa-validator)

* Version: 2.0.0-RC1
* Modifications: None
* Triggering validation: New unit test for this test, essentially as presented in project readme. Fetching validation artifacts using production repository.
* Validation: **25 validations**, 52 seconds (average: 2,08 seconds)
* Max heap size: 1,422 GB

![002_2](https://cloud.githubusercontent.com/assets/126939/9066338/4302c5d8-3ad7-11e5-8984-0662845968a7.png)


## Result

**The new validator needs some less memory, but is ~38 times faster than the old validator.**
