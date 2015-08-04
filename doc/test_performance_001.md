# Performance test #001

## Machine

* OS: Windows 7 Enterprise
* CPU: Intel i7-3770 @ 3.40 GHz
* Memory: 20 GB
* Current software running: 2 instances of IntelliJ and more.

## File

* Size: 4,19 MB
* Validation result: 1 error, 5820 warnings
* Lines of XML: 118 602
* Document type: EHF, profile 04, invoice (IT10)

## Configuration

Validation artifacts as presented in [vefa-validator-conf](https://github.com/difi/vefa-validator-conf). Test case 2 distributes validation artifacts in a new way, but the files distributed (XSDs and XSLTs) are exactly the same in both cases.

## Test case 1: [Old validator](https://github.com/difi/vefa-validator-app)

* Version: Current version (HEAD)
* Modifications: Saxon-HE is upgraded to version 9.6.0-6 (same as test case 2).
* Triggering validation: Using validate-cli from IntelliJ.
* Validation: **1 validation**, 9 minutes 29 seconds
* Max heap size: 1,345 GB

![visualvm_enoro_4mb_1](https://cloud.githubusercontent.com/assets/126939/9065092/fea732d6-3acf-11e5-820e-3048c18ffc50.png)

## Test case 2: [This validator](https://github.com/difi/vefa-validator)

* Version: 2.0.0-RC1
* Modifications: None
* Triggering validation: New unit test for this test, essentially as presented in project readme. Fetching validation artifacts using production repository.
* Validation: **25 validations**, 4 minutes, 13 seconds (average: 10,12 seconds)
* Max heap size: 2,101 GB

![visualvm_enoro_4mb](https://cloud.githubusercontent.com/assets/126939/9065049/de30017c-3acf-11e5-959e-d28261ae4256.png)

## Result

**The new validator needs some more memory, but is ~56 times faster than the old validator.**
