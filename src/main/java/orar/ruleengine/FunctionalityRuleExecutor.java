package orar.ruleengine;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

public class FunctionalityRuleExecutor implements RuleExecutor {

	@Override
	public void materialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void incrementalMaterialize(Set<OWLNamedIndividual> setOfSameasIndividuals) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void incrementalMaterialize(OWLObjectPropertyAssertionAxiom roleAssertion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Set<OWLNamedIndividual>> getNewSameasAssertions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLObjectPropertyAssertionAxiom> getNewRoleAssertions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isABoxExtended() {
		// TODO Auto-generated method stub
		return false;
	}

}
