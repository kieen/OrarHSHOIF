package orar.abstraction;

import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import orar.type.IndividualType;

public interface AbstractionGenerator {

	// public void generateAbstractOntology();

	public OWLOntology getAbstractOntology(Map<IndividualType, Set<OWLNamedIndividual>> typeMap2Individuals);
}
