package orar.innerexplanation;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import orar.util.PrintingHelper;

public class InnerConsistencyChecker_Hermit_test {

	@Test
	public void test1() throws OWLOntologyCreationException {
		String ontologyPath = "src/test/resources/explanation/inconsistentOnt1.owl";
		Set<Explanation<OWLAxiom>> explanations = getExplanations(ontologyPath);
		int count=0;
		for (Explanation<OWLAxiom> eachExplanation:explanations){
			System.out.println("Explanation number #"+ ++count);
			HashSet<OWLAxiom> explanationsMinusEntailment = new HashSet<>(eachExplanation.getAxioms());
			explanationsMinusEntailment.remove(eachExplanation.getEntailment());
			PrintingHelper.printSet(explanationsMinusEntailment);
		}
		
	}

	@Test
	public void test2() throws OWLOntologyCreationException {
		String ontologyPath = "src/test/resources/explanation/inconsistentOnt2.owl";
		Set<Explanation<OWLAxiom>> explanations = getExplanations(ontologyPath);
		int count=0;
		for (Explanation<OWLAxiom> eachExplanation:explanations){
			System.out.println("Explanation number #"+ ++count);
			HashSet<OWLAxiom> explanationsMinusEntailment = new HashSet<>(eachExplanation.getAxioms());
			explanationsMinusEntailment.remove(eachExplanation.getEntailment());
			PrintingHelper.printSet(explanationsMinusEntailment);
		}
		
	}
	
	private Set<Explanation<OWLAxiom>> getExplanations(String ontologyPath) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(ontologyPath));
		InnerConsistencyChecker innerConsistencyChecker = new InnerConsistencyChecker_Hermit(ontology, 1000);
		return innerConsistencyChecker.getExplanations(5);
	}

}
