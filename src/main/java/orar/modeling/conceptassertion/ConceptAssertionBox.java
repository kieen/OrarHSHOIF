package orar.modeling.conceptassertion;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import orar.modeling.roleassertion.RoleAssertionBox;

/**
 * Interface for internal data structure of class assertions. ABox consists of
 * ClassAssertionBox and {@link RoleAssertionBox}
 * 
 * @author kien
 *
 */
public interface ConceptAssertionBox {

	public Set<OWLNamedIndividual> getAllIndividuals();

	/**
	 * @param individual
	 * @return - A (possibly empty) set of concept names occurring in concept
	 *         assertions of the {@code individual}.
	 * 
	 */
	public Set<OWLClass> getAssertedConcepts(OWLNamedIndividual individual);

	/**
	 * @param concept
	 * @param individual
	 * @return true if this assertion is NEWLY added, false otherwise.
	 */
	public boolean addConceptAssertion(OWLNamedIndividual individual, OWLClass concept);

	/**
	 * Add many concept assertions.
	 * 
	 * @param concepts
	 * @param individual
	 * @return true if NEW assertions are added, false otherwise.
	 */
	public boolean addManyConceptAssertions(OWLNamedIndividual individual, Set<OWLClass> concepts);

	/**
	 * @return OWLAPI class assertions, including one for concepts and
	 *         individuals generated during normalization. This should be used
	 *         for testing only.
	 */
	public Set<OWLClassAssertionAxiom> getOWLAPIConceptAssertions();

	/**
	 * @return the number of concept assertions when the ontology is first
	 *         created.
	 */
	public int getNumberOfInitialConceptAssertions();
}
