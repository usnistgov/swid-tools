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
package gov.nist.decima.swid;

import static gov.nist.decima.module.cli.CLIParser.DEFAULT_VALIDATION_REPORT_FILE;
import static gov.nist.decima.module.cli.CLIParser.DEFAULT_VALIDATION_RESULT_FILE;
import static gov.nist.decima.module.cli.CLIParser.OPTION_VALIDATION_REPORT_FILE;
import static gov.nist.decima.module.cli.CLIParser.OPTION_VALIDATION_RESULT_FILE;

import gov.nist.decima.core.assessment.AssessmentException;
import gov.nist.decima.core.assessment.AssessmentExecutor;
import gov.nist.decima.core.assessment.result.AssessmentResults;
import gov.nist.decima.core.document.DocumentException;
import gov.nist.decima.module.cli.CLIParser;
import gov.nist.decima.module.cli.commons.cli.OptionEnumerationValidator;
import gov.nist.decima.xml.DecimaXML;
import gov.nist.decima.xml.assessment.result.ReportGenerator;
import gov.nist.decima.xml.assessment.result.XMLResultBuilder;
import gov.nist.decima.xml.document.XMLDocument;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.transform.TransformerException;

public class Application {
  private static final Logger log = LogManager.getLogger(Application.class);
  private static final String OPTION_USECASE = "usecase";
  private static final String OPTION_USECASE_VALUE_PRIMARY = "primary";
  private static final String OPTION_USECASE_VALUE_CORPUS = "corpus";
  private static final String OPTION_USECASE_VALUE_PATCH = "patch";
  private static final String OPTION_USECASE_VALUE_SUPPLEMENTAL = "supplemental";
  private static final String OPTION_USECASE_DEFAULT_VALUE = OPTION_USECASE_VALUE_PRIMARY;

  private static final String OPTION_AUTHORITATIVE = "A";
  private static final String OPTION_NON_AUTHORITATIVE = "a";

  /**
   * Runs the application.
   * 
   * @param args
   *          the command line arguments
   */
  public static void main(String[] args) {
    try {
      System.exit(new Application().run(args));
    } catch (ParseException e) {
      e.printStackTrace();
      System.exit(-2);
    }
  }

  protected CommandLine parseCLI(String[] args) throws ParseException {
    CLIParser cliParser = new CLIParser("java -jar <decima jar> (options) <swid tag path>");
    cliParser.setVersion(Version.VERSION);

    Option useCase = Option.builder(OPTION_USECASE)
        .desc("the SWID tag type, which is one of: primary, corpus, patch, or supplemental"
            + " (default: primary)")
        .hasArg().build();
    OptionEnumerationValidator useCaseValidator = new OptionEnumerationValidator(useCase);
    useCaseValidator.addAllowedValue(OPTION_USECASE_VALUE_PRIMARY);
    useCaseValidator.addAllowedValue(OPTION_USECASE_VALUE_CORPUS);
    useCaseValidator.addAllowedValue(OPTION_USECASE_VALUE_PATCH);
    useCaseValidator.addAllowedValue(OPTION_USECASE_VALUE_SUPPLEMENTAL);
    cliParser.addOption(useCaseValidator);

    OptionGroup authGroup = new OptionGroup();
    authGroup.addOption(Option.builder(OPTION_AUTHORITATIVE)
        .desc("the tag is produced by an authoritative creator (default)").build());
    authGroup.addOption(Option.builder(OPTION_NON_AUTHORITATIVE)
        .desc("the tag is not produced by an authoritative creator").build());
    cliParser.addOptionGroup(authGroup);

    return cliParser.parse(args);
  }

