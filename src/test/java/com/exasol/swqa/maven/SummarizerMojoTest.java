package com.exasol.swqa.maven;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SummarizerMojoTest {

    @Mock
    MavenProject projectMock;
    @TempDir
    Path tempDir;

    @CsvSource({ "10, 0, 0.0", "20, 80, 80.0", "20, 40, 66.666664", "0, 100, 100.0", })
    @ParameterizedTest
    // [utest -> dsn~extracting-code-coverage-from-jacoco-report~1]
    void extractCoverageFromJacocoXml(final int missed, final int covered, final float expectedPercentage)
            throws MojoFailureException, IOException {
        assertThat(extractCoverageFromJaCoCoXML(jacocoReport("", missed, covered)), equalTo(expectedPercentage));
    }

    String jacocoReport(final String xmlPrefix, final int missed, final int covered) {
        return """
                %s<report name="Example project">
                         <counter type="BRANCH" missed="%d" covered="%d"/>
                </report>
                """.formatted(xmlPrefix, missed, covered);
    }

    @Test
    void extractCoverageFromXmlWithDoctype() throws MojoFailureException, IOException {
        assertThat(extractCoverageFromJaCoCoXML(jacocoReport("""
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <!DOCTYPE report PUBLIC "-//JACOCO//DTD Report 1.1//EN" "report.dtd">
                """, 20, 80)), equalTo(80.0F));
    }

    @Test
    void extractCoverageFromXmlVersionTag() throws MojoFailureException, IOException {
        assertThat(extractCoverageFromJaCoCoXML(jacocoReport("""
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                """, 20, 80)), equalTo(80.0F));
    }

    float extractCoverageFromJaCoCoXML(final String jacocoReportContent) throws IOException, MojoFailureException {
        final Path xmlFile = tempDir.resolve("jacoco.xml");
        Files.writeString(xmlFile, jacocoReportContent);
        return testee().extractCoverageFromJaCoCoXML(xmlFile);
    }

    private SummarizerMojo testee() {
        return new SummarizerMojo(projectMock);
    }
}
