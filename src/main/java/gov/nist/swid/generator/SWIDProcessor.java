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

package gov.nist.swid.generator;

import gov.nist.swid.builder.EntityBuilder;
import gov.nist.swid.builder.FileBuilder;
import gov.nist.swid.builder.PayloadBuilder;
import gov.nist.swid.builder.SWIDBuilder;
import gov.nist.swid.builder.SWIDConstants;
import gov.nist.swid.builder.output.XMLOutputHandler;
import gov.nist.swid.builder.resource.HashAlgorithm;
import gov.nist.swid.builder.resource.HashUtils;
import gov.nist.swid.builder.resource.PathRelativizer;
import gov.nist.swid.builder.resource.ResourceCollection;
import gov.nist.swid.builder.resource.ResourceEntry;
import gov.nist.swid.generator.resource.ArchiveEntryResourceEntry;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.components.io.resources.PlexusIoFileResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SWIDProcessor {
  private final ResourceCollection resourceCollection;
  private final MavenProject project;
  private final TagInfo tagInfo;
  private String swidTagArchiveDir;
  private File tagOutputDirectory;
  private final List<Entity> entities = new LinkedList<>();
  private final Map<String, Artifact> artifactLocationToArtifactMap;

  public SWIDProcessor(MavenProject project) throws IOException {
    this(project, "tagInfo.properties");
  }

  /**
   * Construct a new SWID tag information processor.
   * 
   * @param mavenProject
   *          the calling maven project
   * @param tagInfoPropertyFileName
   *          the filename for cached tag-related metadata
   * @throws IOException
   *           if an error occurs while load the tag information
   */
  public SWIDProcessor(MavenProject mavenProject, String tagInfoPropertyFileName)
      throws IOException {
    this.resourceCollection = new ResourceCollection();
    this.project = mavenProject;
    this.tagInfo = new TagInfo(new File(getProjectBuildDirectory(), tagInfoPropertyFileName));
    this.artifactLocationToArtifactMap = initializeArtifactInfo(mavenProject);
  }

  private static Map<String, Artifact> initializeArtifactInfo(MavenProject project) {
    Map<String, Artifact> retval = new HashMap<>();

    Artifact target = project.getArtifact();
    retval.put(target.getFile().getAbsolutePath(), target);

    @SuppressWarnings("unchecked")
    Collection<Artifact> artifacts = (Collection<Artifact>) project.getRuntimeArtifacts();
    for (Artifact artifact : artifacts) {
      retval.put(artifact.getFile().getAbsolutePath(), artifact);
    }
    return retval;
  }

  public File getTagOutputDirectory() {
    return tagOutputDirectory == null
        ? new File(getProject().getBuild().getDirectory(), "generated-swid") : tagOutputDirectory;
  }

  public void setTagOutputDirectory(File tagOutputDirectory) {
    this.tagOutputDirectory = tagOutputDirectory;
  }

  public String getSwidTagArchiveDir() {
    return swidTagArchiveDir == null ? "SWIDTAG" : swidTagArchiveDir;
  }

  public void setSwidTagArchiveDir(String relativeDir) {
    this.swidTagArchiveDir = relativeDir;
  }

  /**
   * Processes all gathered information to create a SWID tag.
   * 
   * @param archiver
   *          the Maven Archiver instance to add the tag to
   * @throws IOException
   *           if an error occurs when validating tag information
   */
  public void process(Archiver archiver) throws IOException {
    boolean changed = validateTagInfo();
    getTagOutputDirectory().mkdirs();
    File tagFile = getSWIDTagFile();
    if (changed || !tagFile.exists()) {
      generateSWIDTag();
    }
    archiver.addFile(tagFile, getSWIDTagArchivePath());

  }

  private boolean validateTagInfo() throws IOException {
    boolean artifactInfoChanged = false;
    if (!getProjectArtifactId().equals(tagInfo.getProjectArtifactId())) {
      artifactInfoChanged = true;
      tagInfo.setProjectArtifactId(getProjectArtifactId());
    }

    if (!getProjectGroupId().equals(tagInfo.getProjectGroupId())) {
      artifactInfoChanged = true;
      tagInfo.setProjectGroupId(getProjectGroupId());
    }

    if (!getProjectVersion().equals(tagInfo.getProjectVersion())) {
      artifactInfoChanged = true;
      tagInfo.setProjectVersion(getProjectVersion());
    }

    String digest;
    try {
      digest = HashUtils.toHexString(resourceCollection.getMessageDigest(HashAlgorithm.SHA_512));
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    boolean resourceDigestChanged = !digest.equals(tagInfo.getResourceDigest());

    if (artifactInfoChanged || (isSnapshotRelease() && resourceDigestChanged)) {
      tagInfo.setTagId(generateTagId());
      tagInfo.setTagVersion(TagInfo.TAG_VERISON_PROPERTY_DEFAULT);
    }

    if (resourceDigestChanged) {
      tagInfo.setResourceDigest(digest);
      int tagVersion = tagInfo.getTagVersion();
      tagInfo.setTagVersion(++tagVersion);
    }

    boolean retval;
    if (tagInfo.isModified()) {
      tagInfo.saveProperties();
      retval = true;
    } else {
      retval = false;
    }
    return retval;
  }

  protected ResourceCollection getResourceCollection() {
    return resourceCollection;
  }

  protected MavenProject getProject() {
    return project;
  }

  protected TagInfo getTagInfo() {
    return tagInfo;
  }

  protected File getSWIDTagFile() {
    return new File(getTagOutputDirectory(), getSWIDTagFilename());
  }

  protected String getSWIDTagFilename() {
    return getTagInfo().getTagId() + ".swidtag";
  }

  protected String getSWIDTagArchivePath() {
    return getSwidTagArchiveDir() + "/" + getSWIDTagFilename();
  }

  /**
   * Creates and stores the SWID tag.
   * 
   * @throws ArchiverException
   *           if an error occurs while writing the generated SWID tag
   */
  public void generateSWIDTag() throws ArchiverException {
    SWIDBuilder builder = SWIDBuilder.create();

    builder.name(generateName());
    builder.tagVersion(getTagInfo().getTagVersion());
    builder.version(getProjectVersion());

    if (isSnapshotRelease()) {
      builder.versionScheme(SWIDConstants.VERSION_SCHEME_MULTIPART_NUMERIC_WITH_SUFFIX);
    } else {
      builder.versionScheme(SWIDConstants.VERSION_SCHEME_MULTIPART_NUMERIC);
    }
    builder.tagId(getTagInfo().getTagId());

    for (Entity entity : entities) {
      EntityBuilder entityBuilder = EntityBuilder.create();
      if (entity.getName() != null) {
        entityBuilder.name(entity.getName());
      }

      if (entity.getRegid() != null) {
        entityBuilder.regid(entity.getRegid());
      }

      if (entity.getRoles() != null) {
        for (String role : entity.getRoles()) {
          entityBuilder.addRole(role);
        }
      }
      builder.addEntity(entityBuilder);
    }

    String swidPath = getSWIDTagArchivePath();

    PayloadBuilder payloadBuilder = PayloadBuilder.create();
    for (ResourceEntry entry : getResourceCollection().getResources()) {

      FileBuilder fileBuilder
          = payloadBuilder.newFileResource(PathRelativizer.relativize(swidPath, entry.getPath()));

      String version = entry.getVersion();
      if (version != null) {
        fileBuilder.version(version);
      }

      fileBuilder.size(entry.getSize());
      for (Map.Entry<HashAlgorithm, List<Byte>> hashEntry : entry.getDigestValues().entrySet()) {
        fileBuilder.hash(hashEntry.getKey(), HashUtils.toHexString(hashEntry.getValue()));
      }
    }
    builder.payload(payloadBuilder);

    try (OutputStream os = new BufferedOutputStream(new FileOutputStream(getSWIDTagFile()))) {
      XMLOutputHandler handler = new XMLOutputHandler();
      handler.write(builder, os);
    } catch (FileNotFoundException e) {
      throw new ArchiverException(e.getMessage());
    } catch (IOException e) {
      throw new ArchiverException(e.getMessage());
    }
  }

  public String getProjectGroupId() {
    return getProject().getGroupId();
  }

  public String getProjectArtifactId() {
    return getProject().getArtifactId();
  }

  public String getProjectVersion() {
    return getProject().getVersion();
  }

  public File getProjectBuildDirectory() {
    return new File(getProject().getBuild().getDirectory());
  }

  public boolean isSnapshotRelease() {
    String artifactId = getProjectVersion();
    return artifactId.endsWith("SNAPSHOT");
  }

  protected String generateTagId() {
    StringBuilder str = new StringBuilder();
    str.append(getProjectGroupId());
    str.append('-');
    str.append(getProjectArtifactId());
    str.append('-');
    str.append(getProjectVersion());

    if (isSnapshotRelease()) {
      // Append a date/time to make this unique
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'kkmmss");
      str.append("-");
      // Use UTC to prevent information leakage around where the project is built
      str.append(ZonedDateTime.now(ZoneId.of("UTC")).format(formatter));
    }
    return str.toString();
  }

  protected String generateName() {
    String name = getProject().getName();
    if (name == null) {
      StringBuilder str = new StringBuilder();
      str.append(getProjectGroupId());
      str.append('-');
      str.append(getProjectArtifactId());
      str.append('-');
      str.append(getProjectVersion());
      name = str.toString();
    }
    return name;
  }

  /**
   * Associates a Maven Archiver entry with this processor, which will result in the resource being
   * recorded in the SWID tag.
   * 
   * @param ae the Maven Archiver entry to add
   * @throws NoSuchAlgorithmException if a required hash algorithm is not supported
   * @throws IOException if an error occurred while determining a resource artifact path 
   */
  public void addResourceEntry(ArchiveEntry ae) throws NoSuchAlgorithmException, IOException {
    PlexusIoResource resource = ae.getResource();
    Artifact artifact = null;
    if (resource instanceof PlexusIoFileResource) {
      PlexusIoFileResource fileResource = (PlexusIoFileResource) resource;
      File file = fileResource.getFile();
      artifact = artifactLocationToArtifactMap.get(file.getAbsolutePath());
    }
    ArchiveEntryResourceEntry entry = new ArchiveEntryResourceEntry(ae, artifact);
    getResourceCollection().addResource(entry);

  }
  //
  // public static void main(String[] args) throws ArchiverException, IOException,
  // NoSuchAlgorithmException {
  // Model model = new Model();
  // model.setArtifactId("artifactId");
  // model.setGroupId("groupId");
  // model.setVersion("1.0.0-SNAPSHOT");
  //
  // MavenProject project = new MavenProject(model);
  // SWIDProcessor processor = new SWIDProcessor(project);
  // ArchiveEntry entry = ArchiveEntry.createFileEntry("bin/test", new File("pom.xml"),
  // AbstractArchiver.DEFAULT_FILE_MODE, AbstractArchiver.DEFAULT_DIR_MODE);
  // processor.processResource(entry);
  // processor.process();
  // }

  public void addEntity(Entity entity) {
    entities.add(entity);
  }
}
