package orar.innerexplanation;

import java.util.Set;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * Check consistency and compute explanations for inconsistent ontologies
 * 
 * @author kien
 *
 */
public interface InnerConsistencyChecker {

	/**
	 * @return true if the consider ontology is consistent, false otherwise
	 */
	public boolean isConsistent();

	/**
	 * @param maxNumberOfExplanations
	 *            maximal number of explanations the reasoner needs to compute.
	 * @return a set of explanations for inconsistency of the ontology; empty
	 *         set if the ontology is consistent.
	 */
	public Set<Explanation<OWLAxiom>> getExplanations(int maxNumberOfExplanations);
}
