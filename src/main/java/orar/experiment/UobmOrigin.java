package orar.experiment;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import orar.config.Configuration;
import orar.config.DebugLevel;
import orar.config.LogInfo;
import orar.io.ontologyreader.HornSHOIF_OntologyReader;
import orar.io.ontologyreader.OntologyReader;
import orar.materializer.Materializer;
import orar.materializer.HornSHOIF.HornSHOIF_Materialization_Hermit;
import orar.materializer.HornSHOIF.HornSHOIF_Materialization_Konclude;
import orar.modeling.ontology.OrarOntology;

public class UobmOrigin {
	static String tboxFileName = "src/test/resources/uobm-origin/tbox/uobmtbox_origin.owl";
	static String aboxListFileName = "/Users/kien/benchmarks/UOB/dl620/1/aboxListOriginU1.txt";
	static Logger logger = Logger.getLogger(NPD.class);

	public static void main(String[] args) throws OWLOntologyCreationException {
		Configuration.getInstance().clearDebugLevels();
		Configuration.getInstance().clearLogInfoLevels();
		Configuration.getInstance().addLoginfoLevels(LogInfo.STATISTIC, LogInfo.LOADING_TIME, LogInfo.REASONING_TIME);
		Configuration.getInstance().addDebugLevels(DebugLevel.ADDING_MARKING_AXIOMS, DebugLevel.NORMALIZATION);
		runWithSeperatedTBoxAndABoxes();
		// runWithCombinedTBoxAndABoxes();

	}

	public static void runWithSeperatedTBoxAndABoxes() {
	
		OntologyReader ontologyReader = new HornSHOIF_OntologyReader();
		OrarOntology normalizedOrarOntology = ontologyReader.getNormalizedOrarOntology(tboxFileName, aboxListFileName);
		logger.info(
				"Info: Concstructors in the validated ontology:" + normalizedOrarOntology.getActualDLConstructors());
		// long startAbstraction = System.currentTimeMillis();
		Materializer materializer = new HornSHOIF_Materialization_Konclude(normalizedOrarOntology);
		materializer.materialize();

	}
}
