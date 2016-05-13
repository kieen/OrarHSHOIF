package orar.innerexplanation;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

public interface InnerExplanation {
	/**
	 * @param maxNumberOfExplanations
	 *            maximal number of explanations the reasoner needs to compute.
	 * @return a set of explanations for inconsistency of the ontology; empty
	 *         set if the ontology is consistent.
	 */
	public Set<Set<OWLAxiom>> getExplanations(int maxNumberOfExplanations);
}
