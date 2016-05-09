package orar.dlfragmentvalidator.DLLiteR;

import orar.dlfragmentvalidator.OrarOntologyValidator;
import orar.modeling.ontology.OrarOntology;

public class DLLiteR_OrarOntology_Validator extends OrarOntologyValidator {

	public DLLiteR_OrarOntology_Validator(OrarOntology inputOrarOntology) {
		super(inputOrarOntology);
	}

	@Override
	public void initAxiomValidator() {
		this.axiomValidator = new DLLiteR_AxiomValidator();
		this.dlfrangment = "DLLite_R";
	}

}
