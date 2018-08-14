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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ResourceCollection {
  Map<String, ResourcePath> pathToResourcePathMap;
  Map<ResourcePath, ResourceEntry> resources;

  /**
   * Construct a new empty resource collection.
   */
  public ResourceCollection() {
    super();
    pathToResourcePathMap = new HashMap<>();
    resources = new TreeMap<>();
  }

  /**
   * Add a resource to the resource collection.
   * 
   * @param resource
   *          the resource to add
   */
  public void addResource(ResourceEntry resource) {
    ResourcePath key = new ResourcePath(resource.getPath());
    pathToResourcePathMap.put(key.getPath(), key);
    resources.put(key, resource);
  }

  public Collection<ResourceEntry> getResources() {
    return Collections.unmodifiableCollection(resources.values());
  }

  /**
   * Lookup a resource with a specific path.
   * 
   * @param path
   *          the path to lookup a resource for
   * @return a resource record or <code>null</code> if no record was found matching the path
   */
  public ResourceEntry getResource(String path) {
    ResourcePath key = pathToResourcePathMap.get(path);
    ResourceEntry retval;
    if (key != null) {
      retval = resources.get(key);
    } else {
      retval = null;
    }
    return retval;
  }

  /**
   * Retrieve a digest of all of the resources in the collection. This can be used to check if two
   * resource collections are equal.
   * 
   * @param algorithm
   *          the hash function to use to generate the digest of the collection
   * @return a digest based on the provided function
   * @throws NoSuchAlgorithmException
   *           if the requested has function is unsupported
   */
  public byte[] getMessageDigest(HashAlgorithm algorithm) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance(algorithm.getName());

    for (ResourceEntry data : resources.values()) {
      digest.update(HashUtils.toArray(data.getDigestValue(HashAlgorithm.SHA_512)));
    }
    return digest.digest();
  }
}
