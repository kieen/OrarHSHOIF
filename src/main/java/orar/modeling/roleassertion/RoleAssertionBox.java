package orar.modeling.roleassertion;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

/**
 * Interface for role assertions store.
 * 
 * @author kien
 *
 */

public interface RoleAssertionBox {

	/**
	 * add an assertion: role(subject,object)
	 * 
	 * @param subject
	 * @param role
	 * @param object
	 * @return true if the assertion is NEWLY added, false otherwise
	 */
	boolean addRoleAssertion(OWLNamedIndividual subject, OWLObjectProperty role, OWLNamedIndividual object);

	/**
	 * @return the number of role assertions when the ontology is first created.
	 */
	public int getNumberOfRoleAssertions();

	/**
	 * @return get all OWLAPI role assertions, including ones for individuals
	 *         generated during normalization,... Note: this method is only used
	 *         for testing purpose; it should not be used to iterate over all
	 *         assertion due to inefficiency.
	 */
	public Set<OWLObjectPropertyAssertionAxiom> getOWLAPIRoleAssertions();

}
