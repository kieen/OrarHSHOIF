package orar.util;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public class SignatureForTestData {
	DefaultTestDataFactory testData = DefaultTestDataFactory.getInsatnce();

	OWLNamedIndividual a1 = testData.getIndividual("a1");
	OWLNamedIndividual a2 = testData.getIndividual("a2");
	OWLNamedIndividual b = testData.getIndividual("b");
	OWLNamedIndividual c = testData.getIndividual("c");

	OWLClass A = testData.getConcept("A");
	OWLClass A1 = testData.getConcept("A1");
	OWLClass A2 = testData.getConcept("A2");
	OWLClass B = testData.getConcept("B");
	OWLClass C = testData.getConcept("C");

	OWLObjectProperty R = testData.getRole("R");
	OWLObjectProperty R1 = testData.getRole("R1");
	OWLObjectProperty R2 = testData.getRole("R2");

	OWLObjectProperty S = testData.getRole("S");
}
