package com.exasol.swqa.maven;

import static com.exasol.mavenprojectversiongetter.MavenProjectVersionGetter.getCurrentProjectVersion;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.apache.maven.it.Verifier;
import org.junit.jupiter.api.*;
import com.exasol.mavenpluginintegrationtesting.MavenIntegrationTestEnvironment;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class SummarizerMojoIT {
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
    static void beforeAll()
    {
        testEnvironment = new MavenIntegrationTestEnvironment();
        testEnvironment.installPlugin(PLUGIN_JAR.toFile(), PLUGIN_POM.toFile());
    }

    @CsvSource({
            "10, 0, 0.0",
            "20, 80, 80.0",
            "20, 40, 66.7",
            "0, 100, 100.0",
            })
    @ParameterizedTest
    void testExtractPathCoverage(final int missed, final int covered, final float expectedPercentage)
            throws Exception {
        final Path baseTestDir = BASE_TEST_DIR.resolve("project-with-coverage");
        final Path targetDir = baseTestDir.resolve("target");
        final Path siteDir = prepareSiteDir(targetDir);
        createMetricsFile(siteDir, missed, covered);
        runMojo(baseTestDir);
        assertThat(Files.readString(targetDir.resolve("metrics.json")),
                equalTo("""
                        {
                            "coverage" : %.1f
                        }
                        """.formatted(expectedPercentage)));
    }

    private static Path prepareSiteDir(Path targetDir) {
        final Path siteDir = targetDir.resolve("site");
        siteDir.toFile().delete();
        siteDir.toFile().mkdirs();
        return siteDir;
    }

    private static void createMetricsFile(final Path siteDir, final int missed, final int covered)
            throws IOException {
        Files.write(siteDir.resolve("jacoco.xml"), """
                <report name="Example project">
                    <counter type="BRANCH" missed="%d" covered="%d"/>
                </report>
                """.formatted(missed, covered).getBytes());
    }

    private static void runMojo(final Path projectDir) throws Exception
    {
        final Verifier verifier = createMavenVerifier(projectDir);
        verifier.executeGoal(MAVEN_GOAL);
        verifier.verifyErrorFreeLog();
    }

    /**
     * Create a verifier that does not automatically clean the target directory.
     * <p>
     * We need to extract data from files in the target directory, so automatic clean-up would break
     * the integration test.
     * </p>
     * @param projectDir directory where the test Maven project resides
     * @return verifier instance
     */
    private static Verifier createMavenVerifier(Path projectDir) {
        final Verifier verifier = testEnvironment.getVerifier(projectDir);
        verifier.setAutoclean(false);
        return verifier;
    }
}
