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

package gov.nist.secauto.swid.plugin.entry;

import org.codehaus.plexus.components.io.fileselectors.FileInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface FileEntry {

  /**
   * Retrieve the path to the file.
   * 
   * @return the path
   */
  public Path getPath();

  /**
   * Retrieve the path to the file relative to where it is to be output.
   * 
   * @return the path
   */
  public String getOutputRelativePath();

  /**
   * Retrieve the relative path of to the SWID Tag.
   * 
   * @param swidTagPath
   *          the path to relativize
   * @return the file's relative path
   */
  public List<String> getRelativePathSegements(String swidTagPath);

  /**
   * Retrieve the file's size.
   * 
   * @return the size in bytes
   */
  public Long getSize();

  /**
   * Retrieve an input stream that can be used to read the file.
   * 
   * @return an input stream
   * @throws FileNotFoundException
   *           if the file does not exist
   * @throws IOException
   *           if the file cannot be read
   */
  public InputStream getInputStream() throws FileNotFoundException, IOException;

  /**
   * Retrieve the file's version.
   * 
   * @return the file's version or <code>null</code> if a version cannot be determined
   */
  public String getVersion();

  public FileInfo asFileInfo();
}
