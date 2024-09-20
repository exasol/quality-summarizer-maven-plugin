# quality-summarizer-maven-plugin

[![Build Status](https://github.com/exasol/quality-summarizer-maven-plugin/actions/workflows/ci-build.yml/badge.svg)](https://github.com/exasol/quality-summarizer-maven-plugin/actions/workflows/ci-build.yml)
[![Maven Central &ndash; Quality Summarizer Maven Plugin](https://img.shields.io/maven-central/v/com.exasol/quality-summarizer-maven-plugin)](https://search.maven.org/artifact/com.exasol/quality-summarizer-maven-plugin)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aquality-summarizer-maven-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.exasol%3Aquality-summarizer-maven-plugin)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aquality-summarizer-maven-plugin&metric=security_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Aquality-summarizer-maven-plugin)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aquality-summarizer-maven-plugin&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Aquality-summarizer-maven-plugin)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aquality-summarizer-maven-plugin&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=com.exasol%3Aquality-summarizer-maven-plugin)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aquality-summarizer-maven-plugin&metric=sqale_index)](https://sonarcloud.io/dashboard?id=com.exasol%3Aquality-summarizer-maven-plugin)

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aquality-summarizer-maven-plugin&metric=code_smells)](https://sonarcloud.io/dashboard?id=com.exasol%3Aquality-summarizer-maven-plugin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aquality-summarizer-maven-plugin&metric=coverage)](https://sonarcloud.io/dashboard?id=com.exasol%3Aquality-summarizer-maven-plugin)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aquality-summarizer-maven-plugin&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=com.exasol%3Aquality-summarizer-maven-plugin)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=com.exasol%3Aquality-summarizer-maven-plugin&metric=ncloc)](https://sonarcloud.io/dashboard?id=com.exasol%3Aquality-summarizer-maven-plugin)

This [Maven](https://maven.apache.org) plugin takes quality metrics produced by tools like [JaCoCo](https://www.jacoco.org/) and summarizes them in a single file during build.

The purpose of this file is to be picked up by a crawler from a CI build and is then written into a quality database. Note that the crawler and the database are both outside the scope of this Maven plugin.

## File Format

The target file format is described in a [JSON schema](https://schemas.exasol.com/project-metrics-0.2.0.json).

A typical file looks like this:

```json
{
  "project" : "my-project",
  "commit" : "a2464f0...",
  "date" : "2024-12-31T19:30:00Z",
  "coverage" : 0.92
}
```

## Information for Users

* [Changelog](doc/changes/changelog.md)

## Information for Developers

* [Dependencies](dependencies.md)
