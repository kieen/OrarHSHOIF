package orar.modeling.ontology;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import orar.data.NormalizationDataFactory;
import orar.dlfragmentvalidator.DLConstructor;
import orar.dlfragmentvalidator.DLFragment;
import orar.dlfragmentvalidator.ValidatorDataFactory;
import orar.modeling.conceptassertion.ConceptAssertionBox;
import orar.modeling.conceptassertion.MapbasedConceptAssertionBox;
import orar.modeling.roleassertion.MapbasedRoleAssertionBox;
import orar.modeling.roleassertion.RoleAssertionBox;
import orar.modeling.sameas.MapbasedSameAsBox;
import orar.modeling.sameas.SameAsBox;

public class MapbasedOrarOntology implements OrarOntology {
	private static final Logger logger= Logger.getLogger(MapbasedOrarOntology.class);
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
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory owlDataFactory = manager.getOWLDataFactory();
		OWLClass thingConcept = OWLManager.getOWLDataFactory().getOWLThing();
		Set<OWLNamedIndividual> allIndividuals = this.getIndividualsInSignature();
		Set<OWLClassAssertionAxiom> conceptAssertions= new HashSet<>();
		for (OWLNamedIndividual a:allIndividuals){
			Set<OWLNamedIndividual> sameasOfa = this.getSameIndividuals(a);
			sameasOfa.add(a);
//			logger.info(" ***********sameas a:"+sameasOfa);
			Set<OWLClass> assertedConcepts= new HashSet<>(); 
			for (OWLNamedIndividual eachInd:sameasOfa){
				assertedConcepts.addAll(this.getAssertedConcepts(eachInd));
			}
//			logger.info(" ***********asserted concepts:"+assertedConcepts);
			for (OWLClass eachConcept:assertedConcepts){
				boolean isNotIndividualByNormalization = !ValidatorDataFactory.getInstance()
						.getNamedIndividualGeneratedDuringValidation().contains(a);
				boolean isNotConceptByNormalization = !NormalizationDataFactory.getInstance()
						.getConceptsByNormalization().contains(eachConcept);
				boolean isNotThingConcept = !eachConcept.equals(thingConcept);
				if (isNotConceptByNormalization && isNotIndividualByNormalization && isNotThingConcept) {

					OWLClassAssertionAxiom classAssertion = owlDataFactory.getOWLClassAssertionAxiom(eachConcept, a);
					conceptAssertions.add(classAssertion);
				}
			}
			
		}
		return conceptAssertions;
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

	@Override
	public Set<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual individual) {
		Set<OWLNamedIndividual> equalIndividuals = this.sameasBox.getSameIndividuals(individual);
		if (equalIndividuals == null) {
			equalIndividuals = new HashSet<>();
		}
		return equalIndividuals;
	}

	@Override
	public Set<OWLClass> getAssertedConcepts(OWLNamedIndividual individual) {
		return this.conceptAssertionBox.getAssertedConcepts(individual);
	}

	@Override
	public Map<OWLObjectProperty, Set<OWLNamedIndividual>> getSuccessorRoleAssertionsAsMap(
			OWLNamedIndividual individual) {
		return this.roleAssertionBox.getSuccesorRoleAssertionsAsMap(individual);
	}

	@Override
	public Map<OWLObjectProperty, Set<OWLNamedIndividual>> getPredecessorRoleAssertionsAsMap(
			OWLNamedIndividual objectIndividual) {
		return this.roleAssertionBox.getPredecessorRoleAssertionsAsMap(objectIndividual);
	}

	@Override
	public Set<OWLNamedIndividual> getPredecessors(OWLNamedIndividual object, OWLObjectProperty role) {
		Map<OWLObjectProperty, Set<OWLNamedIndividual>> predecessorAssertionAsMap = this.roleAssertionBox
				.getPredecessorRoleAssertionsAsMap(object);
		Set<OWLNamedIndividual> setOfPredecessors = predecessorAssertionAsMap.get(role);
		if (setOfPredecessors == null) {
			setOfPredecessors = new HashSet<>();
		}
		return setOfPredecessors;
	}

	@Override
	public Set<OWLNamedIndividual> getSuccessors(OWLNamedIndividual subject, OWLObjectProperty role) {
		Map<OWLObjectProperty, Set<OWLNamedIndividual>> successorAssertionAsMap = this.roleAssertionBox
				.getSuccesorRoleAssertionsAsMap(subject);
		Set<OWLNamedIndividual> setOfSuccessors = successorAssertionAsMap.get(role);
		if (setOfSuccessors == null) {
			setOfSuccessors = new HashSet<>();
		}
		return setOfSuccessors;
	}

	@Override
	public SameAsBox getSameasBox() {

		return this.sameasBox;
	}

	@Override
	public Set<OWLNamedIndividual> getSubjectsInRoleAssertions(OWLObjectProperty role) {

		return this.roleAssertionBox.getSubjectsInRoleAssertions(role);
	}

	@Override
	public Set<OWLNamedIndividual> getObjectsInRoleAssertions(OWLObjectProperty role) {

		return this.roleAssertionBox.getObjectsInRoleAssertions(role);
	}

	@Override
	public Set<OWLNamedIndividual> getPredecessorsTakingEqualityIntoAccount(OWLNamedIndividual object,
			OWLObjectProperty role) {
		Set<OWLNamedIndividual> subjects = new HashSet<>();
		Set<OWLNamedIndividual> equivalentIndsOf_object = this.getSameIndividuals(object);
		equivalentIndsOf_object.add(object);
		for (OWLNamedIndividual eachobject : equivalentIndsOf_object) {
			subjects.addAll(getPredecessors(eachobject, role));
		}
		return subjects;
	}

	@Override
	public Set<OWLNamedIndividual> getSuccessorsTakingEqualityIntoAccount(OWLNamedIndividual subject,
			OWLObjectProperty role) {
		Set<OWLNamedIndividual> objects = new HashSet<>();
		Set<OWLNamedIndividual> equivalentIndsOf_subject = getSameIndividuals(subject);
		equivalentIndsOf_subject.add(subject);
		for (OWLNamedIndividual eachSubject : equivalentIndsOf_subject) {
			objects.addAll(getSuccessors(eachSubject, role));
		}
		return objects;
	}

	@Override
	public boolean addSameasAssertion(Set<OWLNamedIndividual> setOfSameasIndividuals) {

		return this.sameasBox.addSameasAssertions(setOfSameasIndividuals);
	}

	@Override
	public int getNumberOfConceptAssertions() {
		
		return this.conceptAssertionBox.getNumberOfConceptAssertions();
	}

	@Override
	public int getNumberOfRoleAssertions() {
		
		return this.roleAssertionBox.getNumberOfRoleAssertions();
	}

}
