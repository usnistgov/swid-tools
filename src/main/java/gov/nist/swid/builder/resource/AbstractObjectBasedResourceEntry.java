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

package gov.nist.swid.builder.resource;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractObjectBasedResourceEntry<ENTRY_TYPE> implements ResourceEntry {
  private final Map<HashAlgorithm, List<Byte>> digests;
  private final ENTRY_TYPE resource;

  public AbstractObjectBasedResourceEntry(ENTRY_TYPE resource) throws NoSuchAlgorithmException, IOException {
    this.resource = resource;
    this.digests = processDigests(resource);
  }

  protected abstract EnumMap<HashAlgorithm, List<Byte>> processDigests(ENTRY_TYPE resource)
      throws NoSuchAlgorithmException, IOException;

  protected ENTRY_TYPE getResource() {
    return resource;
  }

  @Override
  public Map<HashAlgorithm, List<Byte>> getDigestValues() {
    return Collections.unmodifiableMap(digests);
  }

  @Override
  public List<Byte> getDigestValue(HashAlgorithm key) throws NoSuchAlgorithmException {
    List<Byte> retval = digests.get(key);
    if (retval == null) {
      throw new NoSuchAlgorithmException(key.getName());
    }
    return retval;
  }
}