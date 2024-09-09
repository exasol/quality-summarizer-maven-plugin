package com.exasol.swqa.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * The SummarizerMojo class is a Maven plugin goal that performs quality metric summarization.
 * It implements the Mojo interface and extends the AbstractMojo class.
 * The goal "summarize" and runs in the default phase "VERIFY".
 */
@Mojo(name = "summarize", defaultPhase = LifecyclePhase.VERIFY)
public class SummarizerMojo extends AbstractMojo {

    @Parameter(property = "breakBuildOnFailure", defaultValue = "true")
    private boolean breakBuildOnFailure;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            // TODO: Implementation
        } catch (Exception exception) {
            if (breakBuildOnFailure) {
                throw new MojoFailureException("Failed", exception);
            } else {
                getLog().error("Failed", exception);
            }
        }
    }
}

