package orar.example;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import orar.config.Configuration;
import orar.config.LogInfo;
import orar.io.ontologyreader.HornSHOIF_OntologyReader;
import orar.io.ontologyreader.OntologyReader;
import orar.modeling.ontology.OrarOntology;
import orar.util.PrintingHelper;

public class LoadingExample {
	static String tboxFileName = "src/test/resources/uobm_origintbox_oxforddata/uobmtbox_origin_workwith_oxford_generator.owl";
	static String aboxListFileName = "src/test/resources/uobm_origintbox_oxforddata/aboxListOf2.txt";

	public static void main(String[] args) throws OWLOntologyCreationException {
		Configuration.getInstance().clearDebugLevels();
		Configuration.getInstance().clearLogInfoLevels();
		Configuration.getInstance().addLoginfoLevels(LogInfo.STATISTIC, LogInfo.LOADING_TIME, LogInfo.REASONING_TIME);
		OntologyReader ontoReader= new HornSHOIF_OntologyReader();
		
		OrarOntology orarOntology=ontoReader.getNormalizedOrarOntology(tboxFileName, aboxListFileName);
		PrintingHelper.printSet(orarOntology.getActualDLConstructors());
	}
}
