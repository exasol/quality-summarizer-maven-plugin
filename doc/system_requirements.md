# System Requirement Specification (SRS) — Quality Summarizer Maven Plugin

## Introduction and Goals

### Features

#### Software Quality Metric Summarization From Build Products
`feat~quality-metric-summarization~1`

The summarizer aggregates software quality metric from the products of a build.

Rationale:

During build professional projects run a number of quality checkers, we want the results to be consolidated in a single location at the end of the build.

Needs: req

### Requirement Overview

The system is required to extract quality metrics from existing reports (such as JaCoCo) and consolidate them into a single file during a Continuous Integration (CI) build.

#### Extracting Code Coverage From JaCoCo Report
`req~extracting-code-coverage-from-ja-co-co-report~1`

The summarizer extracts the overall path coverage from a JaCoCo Report.

Covers:

* [`feat~quality-metric-summarization~1`](#software-quality-metric-summarization-from-build-products)

Needs: req

#### Summarization Output
`req~summarization-output~1`

The summarizer writes the consolidated result into a single JSON file called `metrics.json` with the schema defined under:

https://schemas.exasol.com/project-metrics-0.2.0.json

Covers:

* [`feat~quality-metric-summarization~1`](#software-quality-metric-summarization-from-build-products)

Needs: req

#### Summarizer Must Run in a Local Maven Build
`req~summarizer-must-run-in-a-local-maven-build~1`

The summarizer must be able to run in a local Maven build, independently of the underlying OS.

Covers:

* [`feat~quality-metric-summarization~1`](#software-quality-metric-summarization-from-build-products)

Needs: req


#### Summarizer Must Run in a GitHub CI Build
`req~summarizer-must-run-in-a-git-hub-ci-build~1`

The summarizer must be able to run in a CI build on a standard GitHub Ubuntu runner.

Covers:

* [`feat~quality-metric-summarization~1`](#software-quality-metric-summarization-from-build-products)

Needs: req

### Quality Goals

The system should ensure reliability in data extraction, accuracy in consolidating report data, and efficiency in performing operations during the CI build process.

## Stakeholders
CI/CD Engineers
: They will be using the system to automate the process of extracting and consolidating metrics during CI builds.

Quality Assurance Engineers
: They will be the end-users of the consolidated metrics report for quality checks and validation.

Project Managers
: They require the consolidated report for project oversight and to make informed decisions based on the quality metrics.

Software Developers
: They take the code coverage result to decide into which projects to invest more test effort.

## System Scope

### Business Context
The system is part of the CI pipeline, where after each code push, a CI build will trigger. As part of this build, the system will extract quality metrics from existing reports and consolidate the data into a single file. This behavior automates what would typically be a manual process, saving time, and ensuring consistency in report generation.

## Quality Requirements

### Quality Tree

#### Efficiency

##### Allowed Execution Time
`qg~allowed-execution-time~1`

The summarizer must complete the summarization step on a standard GitHub CI runner with Ubuntu in under half a second.

Rationale:

This step should not slow down the CI build by any relevant amount. Half a second longer should be acceptable for the overall build.

Needs: qs

#### Reliability

##### Failing Safely When Summarization Breaks
`qg~failing-safely-when-summarization-breaks~1`

CI Engineers must be able to tell the summarizer to not break the build in case the summarization fails.

Rationale:

While useful, the summarization is not a mission-critical part of the build and should not prevent the rest from completing.

Needs: qs

## Glossary
CI
: Continuous Integration

Quality Metrics
: Data points or measurements related to the quality of a software product.

JaCoCo
: A free code coverage library for Java, which adds coverage metrics to the build process.
