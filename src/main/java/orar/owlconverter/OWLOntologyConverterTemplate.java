package orar.owlconverter;

import java.io.File;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import orar.io.ontologyreader.OntologyReader;

public abstract class OWLOntologyConverterTemplate implements OWLOntologyConverter {
	private static Logger logger = Logger.getLogger(OWLOntologyConverterTemplate.class);
	private final OntologyReader ontologyReader;

	public OWLOntologyConverterTemplate() {
		this.ontologyReader = getOntologyReader();
	}

	protected abstract OntologyReader getOntologyReader();

	@Override
	public void convert(String tboxFile, String aboxListFile, String owlFunctionalSyntaxFile) {
		long startSavingTime = System.currentTimeMillis();
		OWLOntology owlOntology = this.ontologyReader.getOWLAPIOntology(tboxFile, aboxListFile);
		long endSavingTime = System.currentTimeMillis();
		long savingTimeInSeconds = (endSavingTime - startSavingTime) / 1000;
		logger.info("Time for loading the ontology (in seconds): " + savingTimeInSeconds);
		saveOntology(owlOntology, owlFunctionalSyntaxFile);
		logger.info("Done!");
	}

	private void saveOntology(OWLOntology ontology, String owlFunctionalSyntaxFile) {
		OWLOntologyManager manager = ontology.getOWLOntologyManager();
		logger.info("Saving ontology to the file: " + owlFunctionalSyntaxFile + "...");
		OWLFunctionalSyntaxOntologyFormat functionalFormat = new OWLFunctionalSyntaxOntologyFormat();
		// OWLXMLOntologyFormat functionalFormat = new
		// OWLXMLOntologyFormat();
		File file = new File(owlFunctionalSyntaxFile);
		IRI iriDocument = IRI.create(file.toURI());
		try {
			long startSavingTime = System.currentTimeMillis();
			manager.saveOntology(ontology, functionalFormat, iriDocument);
			long endSavingTime = System.currentTimeMillis();
			long savingTimeInSeconds = (endSavingTime - startSavingTime) / 1000;

			logger.info("Time for saving the ontology for Konclude (in seconds):" + savingTimeInSeconds);

		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}

	}
}
