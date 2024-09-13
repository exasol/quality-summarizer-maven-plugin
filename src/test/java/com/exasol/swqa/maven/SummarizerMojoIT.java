package com.exasol.swqa.maven;

import static com.exasol.mavenprojectversiongetter.MavenProjectVersionGetter.getCurrentProjectVersion;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.apache.maven.it.Verifier;
import org.junit.jupiter.api.*;
import com.exasol.mavenpluginintegrationtesting.MavenIntegrationTestEnvironment;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.logging.Logger;
import java.util.stream.Stream;

// [itest -> dsn~os-compatibility~1]]
class SummarizerMojoIT {
    private static final Logger LOGGER = Logger.getLogger(SummarizerMojoIT.class.getName());
    private static final String GROUP_ID = "com.exasol";
    private static final String ARTIFACT_ID = "quality-summarizer-maven-plugin";
    private static final String GOAL = "summarize";
    private static final String PLUGIN_VERSION = getCurrentProjectVersion();
    private static final String PLUGIN_JAR_FILE_NAME = ARTIFACT_ID + "-" + PLUGIN_VERSION + ".jar";
    private static final String MAVEN_GOAL = GROUP_ID + ":" + ARTIFACT_ID + ":" + PLUGIN_VERSION + ":" + GOAL;
    private static final Path PLUGIN_JAR = Path.of("target", PLUGIN_JAR_FILE_NAME).toAbsolutePath();
    // We need the flattened version of the POM file in order to include the generated POM.
    private static final Path PLUGIN_POM = Path.of(".flattened-pom.xml").toAbsolutePath();
    private static final Path BASE_TEST_DIR = Paths.get("src/test/resources").toAbsolutePath();

    private static MavenIntegrationTestEnvironment testEnvironment;

    @BeforeAll
    // [itest -> dsn~maven-plugin~1]
    static void beforeAll() {
        testEnvironment = new MavenIntegrationTestEnvironment();
        testEnvironment.installPlugin(PLUGIN_JAR.toFile(), PLUGIN_POM.toFile());
    }

    @CsvSource({ "10, 0, 0.0", "20, 80, 80.0", "20, 40, 66.7", "0, 100, 100.0", })
    @ParameterizedTest
    // [itest -> dsn~extracting-code-coverage-from-jacoco-report~1]
    // [itest -> dsn~metric-output-file~1]
    // [itest -> dsn~writing-code-coverage-value~1]
    void testExtractPathCoverage(final int missed, final int covered, final String expectedPercentage)
            throws Exception {
        final Path baseTestDir = BASE_TEST_DIR.resolve("project-with-coverage");
        final Path targetDir = baseTestDir.resolve("target");
        final Path siteDir = prepareSiteDir(targetDir);
        try {
            createMetricsFile(siteDir, missed, covered);
            // [itest -> qs~allowed-execution-time~1]
            runMojoWithMaxAllowedExecutionDuration(baseTestDir, Duration.of(500, MILLIS));
            assertThat(Files.readString(targetDir.resolve("metrics.json")), equalTo("""
                    {
                        "coverage" : %s
                    }
                    """.formatted(expectedPercentage)));
        } finally {
            cleanUpSiteDir(siteDir);
        }
    }

    private static Path prepareSiteDir(Path targetDir) {
        final Path siteDir = targetDir.resolve("site");
        try {
            deleteDirectoryRecursively(siteDir);
            Files.createDirectories(siteDir);
            return siteDir;
        } catch (IOException exception) {
            throw new UncheckedIOException(
                    "Failed to prepare a clean site directory " + siteDir + ": " + exception.getMessage(), exception);
        }
    }

    private static void deleteDirectoryRecursively(Path dir) throws IOException {
        if (Files.exists(dir)) {
            try(final Stream<Path> dirElements = Files.walk(dir)) {
                dirElements //
                        .map(Path::toFile)//
                        .sorted(Comparator.reverseOrder()) //
                        .forEach(file -> {
                            final boolean success = file.delete();
                            if (!success) { LOGGER.warning("Unable to delete directory entry: " + file);}
                        });
            }
        }
    }

    private static void createMetricsFile(final Path siteDir, final int missed, final int covered) throws IOException {
        Files.write(siteDir.resolve("jacoco.xml"), """
                <report name="Example project">
                    <counter type="BRANCH" missed="%d" covered="%d"/>
                </report>
                """.formatted(missed, covered).getBytes());
    }

    private static void runMojoWithMaxAllowedExecutionDuration(final Path projectDir, final Duration timeout)
            throws Exception {
        final Verifier verifier = createMavenVerifier(projectDir);
        final Instant before = Instant.now();
        verifier.executeGoal(MAVEN_GOAL);
        final Duration duration = Duration.between(Instant.now(), before);
        if (duration.compareTo(timeout) < 0) {
            throw new AssertionError(
                    "Allowed execution time for Maven step exceeded: " + duration + "(> " + timeout + ")");
        }
        verifier.verifyErrorFreeLog();
    }

    private void cleanUpSiteDir(final Path siteDir) throws IOException {
        deleteDirectoryRecursively(siteDir);
    }

    /**
     * Create a verifier that does not automatically clean the target directory.
     * <p>
     * We need to extract data from files in the target directory, so automatic clean-up would break the integration
     * test.
     * </p>
     * 
     * @param projectDir directory where the test Maven project resides
     * @return verifier instance
     */
    private static Verifier createMavenVerifier(Path projectDir) {
        final Verifier verifier = testEnvironment.getVerifier(projectDir);
        verifier.setAutoclean(false);
        return verifier;
    }

    @Test
    // [itest -> qs~failing-safely-when-summarization-breaks~1]
    void testWhenIgnoreFailureIsSetThenMissingJaCoCoReportFileIsIgnored() {
        assertDoesNotThrow(() -> {
            final Path emptyProjectDir = BASE_TEST_DIR.resolve("empty-project");
            final Verifier verifier = createMavenVerifier(emptyProjectDir);
            verifier.executeGoal(MAVEN_GOAL);
            verifier.verifyErrorFreeLog();
            verifier.verifyTextInLog("The following issue occurred during quality metric summarization: "
                    + "'Failed to extract coverage from the JaCoCo XML");
            assertThat(emptyProjectDir.resolve("metrics.json").toFile(), not(anExistingFile()));
        });
    }
}