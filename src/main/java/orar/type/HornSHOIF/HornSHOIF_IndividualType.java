package orar.type.HornSHOIF;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import orar.type.IndividualType;

public class HornSHOIF_IndividualType implements IndividualType {

	private final Set<OWLClass> concepts;
	private final Set<OWLObjectProperty> preRoles;
	private final Set<OWLObjectProperty> sucRoles;

	HornSHOIF_IndividualType(Set<OWLClass> atomicConcepts, Set<OWLObjectProperty> preRoles,
			Set<OWLObjectProperty> sucRoles) {

		this.concepts = new HashSet<OWLClass>(atomicConcepts);
		this.preRoles = new HashSet<OWLObjectProperty>(preRoles);
		this.sucRoles = new HashSet<OWLObjectProperty>(sucRoles);

	}

	@Override
	public Set<OWLClass> getConcepts() {
		return new HashSet<>(concepts);
	}

	@Override
	public Set<OWLObjectProperty> getPredecessorRoles() {
		return new HashSet<>(preRoles);
	}

	@Override
	public Set<OWLObjectProperty> getSuccessorRoles() {
		return new HashSet<>(sucRoles);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((concepts == null) ? 0 : concepts.hashCode());
		result = prime * result + ((preRoles == null) ? 0 : preRoles.hashCode());
		result = prime * result + ((sucRoles == null) ? 0 : sucRoles.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HornSHOIF_IndividualType other = (HornSHOIF_IndividualType) obj;
		if (concepts == null) {
			if (other.concepts != null)
				return false;
		} else if (!concepts.equals(other.concepts))
			return false;
		if (preRoles == null) {
			if (other.preRoles != null)
				return false;
		} else if (!preRoles.equals(other.preRoles))
			return false;
		if (sucRoles == null) {
			if (other.sucRoles != null)
				return false;
		} else if (!sucRoles.equals(other.sucRoles))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HornSHOIF_IndividualType [concepts=" + concepts + ", preRoles=" + preRoles + ", sucRoles=" + sucRoles
				+ "]";
	}

}
