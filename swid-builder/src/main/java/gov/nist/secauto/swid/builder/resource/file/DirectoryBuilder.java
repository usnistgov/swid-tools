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

package gov.nist.secauto.swid.builder.resource.file;

import gov.nist.secauto.swid.builder.resource.ResourceBuilder;
import gov.nist.secauto.swid.builder.resource.ResourceCollectionEntryGenerator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DirectoryBuilder
    extends AbstractFileSystemItemBuilder<DirectoryBuilder> {
  private Map<String, DirectoryBuilder> directoryMap = new LinkedHashMap<>();
  private List<ResourceBuilder> resources = new LinkedList<>();

  @Override
  public void reset() {
    super.reset();
  }

  public static DirectoryBuilder create() {
    return new DirectoryBuilder();
  }

  @Override
  public <T> void accept(T parentContext, ResourceCollectionEntryGenerator<T> creator) {
    creator.generate(parentContext, this);
  }

  /**
   * Retrieves the child resources.
   * 
   * @return the resources
   */
  public List<ResourceBuilder> getResources() {
    return Collections.unmodifiableList(resources);
  }

  /**
   * Retrieves the child resources that match the specified builder.
   * 
   * @param <T>
   *          the type of builder to filter on
   * @param clazz
   *          the builder to filter on
   * @return the matching resources
   */
  public <T extends ResourceBuilder> List<T> getResources(Class<T> clazz) {
    @SuppressWarnings("unchecked")
    List<? extends T> retval
        = resources.stream().filter(e -> clazz.isInstance(e.getClass())).map(e -> (T) e).collect(Collectors.toList());
    return Collections.unmodifiableList(retval);
  }

  /**
   * Retrieves or creates the named directory resource if it doesn't exist.
   * 
   * @param name
   *          the directory name
   * @return a directory resource
   */
  public DirectoryBuilder getDirectoryResource(String name) {
    DirectoryBuilder retval = directoryMap.get(name);
    if (retval == null) {
      retval = DirectoryBuilder.create();
      retval.name(name);
      directoryMap.put(name, retval);
      resources.add(retval);
    }
    return retval;
  }

  /**
   * Adds a new file resource to this directory.
   * 
   * @param filename
   *          the file name to add
   * @return the new FileBuilder
   */
  public FileBuilder newFileResource(String filename) {
    FileBuilder retval = FileBuilder.create();
    retval.name(filename);
    resources.add(retval);
    return retval;
  }

}
