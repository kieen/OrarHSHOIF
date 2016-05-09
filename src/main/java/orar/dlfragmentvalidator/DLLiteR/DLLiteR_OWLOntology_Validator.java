package orar.dlfragmentvalidator.DLLiteR;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.dlfragmentvalidator.DLFragment;
import orar.dlfragmentvalidator.OWLOntologyValidator;

public class DLLiteR_OWLOntology_Validator extends OWLOntologyValidator {

	public DLLiteR_OWLOntology_Validator(OWLOntology inputOWLOntology) {
		super(inputOWLOntology, DLFragment.DLLITE_R);

	}

	@Override
	public void initAxiomValidator() {
		this.axiomValidator = new DLLiteR_AxiomValidator();

	}

}
