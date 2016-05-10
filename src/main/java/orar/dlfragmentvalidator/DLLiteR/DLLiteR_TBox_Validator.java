package orar.dlfragmentvalidator.DLLiteR;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

import orar.dlfragmentvalidator.TBoxValidator;

public class DLLiteR_TBox_Validator extends TBoxValidator {
	public DLLiteR_TBox_Validator(Set<OWLAxiom> inputTBoxAxioms) {
		super(inputTBoxAxioms);
	}

	@Override
	public void initAxiomValidator() {
		this.axiomValidator = new DLLiteR_AxiomValidator();
		this.dlfrangment = "DLLiteR";
	}
}
