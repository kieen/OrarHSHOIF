package orar.modeling.ontology;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import orar.dlfragment.DLConstructor;
import orar.dlfragment.DLFragment;
import orar.modeling.conceptassertion.ConceptAssertionBox;
import orar.modeling.conceptassertion.MapbasedConceptAssertionBox;
import orar.modeling.roleassertion.MapbasedRoleAssertionBox;
import orar.modeling.roleassertion.RoleAssertionBox;
import orar.modeling.sameas.MapbasedSameAsBox;
import orar.modeling.sameas.SameAsBox;

public class MapbasedOrarOntology implements OrarOntology {
	/*
	 * TBox axioms
	 */
	private final Set<OWLAxiom> tboxAxioms;

	/*
	 * Assertions
	 */
	private final ConceptAssertionBox conceptAssertionBox;
	private final RoleAssertionBox roleAssertionBox;
	private final SameAsBox sameasBox;

	/*
	 * Signature
	 */
	private final Set<OWLNamedIndividual> individualsInSignature;
	private final Set<OWLClass> conceptNamesInSignature;
	private final Set<OWLObjectProperty> roleNamesInSignature;

	private int numberOfInputConceptAssertions = 0;
	private int numberOfInputRoleAssertions = 0;
	/*
	 * Constructors
	 */
	/**
	 * constructors actually occurring in the ontology.
	 */
	private Set<DLConstructor> actualDLConstructors;
	/**
	 * the DL logic that the algorithm is designed for.
	 */
	private DLFragment targetDLFragment;

	public MapbasedOrarOntology() {
		this.tboxAxioms = new HashSet<>();
		this.conceptAssertionBox = new MapbasedConceptAssertionBox();
		this.roleAssertionBox = new MapbasedRoleAssertionBox();
		this.sameasBox = new MapbasedSameAsBox();

		this.individualsInSignature = new HashSet<>();
		this.conceptNamesInSignature = new HashSet<>();
		this.roleNamesInSignature = new HashSet<>();
		this.actualDLConstructors = new HashSet<>();
	}

	/*
	 * Methods
	 */
	@Override
	public Set<OWLNamedIndividual> getIndividualsInSignature() {
		return this.individualsInSignature;
	}

	@Override
	public Set<OWLClass> getConceptNamesInSignature() {

		return this.conceptNamesInSignature;
	}

	@Override
	public Set<OWLObjectProperty> getRoleNamesInSignature() {

		return this.roleNamesInSignature;
	}

	@Override
	public Set<OWLAxiom> getTBoxAxioms() {

		return this.tboxAxioms;
	}

	@Override
	public void addIndividualToSignature(OWLNamedIndividual individual) {
		this.individualsInSignature.add(individual);

	}

	@Override
	public void addConceptNameToSignature(OWLClass atomicClass) {
		this.conceptNamesInSignature.add(atomicClass);

	}

	@Override
	public void addRoleNameToSignature(OWLObjectProperty atomicRole) {
		this.roleNamesInSignature.add(atomicRole);

	}

	@Override
	public void addTBoxAxioms(Set<OWLAxiom> tboxAxioms) {
		this.tboxAxioms.addAll(tboxAxioms);

	}

	@Override
	public void addTBoxAxiom(OWLAxiom tboxAxiom) {
		this.tboxAxioms.add(tboxAxiom);

	}

	@Override
	public void addIndividualsToSignature(Set<OWLNamedIndividual> individuals) {
		this.individualsInSignature.addAll(individuals);

	}

	@Override
	public void addConceptNamesToSignature(Set<OWLClass> conceptNames) {
		this.conceptNamesInSignature.addAll(conceptNames);

	}

	@Override
	public void addRoleNamesToSignature(Set<OWLObjectProperty> atomicRoles) {
		this.roleNamesInSignature.addAll(atomicRoles);

	}

	@Override
	public int getNumberOfInputConceptAssertions() {

		return this.numberOfInputConceptAssertions;
	}

	@Override
	public int getNumberOfInputRoleAssertions() {

		return this.numberOfInputRoleAssertions;
	}

	@Override
	public void setNumberOfInputRoleAssertions(int ra) {
		this.numberOfInputRoleAssertions = ra;

	}

	@Override
	public void setNumberOfInputConceptAssertions(int ca) {
		this.numberOfInputConceptAssertions = ca;

	}

	@Override
	public Set<OWLClassAssertionAxiom> getOWLAPIConceptAssertionsWithNormalizationSymbols() {

		return this.conceptAssertionBox.getOWLAPIConceptAssertions();
	}

	@Override
	public Set<OWLClassAssertionAxiom> getOWLAPIConceptAssertionsWHITOUTNormalizationSymbols() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLObjectPropertyAssertionAxiom> getOWLAPIRoleAssertionsWithNormalizationSymbols() {
		return this.roleAssertionBox.getOWLAPIRoleAssertions();
	}

	@Override
	public Set<OWLClassAssertionAxiom> getOWLAPIRoleAssertionsWITHOUTNormalizationSymbols() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTargetDLFragment(DLFragment targetDLFragment) {
		this.targetDLFragment = targetDLFragment;
	}

	@Override
	public DLFragment getTargetDLFragment() {

		return this.targetDLFragment;
	}

	@Override
	public Set<DLConstructor> getActualDLConstructors() {

		return this.actualDLConstructors;
	}

	@Override
	public void setActualDLConstructors(Set<DLConstructor> constructorsOccuringInOntology) {
		this.actualDLConstructors = constructorsOccuringInOntology;

	}

	@Override
	public boolean addConceptAssertion(OWLNamedIndividual individual, OWLClass concept) {
		return this.conceptAssertionBox.addConceptAssertion(individual, concept);

	}

	@Override
	public boolean addManyConceptAssertions(OWLNamedIndividual individual, Set<OWLClass> concepts) {

		return this.conceptAssertionBox.addManyConceptAssertions(individual, concepts);
	}

	@Override
	public boolean addRoleAssertion(OWLNamedIndividual subject, OWLObjectProperty role, OWLNamedIndividual object) {
		return this.roleAssertionBox.addRoleAssertion(subject, role, object);

	}

	@Override
	public boolean addSameAsAssertion(OWLNamedIndividual individual, OWLNamedIndividual equalIndividual) {
		return this.sameasBox.addSameAsAssertion(individual, equalIndividual);

	}

	@Override
	public boolean addManySameAsAssertions(OWLNamedIndividual individual,
			Set<OWLNamedIndividual> manyEqualIndividuals) {
		return this.sameasBox.addManySameAsAssertions(individual, manyEqualIndividuals);

	}

}
