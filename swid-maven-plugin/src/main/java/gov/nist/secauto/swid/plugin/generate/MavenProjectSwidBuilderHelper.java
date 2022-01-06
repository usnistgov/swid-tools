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

package gov.nist.secauto.swid.plugin.generate;

import gov.nist.secauto.swid.builder.EntityBuilder;
import gov.nist.secauto.swid.builder.KnownVersionScheme;
import gov.nist.secauto.swid.builder.Role;
import gov.nist.secauto.swid.builder.SWIDBuilder;
import gov.nist.secauto.swid.builder.resource.AbstractResourceCollectionBuilder;
import gov.nist.secauto.swid.builder.resource.HashAlgorithm;
import gov.nist.secauto.swid.builder.resource.file.FileBuilder;
import gov.nist.secauto.swid.plugin.entry.FileEntry;
import gov.nist.secauto.swid.plugin.model.Entity;

import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MavenProjectSwidBuilderHelper {
  private MavenProjectSwidBuilderHelper() {
    // disable construction
  }

  /**
   * Apply Maven project metadata to populate the core SWID Tag data elements.
   * 
   * @param builder
   *          the SWID Tag builder
   * @param project
   *          the Maven project instance
   * @return the SWID Tag builder instance
   */
  public static SWIDBuilder applyProjectMetadata(SWIDBuilder builder, MavenProject project) {
    String groupId = project.getGroupId();
    String artifactId = project.getArtifactId();
    String version = project.getVersion();

    String name = project.getName();
    if (name == null) {
      StringBuilder str = new StringBuilder();
      str.append(groupId);
      str.append('-');
      str.append(artifactId);
      str.append('-');
      str.append(version);
      name = str.toString();
    }
    builder.name(name);

    // no specific tag vaersion

    builder.version(version);

    if (version.endsWith("SNAPSHOT")) {
      builder.versionScheme(KnownVersionScheme.MULTIPART_NUMERIC_WITH_SUFFIX);
    } else {
      builder.versionScheme(KnownVersionScheme.MULTIPART_NUMERIC);
    }
    builder.tagId(generateTagId(project));

    return builder;
  }

  protected static String generateTagId(MavenProject project) {
    String groupId = project.getGroupId();
    String artifactId = project.getArtifactId();
    String version = project.getVersion();

    StringBuilder str = new StringBuilder();
    str.append(groupId);
    str.append('-');
    str.append(artifactId);
    str.append('-');
    str.append(version);

    // detect if this is a snapshot version
    if (version.endsWith("SNAPSHOT")) {
      // Append a date/time to make this snapshot unique
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'kkmmss");
      str.append("-");
      // Use UTC to prevent information leakage around where the project is built
      str.append(ZonedDateTime.now(ZoneId.of("UTC")).format(formatter));
    }
    return str.toString();
  }

  /**
   * Append the provided SWID Tag entities to the tag.
   * 
   * @param builder
   *          the SWID Tag builder
   * @param entities
   *          the list of entities to append
   * @return the SWID Tag builder instance
   */
  public static SWIDBuilder applyEntities(SWIDBuilder builder, List<Entity> entities) {

    for (Entity entity : entities) {
      EntityBuilder entityBuilder = EntityBuilder.create();
      if (entity.getName() != null) {
        entityBuilder.name(entity.getName());
      }

      if (entity.getRegid() != null) {
        entityBuilder.regid(entity.getRegid());
      }

      if (entity.getRoles() != null) {
        for (String roleValue : entity.getRoles()) {
          entityBuilder.addRole(Role.lookupByName(roleValue));
        }
      }
      builder.addEntity(entityBuilder);
    }
    return builder;
  }

  /**
   * Apply the provided file entries to the tag's payload.
   * 
   * @param builder
   *          the payload's builder
   * @param swidTagPath
   *          the output path of the tag
   * @param swidFiles
   *          the files to build payload entries for
   * @param hashAlgorithms
   *          the hash algorithms to use to calculate file digests
   * @throws NoSuchAlgorithmException
   *           if a hash algorithm is not supported
   * @throws IOException
   *           if an error occured while processing the payload files
   */
  public static void applyFileEnties(AbstractResourceCollectionBuilder<?> builder, String swidTagPath,
      List<FileEntry> swidFiles, Collection<HashAlgorithm> hashAlgorithms)
      throws NoSuchAlgorithmException, IOException {
    for (FileEntry entry : swidFiles) {
      FileBuilder fileBuilder = builder.newFileResource(entry.getRelativePathSegements(swidTagPath));

      String version = entry.getVersion();
      if (version != null) {
        fileBuilder.version(version);
      }

      Long size = entry.getSize();
      if (size != null) {
        fileBuilder.size(size);
      }

      for (HashAlgorithm algorithm : hashAlgorithms) {
        fileBuilder.hash(algorithm, entry.getInputStream());
      }
    }
  }

  /**
   * Build a SWID Tag using Maven project metadata and provided Entity and FileEntry information.
   * 
   * @param project
   *          the MavenProject to build the tag for
   * @param swidTagPath
   *          the output path of the tag
   * @param entities
   *          the SWID Tag entities to include
   * @param swidFiles
   *          the files to build payload entries for
   * @return a builder instance for the tag
   * @throws NoSuchAlgorithmException
   *           if a hash algorithm is not supported
   * @throws IOException
   *           if an error occured while processing the payload files
   */
  public static SWIDBuilder buildSwidTag(MavenProject project, String swidTagPath, List<Entity> entities,
      List<FileEntry> swidFiles) throws NoSuchAlgorithmException, IOException {
    SWIDBuilder builder = SWIDBuilder.create();

    MavenProjectSwidBuilderHelper.applyProjectMetadata(builder, project);

    if (entities != null && !entities.isEmpty()) {
      MavenProjectSwidBuilderHelper.applyEntities(builder, entities);
    }

    MavenProjectSwidBuilderHelper.applyFileEnties(builder.newPayload(), swidTagPath, swidFiles,
        Arrays.asList(HashAlgorithm.SHA_512, HashAlgorithm.SHA_256));
    return builder;
  }

}