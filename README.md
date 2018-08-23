# CommonUtils
This is a library I use inside many of my Android projects (almost all of them). It includes many useful classes to comply Material Desing standards, useful classes and more.

## Setup
I suggest to include this library as a Git submodule (or just clone it) into your project and then add it as a Gradle module. Your `settings.gradle` file should look something like this:

```
include ':app', ':CommonUtils'
project(':CommonUtils').projectDir = new File('./CommonUtils/utils')
```

## Issues
If the issue is under the `com.gianlu.commonutils` package this is the right place to create the issue, if it belongs to another package email me or create an issue on the appropriate repository. 

## Versioning 
I usually edit this library from another project, therefore the commit messages might not be the most descriptive ones.
