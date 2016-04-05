package orar.ruleengine;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import orar.refinement.abstractroleassertion.RoleAssertionList;

public interface RuleExecutor {

	public void materialize();

	public void incrementalMaterialize(Set<OWLNamedIndividual> setOfSameasIndividuals);

	public void incrementalMaterialize(OWLObjectPropertyAssertionAxiom roleAssertion);

	public Set<Set<OWLNamedIndividual>> getNewSameasAssertions();

	public Set<OWLObjectPropertyAssertionAxiom> getNewRoleAssertions();

	public boolean isABoxExtended();
}
