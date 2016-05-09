package orar.io.ontologyreader;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.dlfragmentvalidator.OWLOntologyValidator;
import orar.dlfragmentvalidator.DLLiteR.DLLiteR_OWLOntology_Validator;
import orar.normalization.Normalizer;
import orar.normalization.DLLiteR.DLLiteR_Normalizer;

public class DLLiteR_OntologyReader extends OntologyReaderTemplate {

	@Override
	protected OWLOntologyValidator getOntologyValidator(OWLOntology owlOntology) {

		return new DLLiteR_OWLOntology_Validator(owlOntology);
	}

	@Override
	protected Normalizer getNormalizer(OWLOntology owlOntology) {
		/*
		 * As transitivity is already "normalized", we use ALCHOIF normalizer
		 */
		return new DLLiteR_Normalizer(owlOntology);
	}

}
