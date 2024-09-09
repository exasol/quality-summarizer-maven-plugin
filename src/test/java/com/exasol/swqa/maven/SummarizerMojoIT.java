package com.exasol.swqa.maven;

import static com.exasol.mavenprojectversiongetter.MavenProjectVersionGetter.getCurrentProjectVersion;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.apache.maven.it.Verifier;
import org.junit.jupiter.api.*;
import com.exasol.mavenpluginintegrationtesting.MavenIntegrationTestEnvironment;

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
    private static final Path PLUGIN_POM = Path.of("pom.xml").toAbsolutePath();
    private static final Path BASE_TEST_DIR = Paths.get("src/test/resources").toAbsolutePath();

    private static MavenIntegrationTestEnvironment testEnvironment;

    @BeforeAll
    static void beforeAll()
    {
        testEnvironment = new MavenIntegrationTestEnvironment();
        testEnvironment.installPlugin(PLUGIN_JAR.toFile(), PLUGIN_POM.toFile());
    }

    @Test
    void testExtractPathCoverage() throws Exception {
        runMojo(BASE_TEST_DIR.resolve("project-with-coverage"));
        assertThat(Files.readString(Path.of("target/metrics.json")), equalTo("{\"coverage\":80.4}"));
    }

    private static void runMojo(final Path projectDir) throws Exception
    {
        final Verifier verifier = testEnvironment.getVerifier(projectDir);
        verifier.executeGoal(MAVEN_GOAL);
        verifier.verifyErrorFreeLog();
    }
}