  private int run(String[] args) throws ParseException {
    CommandLine cmd = parseCLI(args);

    if (cmd == null) {
      return -1;
    }

    List<String> paths = cmd.getArgList();
    if (paths.isEmpty()) {
      System.err.println("At least one path must be specified as an extra argument.");
      return -2;
    } else if (paths.size() > 1) {
      System.err.println("Only one path must be specified as an extra argument.");
      return -3;
    }
    String path = paths.get(0);

    String tagTypeName = cmd.getOptionValue(OPTION_USECASE, OPTION_USECASE_DEFAULT_VALUE);

    TagType tagType = TagType.valueOf(tagTypeName.toUpperCase());
    boolean authoritative = !cmd.hasOption(OPTION_NON_AUTHORITATIVE);

    log.info("Validating tag: " + path);
    log.info("  tag type: " + tagType.getName());
    log.info("  authoritative tag: " + authoritative);

    // handle resultfile
    File validationResultFile;
    {
      String fileValue = cmd.getOptionValue(OPTION_VALIDATION_RESULT_FILE,
          DEFAULT_VALIDATION_RESULT_FILE);
      validationResultFile = new File(fileValue);
      File parentDir = validationResultFile.getParentFile();
      if (parentDir != null && !parentDir.exists()) {
        parentDir.mkdirs();
      }
    }

    // handle reportfile
    File validationReportFile;
    {
      String fileValue = cmd.getOptionValue(OPTION_VALIDATION_REPORT_FILE,
          DEFAULT_VALIDATION_REPORT_FILE);
      validationReportFile = new File(fileValue);
      File parentDir = validationReportFile.getParentFile();
      if (parentDir != null && !parentDir.exists()) {
        parentDir.mkdirs();
      }
    }

    // Load the document to assess
    File file = new File(path);
    XMLDocument doc;
    try {
      doc = DecimaXML.newXMLDocument(file);
    } catch (FileNotFoundException ex) {
      log.error("Non-existant file argument: " + file.getPath(), ex);
      return -4;
    } catch (DocumentException ex) {
      log.error("Unable to parse the XML file: " + file.getPath(), ex);
      return -5;
    }

    // Configure the assessments
    SWIDAssessmentReactor reactor = new SWIDAssessmentReactor(tagType, authoritative);

    ExecutorService executorService = null;
    AssessmentResults validationResult;
    try {
      executorService = Executors.newFixedThreadPool(2);
      
      AssessmentExecutor<XMLDocument> executor = SWIDAssessmentFactory.getInstance()
          .newAssessmentExecutor(tagType, authoritative, executorService);

      // setup the document assessment
      reactor.pushAssessmentExecution(doc, executor);

      // do the assessment
      validationResult = reactor.react();
    } catch (AssessmentException e) {
      log.error("An error occured while performing the assessment", e);
      return -5;
    } finally {
      if (executorService != null) {
        executorService.shutdown();
      }
    }

    // Output the results
    XMLResultBuilder writer = new XMLResultBuilder();
    try (OutputStream os = new BufferedOutputStream(new FileOutputStream(validationResultFile))) {
      log.info("Storing assessment results to: " + validationResultFile);
      writer.write(validationResult, os);
    } catch (FileNotFoundException e) {
      log.error("The result file does not appear to be a regular file: " + validationResultFile, e);
      return -6;
    } catch (IOException e) {
      log.error("Unable to write to the result file: " + validationResultFile, e);
      return -7;
    }

    // output the report
    ReportGenerator reportGenerator = new ReportGenerator();
    // This is the location of the bootstrap directory, which is intended to be relative to the
    // install location of SWIDVal
    File bootsrapDir = new File("bootstrap");
    try {
      reportGenerator.setBootstrapDir(bootsrapDir);
    } catch (IOException e) {
      log.error("Invalid bootstrap location: " + bootsrapDir, e);
      return -8;
    }
    reportGenerator.setHtmlTitle("SWID Tag validation Report");
    reportGenerator.setIgnoreNotTestedResults(true);
    reportGenerator.setIgnoreOutOfScopeResults(true);
    try {
      reportGenerator.setXslTemplateExtension(new URI("classpath:xsl/swid-result.xsl"));
    } catch (URISyntaxException e) {
      log.error("Unable to load XSL template", e);
      return -9;
    }
    log.info("Generating HTML report to: " + validationReportFile);
    try {
      reportGenerator.generate(validationResultFile, validationReportFile);
    } catch (TransformerException | IOException e) {
      log.error("Unable to generate HTML report", e);
      return -10;
    }
    return 0;
  }
}
