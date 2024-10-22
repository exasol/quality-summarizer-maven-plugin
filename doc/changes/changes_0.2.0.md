# Quality Summarizer Maven Plugin 0.2.0, released 2024-??-??

Code name: Downgrade Maven Version

## Summary

This version downgrades the required Maven version from 3.8.8 to 3.8.7, allowing this plugin to work on GitHub action runners `windows-latest`. The release also allows reading jacoco report with doctype declaration.

## Bugfixes

* #9: Downgrade required Maven version
* #7: Allow reading jacoco report with doctype declaration.

## Dependency Updates

### Test Dependency Updates

* Updated `commons-io:commons-io:2.16.1` to `2.17.0`
* Updated `org.junit.jupiter:junit-jupiter-params:5.11.0` to `5.11.3`
* Added `org.mockito:mockito-junit-jupiter:5.14.2`
* Added `org.slf4j:slf4j-api:2.0.16`
* Added `org.slf4j:slf4j-jdk14:2.0.16`

### Plugin Dependency Updates

* Updated `org.apache.maven.plugins:maven-dependency-plugin:3.7.1` to `3.8.0`
