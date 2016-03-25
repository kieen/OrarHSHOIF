package orar.normalization.transitivity;

import org.semanticweb.owlapi.model.OWLOntology;

public interface TransitivityNormalizer {
	/**
	 * add corresponding axioms wrt transitivity
	 */
	public void normalizeTransitivity();

	/**
	 * @return the resulting ontology
	 * 
	 */
	public OWLOntology getResultingOntology();
}
