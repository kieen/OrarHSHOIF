package orar.materializer.HornSHOIF;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntology;

import completenesschecker.CompletenessChecker;
import completenesschecker.CompletenessCheckerHorn;
import junit.framework.Assert;
import orar.config.Configuration;
import orar.config.LogInfo;
import orar.dlreasoner.DLReasoner;
import orar.dlreasoner.HermitDLReasoner;
import orar.io.ontologyreader.HornSHOIF_OntologyReader;
import orar.io.ontologyreader.OntologyReader;
import orar.materializer.Materializer;
import orar.modeling.ontology.OrarOntology;

public class HornALCHOIF_Materializer_Hermit_Test {

	/**
	 * No new entailments
	 */
	@Test
	public void test1() {

		String ontologyPath = "src/test/resources/main/test1.owl";
		haveTheSameResults(ontologyPath);
	}

	@Test
	public void test2() {
		String ontologyPath = "src/test/resources/main/test2.owl";

		haveTheSameResults(ontologyPath);
	}

	@Test
	public void test3() {
		String ontologyPath = "src/test/resources/main/test3.owl";
		haveTheSameResults(ontologyPath);
	}

	@Test
	public void test4() {
		String ontologyPath = "src/test/resources/main/test4.owl";

		haveTheSameResults(ontologyPath);
	}

	@Test
	public void test5() {
		String ontologyPath = "src/test/resources/main/test5.owl";

		haveTheSameResults(ontologyPath);
	}

	@Test
	public void test6() {
		String ontologyPath = "src/test/resources/main/test6.owl";

		haveTheSameResults(ontologyPath);
	}

	@Test
	public void testFunctional() {
		String ontologyPath = "src/test/resources/main/testFunctional.owl";

		haveTheSameResults(ontologyPath);
	}

	@Test
	public void testFunctional2() {
		String ontologyPath = "src/test/resources/main/testFunctional2.owl";

		haveTheSameResults(ontologyPath);
	}

	@Test
	public void testFunctional3() {
		String ontologyPath = "src/test/resources/main/testFunctional3.owl";

		haveTheSameResults(ontologyPath);
	}

	@Test
	public void testFunctional4() {
		String ontologyPath = "src/test/resources/main/testFunctional4.owl";
		haveTheSameResults(ontologyPath);
	}

	@Test
	public void testFunctional5() {
		String ontologyPath = "src/test/resources/main/testFunctional5.owl";

		haveTheSameResults(ontologyPath);
	}

	@Test
	public void testFunctional6() {
		String ontologyPath = "src/test/resources/main/testFunctional6.owl";

		haveTheSameResults(ontologyPath);
	}

	@Test
	public void testFunctional7() {
		String ontologyPath = "src/test/resources/main/testFunctional7.owl";

		haveTheSameResults(ontologyPath);
	}

	@Test
	public void testNominal() {
		String ontologyPath = "src/test/resources/main/testNominal.owl";

		haveTheSameResults(ontologyPath);
	}

	@Test
	public void testNominal1() {
		String ontologyPath = "src/test/resources/main/testNominal1.owl";

		haveTheSameResults(ontologyPath);
	}

	@Test
	public void testFunctionalAndNominal() {

		String ontologyPath = "src/test/resources/main/testFunctionalAndNominal.owl";

		haveTheSameResults(ontologyPath);
	}

	@Test
	public void testNonTrivialIndMerging() {
		Configuration.getInstance().addAllDebugInfos();
		String ontologyPath = "src/test/resources/main/HornALCHOIF/testNontrivialIndMerging.owl";

		haveTheSameResults(ontologyPath);
	}

	@Test
	public void testNonTrivialIndMerging1() {
		Configuration.getInstance().addAllDebugInfos();
		String ontologyPath = "src/test/resources/main/HornALCHOIF/testNontrivialIndMerging1.owl";

		haveTheSameResults(ontologyPath);
	}

	//
	// @Test
	// public void testUOBM_OX() {
	// String ontologyPath = "src/test/resources/uobm-ox/u1/univ0.owl";
	// @Test
	// public void testUOBM_OX() {
	//
	// String ontologyPath = "src/test/resources/uobm-ox/u1/univ0.owl";
	// haveTheSameResults(ontologyPath);
	// }

	/**
	 * Compare result by Abstraction and by OWLReasoner; assert that they have
	 * the same result.
	 * 
	 * @param ontologyPath
	 */
	private void haveTheSameResults(String ontologyPath) {

		Configuration.getInstance().addLoginfoLevels(LogInfo.ABSTRACTION_INFO, LogInfo.INPUTONTOLOGY_INFO,
				LogInfo.COMPARED_RESULT_INFO);

		OntologyReader ontoReader = new HornSHOIF_OntologyReader();
		OrarOntology normalizedOrarOntology = ontoReader.getNormalizedOrarOntology(ontologyPath);

		Materializer materializer = new HornSHOIF_Materialization_Hermit(normalizedOrarOntology);

		/*
		 * get result directly from Konclude reasoning over the input ontology
		 */
		OWLOntology owlOntology = ontoReader.getOWLAPIOntology(ontologyPath);

		DLReasoner koncludeRealizer = new HermitDLReasoner(owlOntology);

		CompletenessChecker checker = new CompletenessCheckerHorn();
		checker.checkCompleteness(materializer, koncludeRealizer);

		Assert.assertTrue(checker.isComplete());
	

	}
}
