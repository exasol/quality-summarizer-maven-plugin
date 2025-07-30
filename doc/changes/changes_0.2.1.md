# Quality Summarizer Maven Plugin 0.2.1, released 2025-??-??

Code name: Fixed vulnerability CVE-2025-48924 in org.apache.commons:commons-lang3:jar:3.8.1:provided

## Summary

This release fixes the following vulnerability:

### CVE-2025-48924 (CWE-674) in dependency `org.apache.commons:commons-lang3:jar:3.8.1:provided`
Uncontrolled Recursion vulnerability in Apache Commons Lang.

This issue affects Apache Commons Lang: Starting withÂ commons-lang:commons-langÂ 2.0 to 2.6, and, from org.apache.commons:commons-lang3 3.0 beforeÂ 3.18.0.

The methods ClassUtils.getClass(...) can throwÂ StackOverflowError on very long inputs. Because an Error is usually not handled by applications and libraries, a 
StackOverflowError couldÂ cause an application to stop.

Users are recommended to upgrade to version 3.18.0, which fixes the issue.

Sonatype's research suggests that this CVE's details differ from those defined at NVD. See https://ossindex.sonatype.org/vulnerability/CVE-2025-48924 for details
#### References
* https://ossindex.sonatype.org/vulnerability/CVE-2025-48924?component-type=maven&component-name=org.apache.commons%2Fcommons-lang3&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2025-48924
* https://github.com/advisories/GHSA-j288-q9x7-2f5v

## Security

* #17: Fixed vulnerability CVE-2025-48924 in dependency `org.apache.commons:commons-lang3:jar:3.8.1:provided`

## Dependency Updates

### Test Dependency Updates

* Updated `com.exasol:maven-project-version-getter:1.2.0` to `1.2.1`
* Updated `commons-io:commons-io:2.17.0` to `2.20.0`
* Updated `org.junit.jupiter:junit-jupiter-params:5.11.3` to `5.13.4`
* Updated `org.mockito:mockito-junit-jupiter:5.14.2` to `5.18.0`
* Updated `org.slf4j:slf4j-api:2.0.16` to `2.0.17`
* Updated `org.slf4j:slf4j-jdk14:2.0.16` to `2.0.17`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:4.3.3` to `5.2.2`
* Updated `org.apache.maven.plugins:maven-plugin-plugin:3.15.0` to `3.15.1`
