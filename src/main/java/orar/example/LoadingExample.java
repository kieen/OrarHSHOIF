package orar.example;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import orar.config.Configuration;
import orar.config.LogInfo;
import orar.io.ontologyreader.HornSHOIF_OntologyReader;
import orar.io.ontologyreader.OntologyReader;
import orar.modeling.ontology.OrarOntology;

public class LoadingExample {
	static String tboxFileName = "/Users/kien/benchmarks/npd-v2/npd-pagoda.owl";
	static String aboxListFileName = "/Users/kien/benchmarks/npd-v2/aboxList.txt";

	public static void main(String[] args) throws OWLOntologyCreationException {
		Configuration.getInstance().clearDebugLevels();
		Configuration.getInstance().clearLogInfoLevels();
		Configuration.getInstance().addLoginfoLevels(LogInfo.STATISTIC, LogInfo.LOADING_TIME, LogInfo.REASONING_TIME);
		OntologyReader ontoReader= new HornSHOIF_OntologyReader();
		
		OrarOntology orarOntology=ontoReader.getNormalizedOrarOntology(tboxFileName, aboxListFileName);
	}
}
