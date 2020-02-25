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
package gov.nist.secauto.swid.builder.resource;

import gov.nist.secauto.swid.builder.AbstractLanguageSpecificBuilder;
import gov.nist.secauto.swid.builder.ValidationException;
import gov.nist.secauto.swid.builder.resource.file.DirectoryBuilder;
import gov.nist.secauto.swid.builder.resource.file.FileBuilder;
import gov.nist.secauto.swid.builder.resource.firmware.FirmwareBuilder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractResourceCollectionBuilder<E extends AbstractResourceCollectionBuilder<E>>
    extends AbstractLanguageSpecificBuilder<E> {
  private Map<String, DirectoryBuilder> directoryMap;
  private List<ResourceBuilder> resources;

  public AbstractResourceCollectionBuilder() {
    super();
  }

  @Override
  public void reset() {
    super.reset();
    directoryMap = new LinkedHashMap<>();
    resources = new LinkedList<>();
  }

  /**
   * Creates a new file builder based on resource pointed to by a sequence of path segments.
   * 
   * @param pathSegments
   *          a sequence of path segements that represent a path to a resource
   * @return a new file builder representing the provided path
   */
  public FileBuilder newFileResource(List<String> pathSegments) {
    FileBuilder retval;
    String filename;
    if (pathSegments.size() > 1) {
      List<String> directoryPath = pathSegments.subList(0, pathSegments.size() - 1);
      DirectoryBuilder directoryBuilder = getDirectoryBuilder(directoryPath);
      filename = pathSegments.get(pathSegments.size() - 1);
      retval = directoryBuilder.newFileResource(filename);
    } else {
      filename = pathSegments.get(0);
      retval = FileBuilder.create();
      retval.name(filename);
      resources.add(retval);
    }
    return retval;
  }

  /**
   * Creates a new firmware resource, adding it to this resource collection.
   * 
   * @return the new firmware builder
   */
  public FirmwareBuilder newFirmwareResource() {
    FirmwareBuilder retval = FirmwareBuilder.create();
    resources.add(retval);
    return retval;
  }

  private DirectoryBuilder getDirectoryBuilder(List<String> directoryPath) {
    DirectoryBuilder retval = null;
    for (String name : directoryPath) {
      if (retval == null) {
        DirectoryBuilder dir = directoryMap.get(name);
        if (dir == null) {
          dir = DirectoryBuilder.create();
          dir.name(name);
          directoryMap.put(name, dir);
          resources.add(dir);
        }
        retval = dir;
      } else {
        retval = retval.getDirectoryResource(name);
      }
    }
    return retval;
  }

  /**
   * Retrieves the child resources that match the specified builder..
   * 
   * @param <T>
   *          the type of builder to filter on
   * @param clazz
   *          the builder to filter on
   * @return the matching resources
   */
  public <T extends ResourceBuilder> List<T> getResources(Class<T> clazz) {
    // List<T> retval = new LinkedList<>();
    // for (ResourceBuilder builder : resources) {
    // if (clazz.isInstance(builder)) {
    // retval.add((T)builder);
    // }
    // }
    @SuppressWarnings("unchecked")
    List<? extends T> retval
        = resources.stream().filter(e -> clazz.isInstance(e)).map(e -> (T) e).collect(Collectors.toList());
    return Collections.unmodifiableList(retval);
  }

  public List<ResourceBuilder> getResources() {
    return Collections.unmodifiableList(resources);
  }

  @Override
  public void validate() throws ValidationException {
    super.validate();
  }

}
