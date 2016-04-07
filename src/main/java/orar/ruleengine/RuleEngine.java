package orar.ruleengine;

import java.util.Queue;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import orar.modeling.ontology.OrarOntology;

public interface RuleEngine {

	public void materialize();

	public void incrementalMaterialize();

	public OrarOntology getOntology();
	
	public void addTodoSameasAssertions( Set<Set<OWLNamedIndividual>> todoSameasAssertions);
	public void addTodoRoleAsesrtions(Set<OWLObjectPropertyAssertionAxiom>odoRoleAssertions);
}
