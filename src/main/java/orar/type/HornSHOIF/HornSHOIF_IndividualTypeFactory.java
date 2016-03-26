package orar.type.HornSHOIF;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import orar.type.IndividualType;

public interface HornSHOIF_IndividualTypeFactory {
	public IndividualType getIndividualType(Set<OWLClass> atomicConcepts, Set<OWLObjectProperty> preRoles,
			Set<OWLObjectProperty> sucRoles);
}
