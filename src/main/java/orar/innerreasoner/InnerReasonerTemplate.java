package orar.innerreasoner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import orar.data.AbstractDataFactory;
import orar.data.DataForTransferingEntailments;
import orar.data.MetaDataOfOntology;
import orar.refinement.abstractroleassertion.AbstractRoleAssertionBox;

public abstract class InnerReasonerTemplate implements InnerReasoner {
	/*
	 * data
	 */
	protected final Map<OWLNamedIndividual, Set<OWLClass>> conceptAssertionsMap;
	protected final Map<OWLNamedIndividual, Set<OWLNamedIndividual>> sameAsMap;
	protected final AbstractRoleAssertionBox roleAssertionList;
	protected long reasoningTime;

	/*
	 * others
	 */
	private Logger logger = Logger.getLogger(InnerReasonerTemplate.class);

	protected final OWLOntology owlOntology;
	protected OWLReasoner reasoner;
	protected OWLDataFactory dataFactory;
	boolean entailmentComputed;
	protected final AbstractDataFactory abstractDataFactory;
	protected final DataForTransferingEntailments dataForTransferring;
	protected final MetaDataOfOntology metadataOfOntology;

	protected final MarkingAxiomAdder axiomsAdder;
	protected final Set<OWLNamedIndividual> instancesOfSingletonConcepts;
	protected final Set<OWLNamedIndividual> instancesOfLoopConcepts;
	protected final Set<OWLNamedIndividual> instancesOfHasTranConcepts;

	public InnerReasonerTemplate(OWLOntology owlOntology) {
		/*
		 * init data
		 */
		this.conceptAssertionsMap = new HashMap<>();
		this.sameAsMap = new HashMap<>();
		this.roleAssertionList = new AbstractRoleAssertionBox();

		/*
		 * others
		 */
		this.owlOntology = owlOntology;
		this.dataFactory = OWLManager.getOWLDataFactory();
		this.entailmentComputed = false;
		this.abstractDataFactory = AbstractDataFactory.getInstance();
		this.dataForTransferring = DataForTransferingEntailments.getInstance();

		this.metadataOfOntology = MetaDataOfOntology.getInstance();

		this.axiomsAdder = new MarkingAxiomAdder(owlOntology);
		this.instancesOfSingletonConcepts = new HashSet<>();
		this.instancesOfLoopConcepts = new HashSet<>();
		this.instancesOfHasTranConcepts = new HashSet<>();
	}

	@Override
	public Map<OWLNamedIndividual, Set<OWLClass>> getEntailedConceptAssertionsAsMap() {

		return this.conceptAssertionsMap;
	}

	@Override
	public AbstractRoleAssertionBox getEntailedRoleAssertions() {

		return this.roleAssertionList;
	}

	@Override
	public Map<OWLNamedIndividual, Set<OWLNamedIndividual>> getSameAsMap() {

		return this.sameAsMap;
	}

	/**
	 * Initialize the reasoner with {@code ontology} in the root ontology
	 * 
	 * @param ontology
	 */
	protected abstract OWLReasoner getOWLReasoner(OWLOntology ontology);

	@Override
	public void computeEntailments() {
		/*
		 * add axioms to mark singleton concepts
		 */
		this.axiomsAdder.addMarkingAxioms();
		/*
		 * get the reasoner
		 */
		this.reasoner = getOWLReasoner(owlOntology);
		/*
		 * compute entailments
		 */
		long startTime = System.currentTimeMillis();
		this.reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS, InferenceType.OBJECT_PROPERTY_ASSERTIONS,
				InferenceType.SAME_INDIVIDUAL);
		if (!reasoner.isConsistent()) {
			logger.error("Ontology inconsistent!");
		}

