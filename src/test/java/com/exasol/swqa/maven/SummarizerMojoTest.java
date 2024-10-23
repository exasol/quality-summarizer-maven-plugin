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

    @Test
    void extractCoverageFromTopLevelElement() throws MojoFailureException, IOException {
        assertThat(extractCoverageFromJaCoCoXML("""
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <!DOCTYPE report PUBLIC "-//JACOCO//DTD Report 1.1//EN" "report.dtd">
                <report name="Quality Summarizer Maven Plugin">
                    <sessioninfo id="hostname" start="1729610403849" dump="1729610408295" />
                    <package name="com/exasol/swqa/maven">
                        <class name="com/exasol/swqa/maven/SummarizerMojo" sourcefilename="SummarizerMojo.java">
                            <method name="&lt;init&gt;" desc="(Lorg/apache/maven/project/MavenProject;)V" line="43">
                                <counter type="INSTRUCTION" missed="1" covered="6" />
                                <counter type="LINE" missed="1" covered="3" />
                                <counter type="COMPLEXITY" missed="1" covered="1" />
                                <counter type="METHOD" missed="1" covered="1" />
                            </method>
                            <!-- ... -->
                            <counter type="INSTRUCTION" missed="20" covered="237" />
                            <counter type="BRANCH" missed="2" covered="3" />
                            <counter type="LINE" missed="5" covered="55" />
                            <counter type="COMPLEXITY" missed="2" covered="14" />
                            <counter type="METHOD" missed="1" covered="12" />
                            <counter type="CLASS" missed="1" covered="1" />
                        </class>
                        <sourcefile name="SummarizerMojo.java">
                            <line nr="43" mi="0" ci="2" mb="0" cb="0" />
                            <line nr="44" mi="0" ci="3" mb="0" cb="0" />
                            <!-- ... -->
                            <counter type="INSTRUCTION" missed="20" covered="237" />
                            <counter type="BRANCH" missed="2" covered="4" />
                            <counter type="LINE" missed="5" covered="55" />
                            <counter type="COMPLEXITY" missed="2" covered="14" />
                            <counter type="METHOD" missed="1" covered="12" />
                            <counter type="CLASS" missed="1" covered="1" />
                        </sourcefile>
                        <counter type="INSTRUCTION" missed="20" covered="237" />
                        <counter type="BRANCH" missed="2" covered="5" />
                        <counter type="LINE" missed="5" covered="55" />
                        <counter type="COMPLEXITY" missed="2" covered="14" />
                        <counter type="METHOD" missed="1" covered="12" />
                        <counter type="CLASS" missed="1" covered="1" />
                    </package>
                    <counter type="INSTRUCTION" missed="20" covered="237" />
                    <counter type="BRANCH" missed="0" covered="100" />
                    <counter type="LINE" missed="5" covered="55" />
                    <counter type="COMPLEXITY" missed="2" covered="14" />
                    <counter type="METHOD" missed="1" covered="12" />
                    <counter type="CLASS" missed="1" covered="1" />
                </report>
                """), equalTo(100.0F));
    }

    private String jacocoReport(final String xmlPrefix, final int missed, final int covered) {
        return """
                %s<report name="Example project">
                         <counter type="BRANCH" missed="%d" covered="%d"/>
                </report>
                """.formatted(xmlPrefix, missed, covered);
    }

    private float extractCoverageFromJaCoCoXML(final String jacocoReportContent)
            throws IOException, MojoFailureException {
        final Path xmlFile = tempDir.resolve("jacoco.xml");
        Files.writeString(xmlFile, jacocoReportContent);
        return testee().extractCoverageFromJaCoCoXML(xmlFile);
    }

    private SummarizerMojo testee() {
        return new SummarizerMojo(projectMock);
    }
}
