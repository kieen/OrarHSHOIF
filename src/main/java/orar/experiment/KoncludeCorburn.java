package orar.experiment;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import orar.config.Configuration;
import orar.config.LogInfo;
import orar.dlreasoner.DLReasoner;
import orar.dlreasoner.KoncludeDLReasoner;
import orar.io.ontologyreader.HornSHOIF_OntologyReader;
import orar.io.ontologyreader.OntologyReader;

public class KoncludeCorburn {
	static Logger logger = Logger.getLogger(Coburn.class);

	static String combinedAboxAndTBox = "/Users/kien/benchmarks/coburn/coburnSHOIF.functionalsyntax.owl";

	public static void main(String[] args) throws OWLOntologyCreationException {
		Configuration.getInstance().clearDebugLevels();
		Configuration.getInstance().clearLogInfoLevels();
		Configuration.getInstance().addLoginfoLevels(LogInfo.STATISTIC, LogInfo.LOADING_TIME, LogInfo.REASONING_TIME);
		runWithCombinedTBoxAndABoxes();

	}

	public static void runWithCombinedTBoxAndABoxes() {

		OntologyReader ontologyReader = new HornSHOIF_OntologyReader();
		OWLOntology owlOntology = ontologyReader.getOWLAPIOntology(combinedAboxAndTBox);
		// logger.info(
		// "Info: Concstructors in the validated ontology:" +
		// normalizedOrarOntology.getActualDLConstructors());
		// long startAbstraction = System.currentTimeMillis();
		DLReasoner dlReasoner = new KoncludeDLReasoner(owlOntology);
		dlReasoner.computeEntailments();
		System.out.println("Reasoning time:" + dlReasoner.getReasoningTime());

	}
}
