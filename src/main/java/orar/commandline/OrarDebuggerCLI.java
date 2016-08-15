package orar.commandline;

import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAxiom;

import orar.config.Configuration;
import orar.config.LogInfo;
import orar.debugger.OrarDebugger;
import orar.debugger.OrarDebugger_Hermit;
import orar.debugger.OrarDebugger_Pellet;
import orar.dlfragmentvalidator.DLConstructor;
import orar.io.ontologyreader.DLLiteHOD_OntologyReader;
import orar.io.ontologyreader.DLLiteH_OntologyReader;
import orar.io.ontologyreader.HornSHOIF_OntologyReader;
import orar.io.ontologyreader.OntologyReader;
import orar.materializer.Materializer;
import orar.materializer.DLLite.DLLite_Materializer_Fact;
import orar.materializer.DLLite.DLLite_Materializer_Hermit;
import orar.materializer.DLLite.DLLite_Materializer_Konclude;
import orar.materializer.DLLite.DLLite_Materializer_Pellet;
import orar.materializer.HornSHIF.HornSHIF_Materializer_Fact;
import orar.materializer.HornSHIF.HornSHIF_Materializer_Hermit;
import orar.materializer.HornSHIF.HornSHIF_Materializer_Konclude;
import orar.materializer.HornSHIF.HornSHIF_Materializer_Pellet;
import orar.materializer.HornSHOIF.HornSHOIF_Materializer_Fact;
import orar.materializer.HornSHOIF.HornSHOIF_Materializer_Hermit;
import orar.materializer.HornSHOIF.HornSHOIF_Materializer_Konclude;
import orar.materializer.HornSHOIF.HornSHOIF_Materializer_Pellet;
import orar.modeling.ontology.OrarOntology;

public class OrarDebuggerCLI {
	private static Configuration config = Configuration.getInstance();
	private static Logger logger = Logger.getLogger(OrarDebuggerCLI.class);

