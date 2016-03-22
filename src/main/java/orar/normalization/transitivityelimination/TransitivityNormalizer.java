package orar.normalization.transitivityelimination;

import org.semanticweb.owlapi.model.OWLOntology;

public interface TransitivityNormalizer {
	/**
	 * eliminate transitivity and add corresponding axioms
	 */
	public void normalizeTransitivity();

	/**
	 * @return the resulting ontology without transivitity and is entailments
	 *         preserved
	 * 
	 */
	public OWLOntology getResultingOntology();
}
