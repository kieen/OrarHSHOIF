package orar.explanation;

import java.util.Set;

import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;

public interface ConsistencyChecker {

	/**
	 * @return true if the consider ontology is consistent, false otherwise
	 */
	public int isConsistent();

	/**
	 * @return a set of explanations for inconsistency of the ontology; empty
	 *         set if the ontology is consistent.
	 */
	public Set<Explanation<OWLAxiom>> getExplanations();
}
