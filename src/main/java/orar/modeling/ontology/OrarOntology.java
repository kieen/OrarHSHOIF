package orar.modeling.ontology;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import orar.dlfragmentvalidator.DLConstructor;
import orar.dlfragmentvalidator.DLFragment;

/**
 * Internal representation of an ontology.
 * 
 * @author kien
 * 
 * 
 */
public interface OrarOntology {

	/*
	 * Signature:getters
	 */
	public Set<OWLNamedIndividual> getIndividualsInSignature();

	public Set<OWLClass> getConceptNamesInSignature();

	public Set<OWLObjectProperty> getRoleNamesInSignature();

	/*
	 * Signagure:setters/adders
	 */
	public void addIndividualToSignature(OWLNamedIndividual individual);

	public void addIndividualsToSignature(Set<OWLNamedIndividual> individuals);

	public void addConceptNameToSignature(OWLClass atomicClass);

	public void addConceptNamesToSignature(Set<OWLClass> conceptNames);

	public void addRoleNameToSignature(OWLObjectProperty atomicRole);

	public void addRoleNamesToSignature(Set<OWLObjectProperty> atomicRoles);

	/*
	 * Number of assertions when the ontology was first created: getters
	 */
	public int getNumberOfInputConceptAssertions();

	public int getNumberOfInputRoleAssertions();

	/*
	 * Number of assertions when the ontology was first created: setters
	 */
	public void setNumberOfInputRoleAssertions(int numberOfInputRoleAssertions);

	public void setNumberOfInputConceptAssertions(int numberOfInputConceptAssertions);

	/*
	 * Methods for testing correctness
	 */
	/**
	 * Get all OWLAPI concept assertions, <b> INCLUDING </b> those for
	 * individuals/concepts generated during NORMALIZATION and DL-PROFILE
	 * Extraction.<br>
	 * This method is not efficient and only used for checking
	 * correctness/comparing results with other tools via OWLAPI.
	 * 
	 * @return a set of all OWLAPI concept assertions of the ontology. Note that
	 *         this include those for concepts/individuals generated during the
	 *         normalization and profile validation phase.
	 */
	public Set<OWLClassAssertionAxiom> getOWLAPIConceptAssertionsWithNormalizationSymbols();

	/**
	 * Get all OWLAPI concept assertions, <b> EXCLUDING </b> those for
	 * individuals/concepts generated during NORMALIZATION and DL-PROFILE
	 * Extraction.<br>
	 * This method is not efficient and only used for checking
	 * correctness/comparing results with other tools via OWLAPI.
	 * 
	 * @return a set of OWLAPI concept assertions after removing those of
	 *         concepts/individuals generated during normalization and profile
	 *         validation phase.
	 */
	public Set<OWLClassAssertionAxiom> getOWLAPIConceptAssertionsWHITOUTNormalizationSymbols();

	/**
	 * Get all OWLAPI role assertions, <b> INCLUDING </b> those for
	 * individuals/concepts generated during NORMALIZATION and DL-PROFILE
	 * Extraction.<br>
	 * This method is not efficient and only used for checking
	 * correctness/comparing results with other tools via OWLAPI.
	 * 
	 * @return a set of all OWLAPI role assertions of the ontology.
	 */
	public Set<OWLObjectPropertyAssertionAxiom> getOWLAPIRoleAssertionsWithNormalizationSymbols();

	/**
	 * Get all OWLAPI role assertions, <b> EXCLUDING </b> those for
	 * individuals/concepts generated during NORMALIZATION and DL-PROFILE
	 * Extraction.<br>
	 * This method is not efficient and only used for checking
	 * correctness/comparing results with other tools via OWLAPI.
	 * 
	 * @return a set of OWLAPI concept assertions after removing those of
	 *         concepts/individuals generated during normalization and profile
	 *         validation phase.
	 */
	public Set<OWLClassAssertionAxiom> getOWLAPIRoleAssertionsWITHOUTNormalizationSymbols();

	/*
	 * Methods for DL fragments
	 */
	/**
	 * @return set target DL Fragment of this ontology. A Target DL Fragment is
	 *         the DL for which algorithms guarantee soundness and completeness.
	 */
	public void setTargetDLFragment(DLFragment targetDLFragment);

	/**
	 * @return get target DL Fragment of this ontology. A Target DL Fragment is
	 *         the DL for which algorithms guarantee soundness and completeness.
	 */
	public DLFragment getTargetDLFragment();

	/**
	 * @return get constructors really occurring in this ontology. This will be
	 *         used to design suitable optimization, e.g. optimizations for
	 *         ontology without nominals will be different from the ones with
	 *         nominals.
	 */
	public Set<DLConstructor> getActualDLConstructors();

	public void setActualDLConstructors(Set<DLConstructor> constructors);

	/*
	 * TBox
	 */
	/**
	 * @return a set of OWLAPI TBox axioms, including role-axioms
	 */
	public Set<OWLAxiom> getTBoxAxioms();

	public void addTBoxAxioms(Set<OWLAxiom> tboxAxioms);

	public void addTBoxAxiom(OWLAxiom tboxAxiom);

	/*
	 * Adding concept assertions
	 */
	public boolean addConceptAssertion(OWLNamedIndividual individual, OWLClass concept);

	public boolean addManyConceptAssertions(OWLNamedIndividual originalInd, Set<OWLClass> concepts);

	/*
	 * Adding role assertions
	 */
	public boolean addRoleAssertion(OWLNamedIndividual subject, OWLObjectProperty role, OWLNamedIndividual object);

	/*
	 * Adding sameas assertion
	 */
	public boolean addSameAsAssertion(OWLNamedIndividual individual, OWLNamedIndividual equalIndividual);

	public boolean addManySameAsAssertions(OWLNamedIndividual individual, Set<OWLNamedIndividual> equalIndividuals);

	
}
