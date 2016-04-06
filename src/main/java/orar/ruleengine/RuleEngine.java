package orar.ruleengine;

import orar.modeling.ontology.OrarOntology;

public interface RuleEngine {

	public void materialize();

	public void incrementalMaterialize();

	public OrarOntology getOntology();
}
