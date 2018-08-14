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

package gov.nist.secauto.swid.plugin.entry.archive;

import gov.nist.secauto.swid.plugin.entry.FileEntry;
import gov.nist.swid.builder.resource.PathRelativizer;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.components.io.fileselectors.FileInfo;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class ArchiveFileEntry implements FileEntry {

  private final ArchiveEntry archiveEntry;
  private final Artifact artifact;

  /**
   * Construct a new file entry based on archive data.
   * 
   * @param archiveEntry
   *          the archive metadata
   * @param artifact
   *          optional artifact metadata
   */
  public ArchiveFileEntry(ArchiveEntry archiveEntry, Artifact artifact) {
    Objects.requireNonNull(archiveEntry, "archiveEntry");
    this.archiveEntry = archiveEntry;
    this.artifact = artifact;
  }

  @Override
  public Long getSize() {
    PlexusIoResource resource = archiveEntry.getResource();

    File file = null;
    if (resource instanceof PlexusIoFileResource) {
      file = ((PlexusIoFileResource) resource).getFile();
    }

    return (file == null) ? null : file.length();
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return archiveEntry.getInputStream();
  }

  @Override
  public String getVersion() {
    String retval = null;
    if (artifact != null) {
      retval = artifact.getVersion();
    }
    return retval;
  }

  @Override
  public Path getPath() {
    PlexusIoResource resource = archiveEntry.getResource();

    File file = null;
    if (resource instanceof PlexusIoFileResource) {
      file = ((PlexusIoFileResource) resource).getFile();
    }

    return (file == null) ? null : file.toPath();
  }

  @Override
  public String getOutputRelativePath() {
    return archiveEntry.getName();
  }

  @Override
  public List<String> getRelativePathSegements(String swidTagPath) {
    return PathRelativizer.relativize(swidTagPath, getOutputRelativePath());
  }

  @Override
  public FileInfo asFileInfo() {
    PlexusIoResource resource = archiveEntry.getResource();
    return resource;
  }

}
