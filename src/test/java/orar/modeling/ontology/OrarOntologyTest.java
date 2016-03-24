package orar.modeling.ontology;

import static org.junit.Assert.*;

import org.junit.Test;

import orar.util.DefaultTestDataFactory;
import orar.util.PrintingHelper;

public class OrarOntologyTest {
	DefaultTestDataFactory testData = DefaultTestDataFactory.getInsatnce();

	@Test
	public void test() {
		OrarOntology orarOntology = new MapbasedOrarOntology();
		orarOntology.addConceptAssertion(testData.getIndividual("a"), testData.getConcept("A"));
		orarOntology.addConceptAssertion(testData.getIndividual("a"), testData.getConcept("B"));
		orarOntology.addRoleAssertion(testData.getIndividual("a"), testData.getRole("R"), testData.getIndividual("b"));

		PrintingHelper.printSet(orarOntology.getOWLAPIConceptAssertionsWithNormalizationSymbols());

		PrintingHelper.printSet(orarOntology.getOWLAPIRoleAssertionsWithNormalizationSymbols());
	}

}
