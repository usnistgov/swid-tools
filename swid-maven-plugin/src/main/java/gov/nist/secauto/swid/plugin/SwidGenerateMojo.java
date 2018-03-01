/**
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government.
 * Pursuant to title 17 United States Code Section 105, works of NIST employees are
 * not subject to copyright protection in the United States and are considered to
 * be in the public domain. Permission to freely use, copy, modify, and distribute
 * this software and its documentation without fee is hereby granted, provided that
 * this notice and disclaimer of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE. IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM, OR
 * IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.swid.plugin;

import gov.nist.secauto.swid.plugin.entry.FileEntry;
import gov.nist.secauto.swid.plugin.entry.resource.ResourceFileEntryProcessor;
import gov.nist.secauto.swid.plugin.generate.MavenProjectSwidBuilderHelper;
import gov.nist.secauto.swid.plugin.model.Entity;
import gov.nist.swid.builder.SWIDBuilder;
import gov.nist.swid.builder.ValidationException;
import gov.nist.swid.builder.output.XMLOutputHandler;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

/**
 * Goal which touches a timestamp file.
 *
 * @deprecated Don't use!
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class SwidGenerateMojo extends AbstractMojo {
    private static final List<String> DEFAULT_INCLUDES = Collections.singletonList("**/**");

    /**
     * the calling project.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * 
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}/classes/META-INF", property = "outputDir",
            required = true)
    private String outputDirectory;

    @Parameter(defaultValue = "SWIDTAG", required = true)
    private String tagPath;

    @Parameter(defaultValue = "swid-tag.xml", required = true)
    private String tagName;

    @Parameter
    private List<String> includes;
    @Parameter
    private List<String> excludes;

    @Parameter
    private List<Entity> entities;

    /**
     * Retrieve the Maven project.
     * 
     * @return the project
     */
    public MavenProject getProject() {
        return project;
    }

    /**
     * Get the set of inscluded files.
     * 
     * @return the includes
     */
    public String[] getIncludes() {
        List<String> retval;
        if (includes != null && !includes.isEmpty()) {
            retval = includes;
        } else {
            retval = DEFAULT_INCLUDES;
        }
        return retval.toArray(new String[retval.size()]);
    }

    /**
     * Get the set of exscluded files.
     * 
     * @return the excludes
     */
    public String[] getExcludes() {
        List<String> retval;
        if (excludes != null && !excludes.isEmpty()) {
            retval = excludes;
        } else {
            retval = Collections.emptyList();
        }
        return retval.toArray(new String[retval.size()]);
    }

    /**
     * Determine the path to the SWID tag file and create any directories in this path.
     * 
     * @return the tag file
     * @throws MojoExecutionException
     *             if an error occurred while creating directories
     */
    public File getSwidTagFile() throws MojoExecutionException {

        // create the output directory
        File retval = new File(outputDirectory);
        if (!retval.exists()) {
            if (!retval.mkdirs()) {
                throw new MojoExecutionException(
                        "Unable to create the directory specified by outputDirectory configuration parameter: "
                                + retval.getPath());
            }
        }

        // create the tagPath
        String tagPath = this.tagPath;
        retval = new File(retval, tagPath);
        if (!retval.exists()) {
            if (!retval.mkdirs()) {
                throw new MojoExecutionException(
                        "Unable to create the directory specified by tagPath configuration parameter: "
                                + retval.getPath());
            }
        }

        // now the tagFile
        String tagName = this.tagName;
        retval = new File(retval, tagName);
        return retval;
    }

//    private File getOutputDirectoryAsFile() {
//
//        File retval = new File(outputDirectory);
//        if (!retval.isAbsolute()) {
//            retval = new File(project.getBasedir(), outputDirectory);
//        }
//        return retval;
//    }

    /**
     * Execute the mojo.
     */
    public void execute() throws MojoExecutionException {

        String swidPath = "META-INF/" + tagPath + "/" + tagName;

        // determine what files to tag in the payload
        ResourceFileEntryProcessor processor
                = new ResourceFileEntryProcessor(project.getBuild().getOutputDirectory(), getLog());
        processor.setIncludes(getIncludes());
        processor.setExcludes(getExcludes());

        List<FileEntry> swidFiles;
        try {
            // TODO: check for duplicate entries
            swidFiles = processor.process(this.project.getBuild().getResources());
        } catch (IOException e) {
            throw new MojoExecutionException("An error occured while processing build files", e);
        }
        for (FileEntry entry : swidFiles) {
            getLog().debug("Found file: " + entry.getPath() + " as: " + entry.getOutputRelativePath());
        }

        // build the SWID Tag
        SWIDBuilder builder;
        try {
            builder = MavenProjectSwidBuilderHelper.buildSwidTag(project, swidPath, this.entities, swidFiles);
        } catch (NoSuchAlgorithmException e) {
            throw new MojoExecutionException("A requested hash algorithm is not supported.", e);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to read file while building a SWID tag.", e);
        }

        // Output the tag to a file
        File tagFile = getSwidTagFile();
        XMLOutputHandler xmlHandler = new XMLOutputHandler();
        try {
            xmlHandler.write(builder, new BufferedOutputStream(new FileOutputStream(tagFile)));
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to write SWID tag", e);
        } catch (ValidationException e) {
            throw new MojoExecutionException("The generated SWID tag was found to be invalid", e);
        }
    }

}
