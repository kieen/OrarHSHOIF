package orar.io;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import orar.io.ontologyreader.OntologyConverter;
import orar.io.ontologyreader.StreamOntologyReader2InternalModel;
import orar.modeling.ontology.OrarOntology;
import orar.util.PrintingHelper;

public class StreamOntologyReaderTest {

	/**
	 * Check if parsing using Jena and using OWLAPI return the same results.
	 * 
	 * @throws OWLOntologyCreationException
	 */
	@Test
	public void testUOBM1() throws OWLOntologyCreationException {
		/*
		 * Load ontology where TBox and ABox are in separated files.
		 */
		String tboxFileName = "src/test/resources/uobm-ox/univ-bench-dl-ox.owl";

		String aboxListFileName = "src/test/resources/uobm-ox/u1/aboxU1.txt";

		String allInOneOntologyName = "src/test/resources/uobm-ox/u1/univ0.owl";

		long streamStart = System.currentTimeMillis();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology owlOntology = manager.loadOntologyFromOntologyDocument(new File(tboxFileName));

		StreamOntologyReader2InternalModel streamReader = new StreamOntologyReader2InternalModel(owlOntology,
				aboxListFileName);

		OrarOntology ontologyByStreamReader = streamReader.getOntology();

		long streamEnd = System.currentTimeMillis();

		long streamTime = (streamEnd - streamStart) / 1000;
		System.out.println("Stream time:" + streamTime);

		/*
		 * Load ontology where TBox and ABox are mixed into one file.
		 */

		long owlapiStart = System.currentTimeMillis();
		manager.removeOntology(owlOntology);

		OWLOntology mixedOntology = manager.loadOntologyFromOntologyDocument(new File(allInOneOntologyName));
		OntologyConverter converter = new OntologyConverter(mixedOntology);
		OrarOntology ontologyByOWLAPI = converter.getInternalOntology();

		long owlapiEnd = System.currentTimeMillis();

		long owlapiTime = (owlapiEnd - owlapiStart) / 1000;

		System.out.println("owlapi time:" + owlapiTime);
		int numberOfCAByStreamReader = ontologyByStreamReader.getOWLAPIConceptAssertionsWithNormalizationSymbols()
				.size();
		int numberOfCAByOWLAPI = ontologyByOWLAPI.getOWLAPIConceptAssertionsWithNormalizationSymbols().size();
		System.out.println("size of Class Assertion by the stream reader:" + numberOfCAByStreamReader);
		System.out.println("size of Class Assertion by OWLAPI reader:" + numberOfCAByOWLAPI);

		// Assert.assertEquals(
		// ontologyByStreamReader.getConceptAssertions(),
		// ontologyByOWLAPI.getConceptAssertions());

		Assert.assertEquals(ontologyByStreamReader.getOWLAPIRoleAssertionsWithNormalizationSymbols(),
				ontologyByOWLAPI.getOWLAPIRoleAssertionsWithNormalizationSymbols());

		Assert.assertEquals(ontologyByStreamReader.getTBoxAxioms(), ontologyByOWLAPI.getTBoxAxioms());
		Set<OWLClass> set1 = ontologyByStreamReader.getConceptNamesInSignature();
		HashSet<OWLClass> cpSet1 = new HashSet<>(set1);
		Set<OWLClass> set2 = ontologyByOWLAPI.getConceptNamesInSignature();
		// HashSet cpSet2 = new HashSet<>(set2);
		System.out.println(set1.size());
		System.out.println(set2.size());

		set1.removeAll(set2);
		set2.removeAll(cpSet1);
		System.out.println("set1");
		PrintingHelper.printSet(set1);
		System.out.println("set2");
		PrintingHelper.printSet(set2);

		Assert.assertEquals(ontologyByStreamReader.getConceptNamesInSignature(),
				ontologyByOWLAPI.getConceptNamesInSignature());

		Assert.assertEquals(ontologyByStreamReader.getRoleNamesInSignature(),
				ontologyByOWLAPI.getRoleNamesInSignature());

	}
}
