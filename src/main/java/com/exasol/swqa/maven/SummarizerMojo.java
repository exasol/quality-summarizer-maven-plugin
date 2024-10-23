package com.exasol.swqa.maven;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * The SummarizerMojo class is a Maven plugin goal that performs quality metric summarization.
 * <p>
 * The goal "summarize" and runs in the default phase {@code VERIFY}.
 * </p>
 */
// [impl -> dsn~maven-plugin~1]
// [impl -> dsn~os-compatibility~1]
// [impl -> dsn~executed-during-verify-phase~1]
@Mojo(name = "summarize", defaultPhase = LifecyclePhase.VERIFY)
public class SummarizerMojo extends AbstractMojo {
    private final MavenProject project;

    @Parameter(property = "jacocoReport", required = false)
    private File jacocoReport;

    /**
     * Create a new instance of the {@link SummarizerMojo}. This is called by Maven and injects the project.
     * 
     * @param project current maven project
     */
    @Inject
    public SummarizerMojo(final MavenProject project) {
        this.project = project;
    }

    @Override
    // [impl -> qs~failing-safely-when-summarization-breaks~1]
    public void execute() {
        try {
            summarize();
        } catch (final Exception exception) {
            // We intentionally don't provide a stack trace here. This is expected fail-safe behavior, and we should not
            // spam the log when failing safely.
            getLog().warn("The following issue occurred during quality metric summarization: '" + exception.getMessage()
                    + "' Continuing build since this step is optional.");
        }
    }

    private void summarize() throws MojoFailureException {
        final Path jacocoXMLPath = findJacocoXMLPath();
        final float coverage = extractCoverageFromJaCoCoXML(jacocoXMLPath);
        writeSummaryFile(coverage);
    }

    private Path findJacocoXMLPath() throws MojoFailureException {
        if (this.jacocoReport != null) {
            return this.jacocoReport.toPath().toAbsolutePath();
        }
        final Path targetDir = prepareTargetDirectory();
        final List<Path> jacocoXMLPaths = Stream
                .of("jacoco.xml", "site/jacoco.xml", "site/jacoco/jacoco.xml", "site/jacoco-aggregate/jacoco.xml")
                .map(targetDir::resolve).toList();
        final Optional<Path> jacocoXMLPath = jacocoXMLPaths.stream().filter(Files::exists).findFirst();
        if (jacocoXMLPath.isEmpty()) {
            throw new MojoFailureException("Jacoco XML report not found in the following locations: " + jacocoXMLPaths);
        }
        return jacocoXMLPath.get();
    }

    private Path prepareTargetDirectory() {
        return Path.of(project.getBuild().getDirectory());
    }

    // [impl -> dsn~extracting-code-coverage-from-jacoco-report~1]
    float extractCoverageFromJaCoCoXML(final Path jacocoXMLPath) throws MojoFailureException {
        getLog().info("Reading Jacoco report '" + jacocoXMLPath + "'");
        try {
            final Document document = getXMLDocument(jacocoXMLPath);
            final NodeList reportNodes = document.getElementsByTagName("report");
            final Element reportNode = (Element) reportNodes.item(0);
            final NodeList counterNodes = reportNode.getElementsByTagName("counter");
            for (int i = 0; i < counterNodes.getLength(); i++) {
                final Element counterNode = (Element) counterNodes.item(i);
                if ("BRANCH".equals(counterNode.getAttribute("type"))
                        && "report".equals(counterNode.getParentNode().getNodeName())) {
                    return calculateBranchCoverage(counterNode);
                }
            }
            return 0.0f;
        } catch (final IOException | ParserConfigurationException | SAXException exception) {
            throw new MojoFailureException("Failed to extract coverage from the JaCoCo XML: " + jacocoXMLPath,
                    exception);
        }
    }

    private float calculateBranchCoverage(final Element counterNode) {
        final int missedBranches = Integer.parseInt(counterNode.getAttribute("missed"));
        final int coveredBranches = Integer.parseInt(counterNode.getAttribute("covered"));
        final int allBranches = missedBranches + coveredBranches;
        final float branchCoveragePercentage = (coveredBranches * 100.0f) / allBranches;
        getLog().info("Branch coverage is " + branchCoveragePercentage + "%. " + coveredBranches + " of " + allBranches
                + " covered.");
        return branchCoveragePercentage;
    }

    private static Document getXMLDocument(final Path jacocoXMLPath)
            throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory documentBuilderFactory = createSecureDocumentBuilderFactory();
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        final Document document = documentBuilder.parse(Files.newInputStream(jacocoXMLPath));
        document.getDocumentElement().normalize();
        return document;
    }

    /**
     * Create a secure {@link DocumentBuilderFactory}.
     * <ul>
     * <li>XML secure processing enabled</li>
     * <li>Unnecessary features disabled</li>
     * </ul>
     *
     * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/jaxp/jaxp.html">Java API for XML
     *      Processing (JAXP) Security Guide</a>
     * @return a secure DocumentBuilderFactory instance
     * @throws ParserConfigurationException if a DocumentBuilderFactory cannot be created
     */
    private static DocumentBuilderFactory createSecureDocumentBuilderFactory() throws ParserConfigurationException {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        // While secure processing is on by default we have this here to be explicit:
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        // Jacoco reports contain a DOCTYPE, so we can't disallow DOCTYPEs using by setting
        // http://apache.org/xml/features/disallow-doctype-decl to true.
        // Instead we deactivate loading external DTDs.
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return documentBuilderFactory;
    }

    // [impl -> dsn~metric-output-file~1]
    private void writeSummaryFile(final float coverage) throws MojoFailureException {
        final Path summaryFilePath = prepareTargetDirectory().resolve("metrics.json");
        getLog().debug("Writing quality summary file to '" + summaryFilePath + "'");
        final String summary = generateSummaryJSON(coverage);
        try {
            Files.writeString(summaryFilePath, summary);
        } catch (final IOException exception) {
            throw new MojoFailureException("Unable to write quality summary file: " + summaryFilePath, exception);
        }
    }

    // [impl -> dsn~writing-code-coverage-value~1]
    private static String generateSummaryJSON(final float coverage) {
        // We are enforcing the US locale for numbers here in order to make sure that a dot will be used as decimal
        // separator.
        final String coverageAsUSLocaleString = String.format(Locale.US, "%.1f", coverage);
        return """
                {
                    "coverage" : %s
                }
                """.formatted(coverageAsUSLocaleString);
    }
}
