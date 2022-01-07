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

package gov.nist.secauto.swid.plugin.entry.resource;

import gov.nist.secauto.swid.plugin.entry.AbstractFileEntryProcessor;
import gov.nist.secauto.swid.plugin.entry.FileEntry;
import gov.nist.secauto.swid.plugin.entry.FileFileEntry;
import gov.nist.secauto.swid.plugin.entry.FileSelectorPredicate;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResourceFileEntryProcessor
    extends AbstractFileEntryProcessor<Resource> {

  private IncludeExcludeFileSelector selector = new IncludeExcludeFileSelector();
  private final String buildOutputDirectory;

  /**
   * Construct a new entry processor.
   * 
   * @param buildOutputDirectory
   *          the directory where built classes are stored
   * @param log
   *          the Maven logger instance
   */
  public ResourceFileEntryProcessor(String buildOutputDirectory, Log log) {
    super(log);
    Objects.requireNonNull(buildOutputDirectory);
    this.buildOutputDirectory = buildOutputDirectory;
  }

  public void setIncludes(String[] includes) {
    selector.setIncludes(includes);
  }

  public void setExcludes(String[] excludes) {
    selector.setExcludes(excludes);
  }

  @Override
  public List<FileEntry> process(List<? extends Resource> resources) throws IOException {
    List<FileEntry> processedResources = super.process(resources);
    List<FileEntry> processedClasses = generateClassFileEntries();

    int size = processedResources.size() + processedClasses.size();
    List<FileEntry> retval;
    if (size == 0) {
      retval = Collections.emptyList();
    } else {
      retval = new ArrayList<>(size);
      retval.addAll(processedResources);
      retval.addAll(processedClasses);
    }
    return retval;
  }

  private List<FileEntry> generateClassFileEntries() throws IOException {
    getLog().debug("Processing classes: " + buildOutputDirectory);

    Path path = Paths.get(buildOutputDirectory);

    List<FileEntry> retval;
    if (path.toFile().exists()) {
      retval = Files.walk(path).filter(Files::isRegularFile).map(p -> new FileFileEntry(p, path))
          .filter(new FileSelectorPredicate(selector)).collect(Collectors.toList());
    } else {
      retval = Collections.emptyList();
    }
    return retval;
  }

  @Override
  protected Collection<? extends FileEntry> generateFileEntries(Resource resource) throws IOException {
    getLog().debug("Processing resource: " + resource.getDirectory());

    Path path = Paths.get(resource.getDirectory());

    List<FileEntry> retval;
    if (path.toFile().exists()) {
      retval = Files.walk(path).filter(Files::isRegularFile).map(p -> new ResourceFileEntry(resource, p))
          .filter(new FileSelectorPredicate(selector)).collect(Collectors.toList());
    } else {
      retval = Collections.emptyList();
    }
    return retval;
  }

}
