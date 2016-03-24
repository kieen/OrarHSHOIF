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
	 * @return a set of all individuals occurring in role assertions.
	 */
	public Set<OWLNamedIndividual> getAllIndividuals();

	/**
	 * add an assertion: role(subject,object)
	 * 
	 * @param subject
	 * @param role
	 * @param object
	 * @return true if the assertion is NEWLY added, false otherwise
	 */
	public boolean addRoleAssertion(OWLNamedIndividual subject, OWLObjectProperty role, OWLNamedIndividual object);

	/**
	 * @return the number of role assertions when the ontology is first created.
	 *         This number is computed directly from the up-to-date maps storing
	 *         role assertions.
	 */
	public int getNumberOfRoleAssertions();

	/**
	 * @return get all OWLAPI role assertions, including ones for individuals
	 *         generated during normalization,... Note: this method is only used
	 *         for testing purpose; it should not be used to iterate over all
	 *         assertion due to inefficiency.
	 */
	public Set<OWLObjectPropertyAssertionAxiom> getOWLAPIRoleAssertions();

	/**
	 * @param role
	 * @return a set of subjects occurring in all role assertions of the
	 *         {@code role}
	 */
	public Set<OWLNamedIndividual> getSubjectsInRoleAssertions(OWLObjectProperty role);

	/**
	 * @param role
	 * @return a set of objects occurring in all role assertions of the
	 *         {@code role}
	 */
	public Set<OWLNamedIndividual> getObjectsInRoleAssertions(OWLObjectProperty role);

}
