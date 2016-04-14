package orar.experiment;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import orar.config.Configuration;
import orar.config.LogInfo;
import orar.io.ontologyreader.HornSHOIF_OntologyReader;
import orar.io.ontologyreader.OntologyReader;
import orar.materializer.Materializer;
import orar.materializer.HornSHOIF.HornSHOIF_Materialization_Hermit;
import orar.modeling.ontology.OrarOntology;

public class NPD {
	static String tboxFileName = "/Users/kien/benchmarks/npd-pagoda/npd.owl";
	static String aboxListFileName = "/Users/kien/benchmarks/npd-v2/aboxList.txt";
	static Logger logger =Logger.getLogger(NPD.class);
	

	public static void main(String[] args) throws OWLOntologyCreationException {
		Configuration.getInstance().clearDebugLevels();
		Configuration.getInstance().clearLogInfoLevels();
		Configuration.getInstance().addLoginfoLevels(LogInfo.STATISTIC, LogInfo.LOADING_TIME);
		runWithSeperatedTBoxAndABoxes();
//		runWithCombinedTBoxAndABoxes();

	}

	public static void runWithSeperatedTBoxAndABoxes() {
		Configuration.getInstance().addLoginfoLevels(LogInfo.STATISTIC,LogInfo.LOADING_TIME,LogInfo.REASONING_TIME);
		
		OntologyReader ontologyReader = new HornSHOIF_OntologyReader();
		OrarOntology normalizedOrarOntology = ontologyReader.getNormalizedOrarOntology(tboxFileName, aboxListFileName);
		logger.info("Info: Concstructors in the validated ontology:"+normalizedOrarOntology.getActualDLConstructors());
		// long startAbstraction = System.currentTimeMillis();
		Materializer materializer = new HornSHOIF_Materialization_Hermit(normalizedOrarOntology);
		materializer.materialize();

	}

}
