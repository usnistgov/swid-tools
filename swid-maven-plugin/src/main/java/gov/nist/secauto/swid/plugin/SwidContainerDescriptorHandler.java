/**
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.swid.plugin;

import gov.nist.secauto.swid.builder.SWIDBuilder;
import gov.nist.secauto.swid.builder.ValidationException;
import gov.nist.secauto.swid.builder.output.XMLOutputHandler;
import gov.nist.secauto.swid.plugin.entry.FileEntry;
import gov.nist.secauto.swid.plugin.entry.archive.ArchiveEntryFileEntryProcessor;
import gov.nist.secauto.swid.plugin.generate.MavenProjectSwidBuilderHelper;
import gov.nist.secauto.swid.plugin.model.Entity;

import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.assembly.filter.ContainerDescriptorHandler;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.components.io.fileselectors.FileInfo;
import org.codehaus.plexus.logging.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Component(role = ContainerDescriptorHandler.class, hint = "swid-generator", instantiationStrategy = "per-lookup")
public class SwidContainerDescriptorHandler implements ContainerDescriptorHandler {
  private static final List<String> DEFAULT_INCLUDES = Collections.singletonList("**/**");

  private static final String DEFAULT_TAG_PATH = "SWIDTAG";
  private static final String DEFAULT_TAG_NAME = "swid-tag.xml";

  /**
   * the calling project.
   */
  @Requirement
  private MavenProject project;

  @Requirement
  private Logger logger;

  /**
   * 
   * Location of the file.
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-swid", property = "outputDir", required = true)
  private File outputDirectory;

  @Parameter(defaultValue = DEFAULT_TAG_PATH, required = true)
  private String tagPath;

  @Parameter(defaultValue = DEFAULT_TAG_NAME, required = true)
  private String tagName;

  @Parameter
  private List<String> includes;
  @Parameter
  private List<String> excludes;

  @Parameter
  private List<Entity> entities;

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
   * Retrieves the directory to write the tag to when building it.
   * 
   * @return a file representing the tag output location
   */
  public File getTagOutputDirectory() {
    return outputDirectory == null ? new File(project.getBuild().getDirectory(), "generated-swid") : outputDirectory;
  }

  /**
   * Retrieves the path for where the tag is stored. This path is considered a subdirectory relative
   * to the software's installation lcoation.
   * 
   * @return the tagPath
   */
  public String getTagPath() {
    return tagPath == null ? DEFAULT_TAG_PATH : tagPath;
  }

  /**
   * @return the tagName
   */
  public String getTagName() {
    return tagName == null ? DEFAULT_TAG_NAME : tagName;
  }

  /**
   * Determine the path to the SWID tag file and create any directories in this path.
   * 
   * @return the tag file
   * @throws ArchiverException
   *           if an error occurred while creating directories
   */
  protected File getSwidTagFile() throws ArchiverException {

    // create the output directory
    File retval = getTagOutputDirectory();
    if (!retval.exists() && !retval.mkdirs()) {
      throw new ArchiverException(
          "Unable to create the directory specified by outputDirectory configuration parameter: " + retval.getPath());
    }
    //
    // // create the tagPath
    // String tagPath = getTagPath();
    // retval = new File(retval, tagPath);
    // if (!retval.exists()) {
    // if (!retval.mkdirs()) {
    // throw new ArchiverException(
    // "Unable to create the directory specified by tagPath configuration parameter:
    // "
    // + retval.getPath());
    // }
    // }

    // now the tagFile
    String tagName = getTagName();
    retval = new File(retval, tagName);
    return retval;
  }

  @Override
  public void finalizeArchiveCreation(Archiver archiver) throws ArchiverException {
    Log log = new DefaultLog(logger);
    // make a SWID tag

    // determine what files to tag in the payload
    ArchiveEntryFileEntryProcessor processor = new ArchiveEntryFileEntryProcessor(project, log);
    processor.setIncludes(getIncludes());
    processor.setExcludes(getExcludes());

    List<FileEntry> swidFiles;
    try {
      List<ArchiveEntry> entries = new LinkedList<>();
      archiver.getResources().forEachRemaining(ae -> entries.add(ae));
      swidFiles = processor.process(entries);
    } catch (IOException e) {
      throw new ArchiverException("An error occured while processing build files", e);
    }
    for (FileEntry entry : swidFiles) {
      log.debug("Found file: " + entry.getPath() + " as: " + entry.getOutputRelativePath());
    }

    // build the SWID Tag
    String archiveLocation = getTagPath() + File.separator + getTagName();
    SWIDBuilder builder;
    try {
      builder = MavenProjectSwidBuilderHelper.buildSwidTag(project, archiveLocation, this.entities, swidFiles);
    } catch (NoSuchAlgorithmException e) {
      throw new ArchiverException("A requested hash algorithm is not supported.", e);
    } catch (IOException e) {
      throw new ArchiverException("Unable to read file while building a SWID tag.", e);
    }

    // Output the tag to a file
    File tagFile = getSwidTagFile();

    try (OutputStream os = new BufferedOutputStream(new FileOutputStream(tagFile))) {
      XMLOutputHandler handler = new XMLOutputHandler();
      handler.write(builder, os);
    } catch (FileNotFoundException e) {
      throw new ArchiverException(e.getMessage());
    } catch (IOException e) {
      throw new ArchiverException(e.getMessage());
    } catch (ValidationException e) {
      throw new ArchiverException(e.getMessage());
    }

    log.info("Adding tag to archive: " + tagFile.getAbsolutePath());
    archiver.addFile(tagFile, archiveLocation);

  }

  @Override
  public void finalizeArchiveExtraction(UnArchiver unarchiver) throws ArchiverException {
    // Do nothing
  }

  @Override
  public List<?> getVirtualFiles() {
    // no virtual files
    return null;
  }

  @Override
  public boolean isSelected(FileInfo fileInfo) throws IOException {
    // accept all resources
    return true;
  }

}