	public static void main(String[] args) {
		// PropertyConfigurator.configure("src/main/resources/log4j.properties");

		Options options = new Options();
		/*
		 * Boolean options
		 */
		Option parsingTime = new Option(Argument.LOADING_TIME, false, "print time for loading ontology");
		Option runningTime = new Option(Argument.REASONING_TIME, false, "print time for ontology materialization");
		Option abstractDebugging = new Option(Argument.ABSTRACT_DEBUG, false,
				"print inconsistency patterns in form of abstract axioms");

		Option totalTime = new Option(Argument.TOTAL_TIME, false,
				"print total time of the system,e.g. loading time + materialization time");

		Option statistic = new Option(Argument.STATISTIC, false,
				"print statistic information of the (materialized) ontology");
		Option help = new Option(Argument.HELP, false, "print help");
		/*
		 * Argument options
		 */
		Option tbox = new Option(Argument.TBOX, true, "TBox OWL file");
		Option aboxes = new Option(Argument.ABOX, true,
				"a text file containing the list of ABox files, each file in a separated line");
		Option ontology = new Option(Argument.ONTOLOGY, true, "OWL ontology file containing both TBox and ABox(es)");
		// Option reasoner = new Option(
		// REASONER,
		// true,
		// "set the reasoner used in the system. If you choose konclude, then
		// the path to Konclude reasoner must be provided");
		//
		StringBuilder reasonerDescription = new StringBuilder();
		reasonerDescription.append("the (inner) reasoner used in the system. Choose one of the following reasoners: ");
		reasonerDescription.append(Argument.KONCLUDE + ", " + Argument.HERMIT + ", " + Argument.PELLET + "\n");
		reasonerDescription.append(
				" If you choose konclude, then the path to konclude reasoner and a port number must be provided as we call Konclude via OWLLink");
		Option reasoner = Option.builder(Argument.REASONER).desc(reasonerDescription.toString()).hasArg(true).build();

		Option konclude = new Option(Argument.KONCLUDEPATH, true, "Konclude reasoner file");
		Option port = new Option(Argument.PORT, true,
				"port number of Konclude server in case the inner reasoner is Konclude");
		// Option dl = new Option(DL, true,
		// "Description Logic fragment, e.g.horn or nonhorn");

		Option dl = Option.builder(Argument.DL)
				.desc("Description Logic fragment. Choose one of the following arguments: dllite_hod, horn_shoif")
				.hasArg(true).build();

		Option split = new Option(Argument.SPLITTING, true,
				"number of types per abstract ABox. Used as an optimization (experimental feature)");
		Option task = Option.builder(Argument.TASK)
				.desc("reasoning task. Choose one of the following arguments: consistency, materialization")
				.hasArg(true).build();
		/*
		 * add options
		 */

		options.addOption(totalTime);
		options.addOption(parsingTime);
		options.addOption(runningTime);
		options.addOption(statistic);
		options.addOption(tbox);
		options.addOption(aboxes);
		options.addOption(ontology);
		// options.addOption(reasoner);
		// options.addOption(konclude);
		// options.addOption(dl);
		options.addOption(abstractDebugging);
		options.addOption(split);
		// options.addOption(port);
		// options.addOption(task);
		options.addOption(help);
		// create the parser
		CommandLineParser parser = new DefaultParser();
		try {
			/*
			 * parse the command line arguments
			 */
			CommandLine commandLine = parser.parse(options, args);
			/*
			 * print help
			 */
			if (commandLine.hasOption(Argument.HELP)) {
				printHelp(options);
				return;
			}
			/*
			 * Check the validity of the arguments
			 */
			if (!argumentsAreValid(commandLine)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("orar", options);
				return;
			}
			/*
			 * get arguments and config the system accordingly
			 */
			setConfigWithBooleanArguments(commandLine);

			logger.info("Run abstraction with " + commandLine.getOptionValue(Argument.REASONER));

			OrarOntology orarOntology = getOrarOntology(commandLine);
			OrarDebugger debugger = getDebugger(orarOntology);
			logger.info("Run " + debugger.getClass());
			runDebugger(debugger, commandLine);
		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			printHelp(options);

		}
	}

	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Orar", options);
		printExampleRun();
	}

	private static void printExampleRun() {
		System.out.println("");
		System.out.println("Example run with HermiT as an inner reasoner:");
		System.out.println(
				"java -jar ../Orar.jar -task materialization -dl dllite_hod -reasoner hermit -statistic -reasoningtime -tbox ./tbox.owl -abox ./aboxList.txt");
		System.out.println("");
		System.out.println("Example run with Konclude as an inner reasoner:");
		System.out.println(
				"java -jar ../Orar.jar -task materialization -dl dllite_hod -reasoner konclude -koncludepath ./KoncludeMac -port 9090 -statistic -reasoningtime -tbox ./tbox.owl -abox ./aboxList.txt");
	}

	private static long runDebugger(OrarDebugger debugger, CommandLine commandLine) {
		Set<Set<OWLAxiom>> allExplannations = debugger.getExplanationsForInconsistency(5, 100000000);
		int count = 0;
		for (Set<OWLAxiom> eachExpl : allExplannations) {
			logger.info("explanation #" + ++count + "\n");
			for (OWLAxiom ax : eachExpl) {
				logger.info(ax);
			}
			logger.info("\n");

		}
		return debugger.getReasoningTimeInSeconds();

	}

	static private OrarDebugger getDebugger(OrarOntology orarOntology) {
		return new OrarDebugger_Pellet(orarOntology);
	}

	static private boolean argumentsAreValid(CommandLine commandLine) {

		if (commandLine.hasOption(Argument.ONTOLOGY)) {
			if (commandLine.hasOption(Argument.TBOX) || commandLine.hasOption(Argument.ABOX)) {
				System.err.println("More than one ways to read the input ontology. Please choose either -"
						+ Argument.ONTOLOGY + " or -" + Argument.TBOX + " -" + Argument.ABOX);
				return false;
			}
		}

		if (!commandLine.hasOption(Argument.ONTOLOGY)
				&& !(commandLine.hasOption(Argument.TBOX) && commandLine.hasOption(Argument.ABOX))) {
			System.err.println("Input ontology arguments are missing. Please choose either -" + Argument.ONTOLOGY
					+ " or -" + Argument.TBOX + " -" + Argument.ABOX);
			return false;

		}

		if (commandLine.hasOption(Argument.SPLITTING)) {
			String typePerOntString = commandLine.getOptionValue(Argument.SPLITTING);
			try {
				Integer.parseInt(typePerOntString);
			} catch (NumberFormatException ex) {
				System.err.print("argument for -splitting is not an integer number");
				return false;
			}
		}

		return true;
	}

	static private void setConfigWithBooleanArguments(CommandLine commandLine) {
		if (commandLine.hasOption(Argument.LOADING_TIME)) {
			config.addLoginfoLevels(LogInfo.LOADING_TIME);
		}

		if (commandLine.hasOption(Argument.ABSTRACT_DEBUG)) {
			config.addLoginfoLevels(LogInfo.ABSTRACT_EXPLANATION);
		}

		if (commandLine.hasOption(Argument.REASONING_TIME)) {
			config.addLoginfoLevels(LogInfo.REASONING_TIME);
		}

		if (commandLine.hasOption(Argument.STATISTIC)) {
			config.addLoginfoLevels(LogInfo.STATISTIC);
		}

		if (commandLine.hasOption(Argument.SPLITTING)) {
			String typePerOntString = commandLine.getOptionValue(Argument.SPLITTING);

			int typePerAbstractOnt = Integer.parseInt(typePerOntString);
			config.setNumberOfTypePerOntology(typePerAbstractOnt);

		}
	}

	static private OntologyReader getOntologyReader(CommandLine commandLine) {

		return new DLLiteHOD_OntologyReader();

	}

	static private OrarOntology getOrarOntology(CommandLine commandLine) {
		OrarOntology orarOntology;
		OntologyReader ontReader = getOntologyReader(commandLine);

		if (commandLine.hasOption(Argument.ONTOLOGY)) {
			String owlFilePath = commandLine.getOptionValue(Argument.ONTOLOGY);

			orarOntology = ontReader.getNormalizedOrarOntology(owlFilePath);

		} else {
			String tboxFile = commandLine.getOptionValue(Argument.TBOX);
			String aboxList = commandLine.getOptionValue(Argument.ABOX);
			orarOntology = ontReader.getNormalizedOrarOntology(tboxFile, aboxList);
		}

		return orarOntology;
	}
}
