package orar.refinement.assertiontransferring.HornSHOIF;

import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import orar.modeling.ontology.OrarOntology;
import orar.refinement.abstractroleassertion.AbstractRoleAssertionBox;
import orar.refinement.assertiontransferring.AssertionTransporterTemplate;

public class HornSHOIF_AssertionTransporter extends AssertionTransporterTemplate {

	public HornSHOIF_AssertionTransporter(OrarOntology orarOntoloy,
			Map<OWLNamedIndividual, Set<OWLClass>> abstractConceptAssertionsAsMap,
			AbstractRoleAssertionBox abstractRoleAssertionBox,
			Map<OWLNamedIndividual, Set<OWLNamedIndividual>> abstractSameasMap) {
		super(orarOntoloy);
		this.abstractConceptAssertionsAsMap = abstractConceptAssertionsAsMap;
		this.abstractRoleAssertionBox = abstractRoleAssertionBox;
		this.abstractSameasMap = abstractSameasMap;
	}

	@Override
	protected void addSameasAssertions() {
		//TODO

	}

	@Override
	protected void addRoleAssertionsForSingletonConcept() {
		// TODO

	}

	@Override
	protected void addRoleAssertionsForConceptsHavingTranRole() {
		// TODO

	}

}
