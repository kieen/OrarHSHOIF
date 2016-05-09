package orar.normalization.DLLiteR;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.normalization.ALCHOI.ALCHOI_Normalizer;

public class DLLiteR_Normalizer extends ALCHOI_Normalizer {

	public DLLiteR_Normalizer(OWLOntology inputOntology) {
		super(inputOntology);
		/*
		 * Normalization is the same as in ALCHOI. Actually less things need to
		 * be done but since we already removed all non-DLLiteR axioms, the
		 * process remains the same as in ALCHOI.
		 */
	}

}