		computeEntailedConceptAssertions();
		computeEntailedRoleAssertions();
		computeEntailedSameasAssertions();
		long endTime = System.currentTimeMillis();
		this.entailmentComputed = true;
		dispose();
		this.reasoningTime = (endTime - startTime) / 1000; // get seconds
	}

	private void computeEntailedRoleAssertions() {
		computeRoleAssertionForInstancesOfLoopConcepts();
		computeRoleAssertionForInstancesOfConceptHasTranRole();
		computeRoleAssertionForInstancesOfSingletonConcept();
		computeRoleAssertionsForXCausedByFunctionalRoles();
		computeRoleAssertionsForZCausedByInverseFunctionalRoles();
	}

	/**
	 * assertions from the R^2_{leq}
	 */
	private void computeRoleAssertionsForXCausedByFunctionalRoles() {
		Set<OWLNamedIndividual> xIndividuals = this.dataForTransferring.getxAbstractHavingFunctionalRole();
		Set<OWLObjectProperty> funcRoles = this.metadataOfOntology.getFunctionalRoles();

		for (OWLNamedIndividual x : xIndividuals) {
			for (OWLObjectProperty role : funcRoles) {
				Set<OWLNamedIndividual> objects = reasoner.getObjectPropertyValues(x, role).getFlattened();
				for (OWLNamedIndividual object : objects) {
					/*
					 * only add if the object is some y
					 */
					if (this.abstractDataFactory.getYAbstractIndividuals().contains(object)) {
						this.roleAssertionList.addXY_RoleAssertionForType(x, role, object);
					}
				}
			}
		}
	}

	/**
	 * assertions from the rule R^2_{leq}
	 */
	private void computeRoleAssertionsForZCausedByInverseFunctionalRoles() {
		Set<OWLNamedIndividual> zIndividuals = this.dataForTransferring.getzAbstractHavingInverseFunctionalRole();
		Set<OWLObjectProperty> inverseFuncRoles = this.metadataOfOntology.getInverseFunctionalRoles();

		for (OWLNamedIndividual z : zIndividuals) {
			for (OWLObjectProperty role : inverseFuncRoles) {
				Set<OWLNamedIndividual> objects = reasoner.getObjectPropertyValues(z, role).getFlattened();
				for (OWLNamedIndividual object : objects) {
					/*
					 * only add if the object is some x
					 */
					if (this.abstractDataFactory.getXAbstractIndividuals().contains(object)) {
						this.roleAssertionList.addZX_RoleAssertionForType(z, role, object);
					}
				}
			}
		}

	}

	/**
	 * compute assertions by R_t^2, (N(u)-->T(u,u))
	 */
	private void computeRoleAssertionForInstancesOfLoopConcepts() {
		Set<OWLNamedIndividual> individuals = new HashSet<>(this.instancesOfLoopConcepts);
		/*
		 * retain only U-individuals.
		 */
		individuals.retainAll(this.abstractDataFactory.getUAbstractIndividuals());
		for (OWLNamedIndividual eachU : individuals) {
			for (OWLObjectProperty tranRol : this.metadataOfOntology.getTransitiveRoles()) {
				Set<OWLNamedIndividual> objects = reasoner.getObjectPropertyValues(eachU, tranRol).getFlattened();
				if (objects.contains(eachU)) {
					this.roleAssertionList.addUU_LoopRoleAssertionForCType(eachU, tranRol);
				}
			}
		}
	}

	/**
	 * compute assertions by R^3_t: N(u), M(x) -->T(u,x)
	 */
	private void computeRoleAssertionForInstancesOfConceptHasTranRole() {
		Set<OWLNamedIndividual> individuals = new HashSet<>(this.instancesOfHasTranConcepts);
		/*
		 * retain only U-individuals.
		 */
		individuals.retainAll(this.abstractDataFactory.getUAbstractIndividuals());

		for (OWLNamedIndividual eachU : individuals) {
			for (OWLObjectProperty tranRole : this.metadataOfOntology.getTransitiveRoles()) {
				/*
				 * query for tranRole
				 */
				Set<OWLNamedIndividual> objects = reasoner.getObjectPropertyValues(eachU, tranRole).getFlattened();
				/*
				 * retain only x-individuals
				 */
				objects.retainAll(this.abstractDataFactory.getXAbstractIndividuals());

				for (OWLNamedIndividual eachObject : objects) {
					this.roleAssertionList.addUX_RoleAssertionForCTypeAndType(eachU, tranRole, eachObject);
				}

				/*
				 * query for inverse of tranRole
				 */
				OWLObjectInverseOf invTranRole = this.dataFactory.getOWLObjectInverseOf(tranRole);
				Set<OWLNamedIndividual> subjects = reasoner.getObjectPropertyValues(eachU, invTranRole).getFlattened();
				/*
				 * retain only x-individuals
				 */
				subjects.retainAll(this.abstractDataFactory.getXAbstractIndividuals());

				for (OWLNamedIndividual eachSubject : subjects) {
					this.roleAssertionList.addUX_RoleAssertionForCTypeAndType(eachSubject, tranRole, eachU);
				}

			}
		}
	}

	private void computeRoleAssertionForInstancesOfSingletonConcept() {
		Set<OWLNamedIndividual> individuals = new HashSet<>(this.instancesOfSingletonConcepts);
		/*
		 * retain only U-individuals.
		 */
		individuals.retainAll(this.abstractDataFactory.getUAbstractIndividuals());
		for (OWLNamedIndividual eachU : individuals) {
			for (OWLObjectProperty role : this.owlOntology.getObjectPropertiesInSignature(true)) {
				/*
				 * query for assertion of the form role(eachU, ?x)
				 */
				Set<OWLNamedIndividual> objects = reasoner.getObjectPropertyValues(eachU, role).getFlattened();
				/*
				 * retain only x-individuals
				 */
				objects.retainAll(this.abstractDataFactory.getXAbstractIndividuals());
				for (OWLNamedIndividual eachObject : objects) {
					this.roleAssertionList.addUX_RoleAssertionForCTypeAndType(eachU, role, eachObject);
				}

				/*
				 * query for assertion of the form role(x, eachU)
				 */
				OWLObjectInverseOf inverseRole = this.dataFactory.getOWLObjectInverseOf(role);
				Set<OWLNamedIndividual> subjects = reasoner.getObjectPropertyValues(eachU, inverseRole).getFlattened();
				/*
				 * retain only x-individuals
				 */
				subjects.retainAll(this.abstractDataFactory.getXAbstractIndividuals());
				for (OWLNamedIndividual eachSubject : subjects) {
					this.roleAssertionList.addUX_RoleAssertionForCTypeAndType(eachSubject, role, eachU);
				}
			}
		}
	}

	/**
	 * compute sameas assertions between representative of concept-types and
	 * those of types.
	 */
	private void computeEntailedSameasAssertions() {
		Set<OWLNamedIndividual> allIndividualsFromConceptType = this.abstractDataFactory.getUAbstractIndividuals();
		for (OWLNamedIndividual indiv : allIndividualsFromConceptType) {
			Set<OWLNamedIndividual> equivalentIndividuals = reasoner.getSameIndividuals(indiv).getEntities();
			/*
			 * Note to remove the indv itself as we DONT use (u=u) to transfer
			 * assertions.
			 */
			equivalentIndividuals.remove(indiv);
			if (!equivalentIndividuals.isEmpty()) {
				this.sameAsMap.put(indiv, equivalentIndividuals);
			}
		}

	}

	/**
	 * compute NEWLY derived concept assertions
	 */
	private void computeEntailedConceptAssertions() {
		Set<OWLClass> allConceptsNames = this.owlOntology.getClassesInSignature(true);
		allConceptsNames.remove(OWLManager.getOWLDataFactory().getOWLThing());

		for (OWLClass eachConceptName : allConceptsNames) {
			Set<OWLNamedIndividual> instances = this.reasoner.getInstances(eachConceptName, false).getFlattened();

			if (this.axiomsAdder.getSingletonConcepts().contains(eachConceptName)) {
				this.instancesOfSingletonConcepts.addAll(instances);
			} else if (this.axiomsAdder.getLoopConcepts().contains(eachConceptName)) {
				this.instancesOfLoopConcepts.addAll(instances);
			} else if (this.axiomsAdder.getHasTranConcepts().contains(eachConceptName)) {
				this.instancesOfHasTranConcepts.addAll(instances);
			} else {
				Set<OWLClassAssertionAxiom> assertedAssertions = this.owlOntology
						.getClassAssertionAxioms(eachConceptName);
				Set<OWLNamedIndividual> assertedIndividuals = new HashSet<>();
				for (OWLClassAssertionAxiom eachAssertion : assertedAssertions) {
					assertedIndividuals.add(eachAssertion.getIndividual().asOWLNamedIndividual());
				}
				instances.removeAll(assertedIndividuals);
				putIndividual2ConceptMap(instances, eachConceptName);
			}
		}

	}

	/**
	 * Put instances of a concept to the map: individual --> its concepts.
	 * 
	 * @param instances
	 * @param concept
	 * @param individual2ItsConcepts
	 */
	private void putIndividual2ConceptMap(Set<OWLNamedIndividual> instances, OWLClass concept) {
		for (OWLNamedIndividual ind : instances) {
			Set<OWLClass> concepts = this.conceptAssertionsMap.get(ind);
			if (concepts == null) {
				concepts = new HashSet<>();
			}
			concepts.add(concept);
			this.conceptAssertionsMap.put(ind, concepts);

		}
	}

	@Override
	public long getReasoningTime() {
		return this.reasoningTime;
	}

	protected abstract void dispose();
}
