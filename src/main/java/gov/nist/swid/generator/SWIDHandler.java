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
 * SHALL NASA BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM, OR
 * IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.swid.generator;

import org.apache.maven.plugin.assembly.filter.ContainerDescriptorHandler;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.components.io.fileselectors.AllFilesFileSelector;
import org.codehaus.plexus.components.io.fileselectors.FileInfo;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Component(role = ContainerDescriptorHandler.class, hint = "swid-generator",
    instantiationStrategy = "per-lookup")
public class SWIDHandler implements ContainerDescriptorHandler {
  // @Parameter(defaultValue="${project}", readonly=true, required=true)
  @Requirement
  private MavenProject project;

  @Requirement
  private Logger logger;

  @Parameter(defaultValue = "${project.build.directory}/generated-swid", property = "tagOutputDir",
      required = true)
  private File tagOutputDirectory;

  @Parameter(defaultValue = "SWIDTAG", required = true)
  private String tagPath;

  private List<String> includes;
  private List<String> excludes;

  private List<Entity> entities;

  public SWIDHandler() {
  }

  public File getTagOutputDirectory() {
    return tagOutputDirectory == null
        ? new File(project.getBuild().getDirectory(), "generated-swid") : tagOutputDirectory;
  }

  public void setTagOutputDirectory(File tagOutputDirectory) {
    this.tagOutputDirectory = tagOutputDirectory;
  }

  public String getTagPath() {
    return tagPath == null ? "SWIDTAG" : tagPath;
  }

  public void setTagPath(String tagPath) {
    this.tagPath = tagPath;
  }

  @Override
  public void finalizeArchiveCreation(Archiver archiver) throws ArchiverException {
    SWIDProcessor processor;
    try {
      processor = new SWIDProcessor(project);
    } catch (Exception e) {
      throw new ArchiverException("unable to process SWID info", e);
    }

    FileSelector fileSelector = newFileSelector();
    archiver.getResources().forEachRemaining(r -> {
      try {
        PlexusIoResource resource = r.getResource();
        // We only care about file resources
        if (resource.isFile() && fileSelector.isSelected(resource)) {
          processor.addResourceEntry(r);
        }
      } catch (IOException e) {
        String error = new StringBuilder()
            .append(
                "Unable to determine if the resource meets the inclusion/exclusion criteria: ")
            .append(r.getName()).toString();
        logger.error(error, e);
        throw new ArchiverException(error, e);
      } catch (NoSuchAlgorithmException e) {
        String error = "Unable to generate hashes for the resource: " + r.getName();
        logger.error(error, e);
        throw new ArchiverException(error, e);
      }
    });

    if (entities != null) {
      for (Entity entity : entities) {
        processor.addEntity(entity);
      }
    }
    try {
      processor.process(archiver);
    } catch (IOException e) {
      throw new ArchiverException("unable to generate SWID tag", e);
    }
  }

  protected FileSelector newFileSelector() {
    FileSelector retval;
    if (includes == null || includes.isEmpty() || excludes == null || excludes.isEmpty()) {
      retval = new AllFilesFileSelector();
    } else {
      final IncludeExcludeFileSelector fileSelector = new IncludeExcludeFileSelector();
      if (includes != null && !includes.isEmpty()) {
        fileSelector.setIncludes(includes.toArray(new String[includes.size()]));
      }

      if (excludes != null && !excludes.isEmpty()) {
        fileSelector.setExcludes(excludes.toArray(new String[excludes.size()]));
      }
      retval = fileSelector;
    }
    return retval;
  }

  @Override
  public void finalizeArchiveExtraction(UnArchiver unarchiver) throws ArchiverException {
  }

  @Override
  public List<?> getVirtualFiles() {
    // short for no virtual files
    return null;
  }

  @Override
  public boolean isSelected(FileInfo fileInfo) throws IOException {
    // Always return true
    return true;
  }

}
