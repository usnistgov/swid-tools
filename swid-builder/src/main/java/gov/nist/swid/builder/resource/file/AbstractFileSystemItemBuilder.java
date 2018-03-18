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

package gov.nist.swid.builder.resource.file;

import static gov.nist.swid.builder.util.Util.requireNonEmpty;

import gov.nist.swid.builder.ValidationException;
import gov.nist.swid.builder.resource.AbstractResourceBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class AbstractFileSystemItemBuilder<E extends AbstractFileSystemItemBuilder<E>>
    extends AbstractResourceBuilder<E> {
  private Boolean key;
  private String root;
  private List<String> location;
  private String name;

  protected AbstractFileSystemItemBuilder() {
    super();
  }

  @Override
  public void reset() {
    super.reset();

    this.key = null;
    this.root = null;
    this.location = null;
    this.name = null;
  }

  public Boolean getKey() {
    return key;
  }

  public String getRoot() {
    return root;
  }

  public List<String> getLocation() {
    return (location == null ? Collections.<String>emptyList() : Collections.unmodifiableList(location));
  }

  public String getName() {
    return name;
  }

  /**
   * Sets the path representing the location and name of a filesystem resource to the provided value. This is a shortcut
   * for calling {@link #name(String)} and {@link #location(List)}
   * 
   * @param pathSegments
   *          one or more path segments
   * @return the same builder instance
   * @see #name(String)
   * @see #location(List)
   */
  @SuppressWarnings("unchecked")
  public E nameAndLocation(List<String> pathSegments) {
    Objects.requireNonNull(pathSegments, "pathSegments");
    if (pathSegments.size() < 2) {
      throw new IllegalArgumentException("two or more path segments must be provided");
    }

    this.name = pathSegments.get(pathSegments.size() - 1);
    this.location = pathSegments.subList(0, pathSegments.size() - 1);
    return (E) this;
  }

  /**
   * Sets the filesystem root of a filesystem resource to the provided value.
   * 
   * @param root
   *          the filesystem root value
   * @return the same builder instance
   */
  @SuppressWarnings("unchecked")
  public E root(String root) {
    requireNonEmpty(root, "root");
    this.root = root;

    return (E) this;
  }

  /**
   * Sets the path representing the location of a filesystem resource to the provided value, with the name omitted.
   * 
   * @param location
   *          a sequence of paths
   * @return the same builder instance
   */
  @SuppressWarnings("unchecked")
  public E location(List<String> location) {
    Objects.requireNonNull(location, "location");
    if (location.isEmpty()) {
      throw new IllegalArgumentException("location");
    }
    this.location = location;
    return (E) this;
  }

  /**
   * Sets the name of a filesystem resource to the provided value.
   * 
   * @param name
   *          the name value
   * @return the same builder instance
   */
  @SuppressWarnings("unchecked")
  public E name(String name) {
    requireNonEmpty(name, "name");
    this.name = name;

    return (E) this;
  }

  @Override
  public void validate() throws ValidationException {
    super.validate();
    validateNonEmpty("name", name);
  }

}
