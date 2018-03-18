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

package gov.nist.secauto.swid.plugin.entry;

import gov.nist.swid.builder.resource.PathRelativizer;

import org.codehaus.plexus.components.io.fileselectors.FileInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public abstract class AbstractFileEntry implements FileEntry {

  public AbstractFileEntry() {
  }

  /**
   * Retrieve the base of the file's path, which can be excluded.
   * 
   * @return the base
   */
  protected abstract Path getBase();

  /**
   * Retrieve the full path to the file.
   * 
   * @return the filePath
   */
  public abstract Path getPath();

  /**
   * Retrieve the relative path of the file with the base removed.
   * 
   * @return the file's relative path
   */
  protected Path getRelativePath() {
    return getBase().relativize(getPath());
  }

  protected abstract String getOutputBase();

  @Override
  public String getOutputRelativePath() {
    StringBuilder builder = new StringBuilder();
    // Should be null if no base is provided or a path using the '/' sperator
    String base = getOutputBase();
    if (base != null) {
      builder.append(base);
      builder.append('/');
    }

    Path relativePath = getRelativePath();
    String relativePathString = relativePath.toString();
    String seperator = relativePath.getFileSystem().getSeparator();
    if (!"/".equals(seperator)) {
      relativePathString = relativePathString.replace(seperator, "/");
    }
    builder.append(relativePathString);
    return builder.toString();

  }

  public List<String> getRelativePathSegements(String swidTagPath) {
    return PathRelativizer.relativize(swidTagPath, getOutputRelativePath());
  }

  @Override
  public FileInfo asFileInfo() {
    return new ResourceFileInfo();
  }

  private class ResourceFileInfo implements FileInfo {

    @Override
    public String getName() {
      return AbstractFileEntry.this.getOutputRelativePath().toString();
    }

    @Override
    public InputStream getContents() throws IOException {
      return AbstractFileEntry.this.getInputStream();
    }

    @Override
    public boolean isFile() {
      return true;
    }

    @Override
    public boolean isDirectory() {
      return false;
    }

    @Override
    public boolean isSymbolicLink() {
      return false;
    }
  }
}
