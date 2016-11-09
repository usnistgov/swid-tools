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

package gov.nist.swid.generator.resource;

import gov.nist.swid.builder.resource.AbstractObjectBasedResourceEntry;
import gov.nist.swid.builder.resource.HashAlgorithm;
import gov.nist.swid.builder.resource.HashUtils;
import gov.nist.swid.builder.resource.PathRelativizer;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.EnumMap;
import java.util.List;

public class ArchiveEntryResourceEntry extends AbstractObjectBasedResourceEntry<ArchiveEntry> {

  private final Artifact artifact;

  public ArchiveEntryResourceEntry(ArchiveEntry entry, Artifact artifact) throws NoSuchAlgorithmException, IOException {
    super(entry);
    this.artifact = artifact;
  }

  @Override
  public String getPath() {
    return PathRelativizer.normalize(getResource().getName());
  }

  @Override
  public long getSize() {
    return getResource().getResource().getSize();
  }

  @Override
  protected EnumMap<HashAlgorithm, List<Byte>> processDigests(ArchiveEntry entry)
      throws NoSuchAlgorithmException, IOException {
    EnumMap<HashAlgorithm, List<Byte>> retval = new EnumMap<>(HashAlgorithm.class);
    // retrieve the entry to process
    PlexusIoResource resource = entry.getResource();
    retval.put(HashAlgorithm.SHA_256,
        HashUtils.toList(HashUtils.hash(HashAlgorithm.SHA_256, resource.getContents())));
    retval.put(HashAlgorithm.SHA_512,
        HashUtils.toList(HashUtils.hash(HashAlgorithm.SHA_512, resource.getContents())));
    return retval;
  }

  @Override
  public String getVersion() {
    String retval = null;
    if (artifact != null) {
      retval = artifact.getVersion();
    }
    return retval;
  }

}
