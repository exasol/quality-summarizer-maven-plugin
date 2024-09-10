package com.exasol.swqa.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The SummarizerMojo class is a Maven plugin goal that performs quality metric summarization.
 * <p>
 * It implements the Mojo interface and extends the AbstractMojo class. The goal "summarize" and runs in the default
 * phase "VERIFY".
 * </p>
 */
// [impl -> dsn~maven-plugin~1]
// [impl -> dsn~os-compatibility~1]
// [impl -> dsn~executed-during-verify-phase~1]
@Mojo(name = "summarize", defaultPhase = LifecyclePhase.VERIFY)
public class SummarizerMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    // [impl -> qs~failing-safely-when-summarization-breaks~1]
    public void execute() {
        try {
            summarize();
        } catch (final Exception exception) {
            getLog().warn("The following issue occurred during quality metric summarization: '" + exception.getMessage()
                    + "'. Continuing build since this step is optional.");
        }
    }

    private void summarize() throws MojoFailureException {
        final Path mavenTargetPath = prepareTargetDirectory();
        final Path jacocoXMLPath = mavenTargetPath.resolve("site").resolve("jacoco.xml");
        final float coverage = extractCoverageFromJaCoCoXML(jacocoXMLPath);
        writeSummaryFile(mavenTargetPath, coverage);
    }

    private Path prepareTargetDirectory() {
        return Path.of(project.getBuild().getDirectory());
    }

    // [impl -> dsn~extracting-code-coverage-from-ja-co-co-report~1]
    private float extractCoverageFromJaCoCoXML(final Path jacocoXMLPath) throws MojoFailureException {
        try {
            final Document document = getXMLDocument(jacocoXMLPath);
            final NodeList reportNodes = document.getElementsByTagName("report");
            final Element reportNode = (Element) reportNodes.item(0);
            final NodeList counterNodes = reportNode.getElementsByTagName("counter");
            for (int i = 0; i < counterNodes.getLength(); i++) {
                final Element counterNode = (Element) counterNodes.item(i);
                if ("BRANCH".equals(counterNode.getAttribute("type"))) {
                    return calculateBranchCoverage(counterNode);
                }
            }
            return 0.0f;
        } catch (final IOException | ParserConfigurationException | SAXException exception) {
            throw new MojoFailureException("Failed to extract coverage from the JaCoCo XML: " + jacocoXMLPath,
                    exception);
        }
    }

    private float calculateBranchCoverage(Element counterNode) {
        final int missedBranches = Integer.parseInt(counterNode.getAttribute("missed"));
        final int coveredBranches = Integer.parseInt(counterNode.getAttribute("covered"));
        final int allBranches = missedBranches + coveredBranches;
        float branchCoveragePercentage = coveredBranches * 100.0f / allBranches;
        getLog().debug("Branch coverage is " + branchCoveragePercentage + ". " + coveredBranches + " of " + allBranches
                + " covered.");
        return branchCoveragePercentage;
    }

    private static Document getXMLDocument(Path jacocoXMLPath)
            throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        final Document document = documentBuilder.parse(Files.newInputStream(jacocoXMLPath));
        document.getDocumentElement().normalize();
        return document;
    }


    // [impl -> dsn~metric-output-file~1]
    private void writeSummaryFile(Path mavenTargetPath, float coverage) throws MojoFailureException {
        final Path summaryFilePath = mavenTargetPath.resolve("metrics.json");
        getLog().debug("Writing quality summary file to '" + summaryFilePath + "'");
        final String summary = generateSummaryJSON(coverage);
        try {
            Files.write(summaryFilePath, summary.getBytes());
        } catch (IOException exception) {
            throw new MojoFailureException("Unable to write quality summary file: " + summaryFilePath, exception);
        }
    }

    // [impl -> dsn~writing-code-coverage-value~1]
    private static String generateSummaryJSON(final float coverage) {
        return """
                {
                    "coverage" : %.1f
                }
                """.formatted(coverage);
    }
}