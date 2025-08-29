# Quality Summarizer Maven Plugin 0.2.1, released 2025-08-29

Code name: Fixes for vulnerability CVE-2025-48924

## Summary

This release fixes the following vulnerability:

### CVE-2025-48924 (CWE-674) in dependency `org.apache.commons:commons-lang3:jar:3.16.0:test`

Uncontrolled Recursion vulnerability in Apache Commons Lang.

This issue affects Apache Commons Lang: Starting with commons-lang:commons-lang 2.0 to 2.6, and, from org.apache.commons:commons-lang3 3.0 before 3.18.0.

The methods ClassUtils.getClass(...) can throw StackOverflowError on very long inputs. Because an Error is usually not handled by applications and libraries, a 
StackOverflowError could cause an application to stop.

Users are recommended to upgrade to version 3.18.0, which fixes the issue.

CVE: CVE-2025-48924
CWE: CWE-674

#### References

- https://ossindex.sonatype.org/vulnerability/CVE-2025-48924?component-type=maven&component-name=org.apache.commons%2Fcommons-lang3&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
- http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2025-48924
- https://github.com/advisories/GHSA-j288-q9x7-2f5v

## Security

* #17: Fixed vulnerability CVE-2025-48924 in dependency `org.apache.commons:commons-lang3:jar:3.8.1:provided`


## Dependency Updates

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.3` to `2.0.4`
* Updated `com.exasol:project-keeper-maven-plugin:4.3.3` to `5.2.3`
* Added `com.exasol:quality-summarizer-maven-plugin:0.2.0`
* Added `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.1`
* Removed `io.github.zlika:reproducible-build-maven-plugin:0.16`
* Added `org.apache.maven.plugins:maven-artifact-plugin:3.6.0`
* Updated `org.apache.maven.plugins:maven-clean-plugin:3.2.0` to `3.4.1`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.1.2` to `3.1.4`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.2.5` to `3.5.3`
* Updated `org.apache.maven.plugins:maven-gpg-plugin:3.2.4` to `3.2.7`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.1.2` to `3.1.4`
* Updated `org.apache.maven.plugins:maven-javadoc-plugin:3.7.0` to `3.11.2`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.12.1` to `3.21.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.2.5` to `3.5.3`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.6.0` to `1.7.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.16.2` to `2.18.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.12` to `0.8.13`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121` to `5.1.0.4751`
* Added `org.sonatype.central:central-publishing-maven-plugin:0.7.0`
* Removed `org.sonatype.plugins:nexus-staging-maven-plugin:1.7.0`
