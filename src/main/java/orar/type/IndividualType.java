package orar.type;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public interface IndividualType {

	/**
	 * @return a set (copy) of concepts occurring in the type
	 */
	public Set<OWLClass> getConcepts();

	/**
	 * @return a set (copy) of successor roles occurring in the type
	 */
	public Set<OWLObjectProperty> getSuccessorRoles();

	/**
	 * @return a set (copy) of predecessor roles occurring in the type
	 */
	public Set<OWLObjectProperty> getPredecessorRoles();

	// public ExtendedPartOfType getExtendedPart();

}
