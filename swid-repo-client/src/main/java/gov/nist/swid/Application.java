
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

package gov.nist.swid;

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.nist.swid.service.Action;
import gov.nist.swid.service.HTTPService;
import gov.nist.swid.service.HTTPServiceImpl;
import gov.nist.swid.service.TagType;

public class Application {

	private static final Logger LOG = LogManager.getLogger(Application.class);
	private static final String OPTION_KEYSTORE = "keystore-file";
	private static final String OPTION_KEYSTORE_PWD = "keystore-passwd";
	private static final String OPTION_SEED = "seed";
	private static final String OPTION_TAGTYPE = "tag-type";
	private static final String OPTION_UPDATE = "update";
	private static final Action SWID_DEFAULT_ACTION = Action.insert;

	/**
	 * Runs the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.exit(new Application().run(args));
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(-2);
		}
	}

	/**
	 * Parses the command line input arguments
	 * 
	 * @param args
	 * @return
	 * @throws ParseException
	 */
	protected CommandLine parseCLI(String[] args) throws ParseException {

		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		Option keystore = Option.builder().longOpt(OPTION_KEYSTORE).desc("the client keystore file path").hasArg()
				.build();
		options.addOption(keystore);

		Option keystorePwd = Option.builder().longOpt(OPTION_KEYSTORE_PWD).desc("the client keystore password").hasArg()
				.build();
		options.addOption(keystorePwd);

		Option seed = Option.builder().longOpt(OPTION_SEED).desc("the client seed").hasArg().build();
		options.addOption(seed);

		Option tagType = Option.builder().longOpt(OPTION_TAGTYPE)
				.desc("the swid tag type - " + TagType.displayTagTypes()).hasArg().build();
		options.addOption(tagType);

		Option update = Option.builder().longOpt(OPTION_UPDATE).desc("the action requested").build();
		options.addOption(update);

		options.addOption(Option.builder().longOpt("help").required(false).build());

		for (String s : args) {
			if (s.equals("-h") || s.equals("--help")) {
				String header = "Post Software Identification(SWID) tags to National Vulnerability Database(NVD) repository\n\n";
				String footer = "\nPlease report issues to nvd@nist.gov";
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("swidclient [options] <SWID Tag paths>", header, options, footer, false);
				System.exit(1);
			}
		}

		CommandLine cmd = parser.parse(options, args);

		return cmd;

	}

	/**
	 * Authenticate with NVD services and insert or update SWID data
	 * 
	 * @param args
	 * @return
	 * @throws ParseException
	 */
	private int run(String[] args) throws ParseException {
		CommandLine cmd = parseCLI(args);

		int validationResult = this.validateCommandLineArguments(cmd);

		if (validationResult < 0) {
			return validationResult;
		}

		List<String> tagFileNames = cmd.getArgList();

		if (tagFileNames.isEmpty()) {
			LOG.error("At least one path must be specified as an extra argument.");
			return -2;
		}

		// Get the command line arguments
		String clientCertificatePath = cmd.getOptionValue(OPTION_KEYSTORE);
		String clientCertificatePassword = cmd.getOptionValue(OPTION_KEYSTORE_PWD);
		String passwordSeed = cmd.getOptionValue(OPTION_SEED);
		Action swidAction = SWID_DEFAULT_ACTION;
		if (cmd.hasOption(OPTION_UPDATE)) {
			swidAction = Action.update;
		}
		TagType type = null;
		if (cmd.hasOption(OPTION_TAGTYPE)) {
			type = TagType.lookup(cmd.getOptionValue(OPTION_TAGTYPE));
		}

		try {
			HTTPService swidService = new HTTPServiceImpl();

			swidService.postSwid(clientCertificatePath, clientCertificatePassword, passwordSeed, tagFileNames,
					swidAction, type);

		} catch (FileNotFoundException ex) {
			LOG.error("Non-existant file argument: ", ex);
			return -4;
		} catch (Exception e) {
			LOG.error("Error performing SWID post request: ", e);
			return -4;
		}
		return 0;

	}

	/**
	 * Validate the required command line options
	 * 
	 * @param cmd
	 * @return
	 */
	private int validateCommandLineArguments(CommandLine cmd) {
		if (cmd == null) {
			return -1;
		}
		List<String> paths = cmd.getArgList();
		if (paths.isEmpty()) {
			LOG.error("At least one swid tag file should be provided.");
			return -2;
		}
		if (!cmd.hasOption(OPTION_KEYSTORE)) {
			LOG.error("Client keystore path must be specified.");
			return -2;
		}
		if (!cmd.hasOption(OPTION_KEYSTORE_PWD)) {
			LOG.error("Client keystore password must be provided.");
			return -2;
		}

		if (!cmd.hasOption(OPTION_SEED)) {
			LOG.error("Please provide the password seed.");
			return -2;
		}

		if (cmd.hasOption(OPTION_TAGTYPE)) {
			TagType tagType = TagType.lookup(cmd.getOptionValue(OPTION_TAGTYPE));
			if (tagType == null) {
				LOG.error("Please provide a valid SWID tag type - " + TagType.displayTagTypes());
				return -2;
			}
		}

		return 0;
	}
}
