package orar.example;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import orar.config.Configuration;
import orar.config.LogInfo;
import orar.io.ontologyreader.HornSHOIF_OntologyReader;
import orar.io.ontologyreader.OntologyReader;
import orar.materializer.Materializer;
import orar.materializer.HornSHIF.HornSHIF_Materialization_Hermit;
import orar.materializer.HornSHIF.HornSHIF_Materialization_Konclude;
import orar.materializer.HornSHOIF.HornSHOIF_Materialization_Konclude;
import orar.modeling.ontology.OrarOntology;

public class UobmOx {

	static Logger logger = Logger.getLogger(UobmOx.class);

	static String combinedAboxAndTBox = "src/test/resources/uobm-ox/u1AboxAndTbox/univ0.owl";

	public static void main(String[] args) throws OWLOntologyCreationException {
		Configuration.getInstance().clearDebugLevels();
		Configuration.getInstance().clearLogInfoLevels();
		Configuration.getInstance().setNumberOfTypePerOntology(30);
		Configuration.getInstance().addLoginfoLevels(LogInfo.STATISTIC, LogInfo.LOADING_TIME, LogInfo.REASONING_TIME);
		runWithCombinedTBoxAndABoxes();

	}

	public static void runWithCombinedTBoxAndABoxes() {

		OntologyReader ontologyReader = new HornSHOIF_OntologyReader();
		OrarOntology normalizedOrarOntology = ontologyReader.getNormalizedOrarOntology(combinedAboxAndTBox);
		logger.info(
				"Info: Concstructors in the validated ontology:" + normalizedOrarOntology.getActualDLConstructors());
		// long startAbstraction = System.currentTimeMillis();
		Materializer materializer = new HornSHIF_Materialization_Konclude(normalizedOrarOntology);
		materializer.materialize();

	}

}
