# Quality Summarizer Maven Plugin 0.1.0, released 2024-09-20

Code name: Path Coverage Extraction

## Summary

In this first release the Quality Summarizer Maven Plugin extracts the overall path coverage of a project from the JaCoCo report and writes it to a file called `metrics.json` in the `target` directory.

This file will then be picked up by a separate crawler from the CI build. That crawler is outside the scope of this project.

## Features

* #4: Added extraction of overall path coverage from JaCoCo report 

## Dependency Updates

### Test Dependency Updates

* Added `com.exasol:maven-plugin-integration-testing:1.1.3`
* Added `com.exasol:maven-project-version-getter:1.2.0`
* Added `commons-io:commons-io:2.16.1`
* Added `org.apache.maven.plugin-testing:maven-plugin-testing-harness:3.3.0`
* Added `org.codehaus.plexus:plexus-archiver:4.10.0`
* Added `org.junit.jupiter:junit-jupiter-params:5.11.0`

### Plugin Dependency Updates

* Added `com.exasol:error-code-crawler-maven-plugin:2.0.3`
* Added `com.exasol:project-keeper-maven-plugin:4.3.3`
* Added `io.github.zlika:reproducible-build-maven-plugin:0.16`
* Added `org.apache.maven.plugins:maven-clean-plugin:3.2.0`
* Added `org.apache.maven.plugins:maven-compiler-plugin:3.13.0`
* Added `org.apache.maven.plugins:maven-deploy-plugin:3.1.2`
* Added `org.apache.maven.plugins:maven-enforcer-plugin:3.5.0`
* Added `org.apache.maven.plugins:maven-failsafe-plugin:3.2.5`
* Added `org.apache.maven.plugins:maven-gpg-plugin:3.2.4`
* Added `org.apache.maven.plugins:maven-install-plugin:3.1.1`
* Added `org.apache.maven.plugins:maven-jar-plugin:3.3.0`
* Added `org.apache.maven.plugins:maven-javadoc-plugin:3.7.0`
* Added `org.apache.maven.plugins:maven-plugin-plugin:3.15.0`
* Added `org.apache.maven.plugins:maven-resources-plugin:3.3.1`
* Added `org.apache.maven.plugins:maven-site-plugin:3.12.1`
* Added `org.apache.maven.plugins:maven-source-plugin:3.2.1`
* Added `org.apache.maven.plugins:maven-surefire-plugin:3.2.5`
* Added `org.apache.maven.plugins:maven-toolchains-plugin:3.2.0`
* Added `org.basepom.maven:duplicate-finder-maven-plugin:2.0.1`
* Added `org.codehaus.mojo:flatten-maven-plugin:1.6.0`
* Added `org.codehaus.mojo:versions-maven-plugin:2.16.2`
* Added `org.itsallcode:openfasttrace-maven-plugin:2.2.0`
* Added `org.jacoco:jacoco-maven-plugin:0.8.12`
* Added `org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121`
* Added `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.2.0`
* Added `org.sonatype.plugins:nexus-staging-maven-plugin:1.7.0`
