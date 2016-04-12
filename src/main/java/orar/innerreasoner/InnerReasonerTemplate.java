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
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import orar.config.Configuration;
import orar.config.DebugLevel;
import orar.data.AbstractDataFactory;
import orar.data.DataForTransferingEntailments;
import orar.data.MetaDataOfOntology;
import orar.refinement.abstractroleassertion.AbstractRoleAssertionBox;
import orar.util.PrintingHelper;

public abstract class InnerReasonerTemplate implements InnerReasoner {
	/*
	 * data
	 */
	protected final Map<OWLNamedIndividual, Set<OWLClass>> conceptAssertionsMap;
	protected final Map<OWLNamedIndividual, Set<OWLNamedIndividual>> sameAsMap;
	protected final AbstractRoleAssertionBox roleAssertionList;
	protected long reasoningTime;
	/*
	 * Config
	 */
	protected final Configuration config = Configuration.getInstance();
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
	protected final Set<OWLNamedIndividual> instancesOfPredecessorOfSingletonConcept;
	protected final Set<OWLObjectProperty> rolesForPredecessorOfSingletonConcept;

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
		this.instancesOfPredecessorOfSingletonConcept = new HashSet<>();
		this.rolesForPredecessorOfSingletonConcept = new HashSet<>();
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
		if (this.config.getDebuglevels().contains(DebugLevel.ADDING_MARKING_AXIOMS)) {
			logger.info("***DEBUG*** Ontololgy after adding marking axioms:");
			PrintingHelper.printSet(this.owlOntology.getAxioms());
		}
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
		computeEntailedSameasAssertions();// varies in Horn-SHIF and Horn-SHOIF
		long endTime = System.currentTimeMillis();
		this.entailmentComputed = true;
		this.reasoningTime = (endTime - startTime) / 1000; // get seconds
		dispose();
	}

	private void computeEntailedRoleAssertions() {
		// varies in HornSHIF and HornSHOIF
		computeRoleAssertionForInstancesOfLoopConcepts();
		computeRoleAssertionForInstancesOfConceptHasTranRole();
		computeRoleAssertionForInstancesOfSingletonConcept();
		// same in HornSHIF and HornSHOIF
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
	protected abstract void computeRoleAssertionForInstancesOfLoopConcepts();

	/**
	 * compute assertions by R^3_t: N(u), M(x) -->T(u,x)
	 */
	protected abstract void computeRoleAssertionForInstancesOfConceptHasTranRole();

	/**
	 * compute assertions by R_exists: N(a), M(b), |M|=1, N SubClassOf
	 * \exists.R.M --> R(a,b)
	 */
	protected abstract void computeRoleAssertionForInstancesOfSingletonConcept();

	/**
	 * compute sameas assertions between representative of concept-types and
	 * those of types.
	 */
	protected abstract void computeEntailedSameasAssertions();

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
			} else if (this.axiomsAdder.getPredecessorOfSingletonConcepts().contains(eachConceptName)) {
				this.instancesOfPredecessorOfSingletonConcept.addAll(instances);
				OWLObjectProperty roleUsedForQuery = this.axiomsAdder.getPredecessorOfSingletonConceptMap2Role()
						.get(eachConceptName);
				if (roleUsedForQuery != null) {
					this.rolesForPredecessorOfSingletonConcept.add(roleUsedForQuery);
				}
			} else {
				/*
				 * filter out asserted instances
				 */
				Set<OWLClassAssertionAxiom> assertedAssertions = this.owlOntology
						.getClassAssertionAxioms(eachConceptName);
				Set<OWLNamedIndividual> assertedIndividuals = new HashSet<>();
				for (OWLClassAssertionAxiom eachAssertion : assertedAssertions) {
					assertedIndividuals.add(eachAssertion.getIndividual().asOWLNamedIndividual());
				}
				instances.removeAll(assertedIndividuals);
				/*
				 * filter out U-individuals (in case of Horn-SHOIF)
				 */
				instances.removeAll(this.abstractDataFactory.getUAbstractIndividuals());
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
