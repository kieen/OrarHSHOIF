package orar.inerreasoner;

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
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import orar.data.AbstractDataFactory;
import orar.refinement.abstractroleassertion.EntailedAbstractRoleAssertions;

public abstract class InnerReasonerTemplate implements InnerReasoner {
	/*
	 * data
	 */
	protected final Map<OWLNamedIndividual, Set<OWLClass>> conceptAssertionsMap;
	protected final Map<OWLNamedIndividual, Set<OWLNamedIndividual>> sameAsMap;
	protected final EntailedAbstractRoleAssertions roleAssertionList;
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

	public InnerReasonerTemplate(OWLOntology owlOntology) {
		/*
		 * init data
		 */
		this.conceptAssertionsMap = new HashMap<>();
		this.sameAsMap = new HashMap<>();
		this.roleAssertionList = new EntailedAbstractRoleAssertions();

		/*
		 * others
		 */
		this.owlOntology = owlOntology;
		this.dataFactory = OWLManager.getOWLDataFactory();
		this.entailmentComputed = false;
		this.abstractDataFactory = AbstractDataFactory.getInstance();
	}

	@Override
	public Map<OWLNamedIndividual, Set<OWLClass>> getEntailedConceptAssertionsAsMap() {

		return this.conceptAssertionsMap;
	}

	@Override
	public EntailedAbstractRoleAssertions getEntailedRoleAssertions() {

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

		long startTime = System.currentTimeMillis();

		this.reasoner = getOWLReasoner(owlOntology);
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
			this.sameAsMap.put(indiv, equivalentIndividuals);
		}

	}

	/**
	 * compute NEWLY derived concept assertions
	 */
	private void computeEntailedConceptAssertions() {
		Set<OWLNamedIndividual> allIndividualsFromType = new HashSet<>();
		allIndividualsFromType.addAll(this.abstractDataFactory.getXAbstractIndividuals());
		allIndividualsFromType.addAll(this.abstractDataFactory.getYAbstractIndividuals());
		allIndividualsFromType.addAll(this.abstractDataFactory.getZAbstractIndividuals());

		for (OWLNamedIndividual indiv : allIndividualsFromType) {
			/*
			 * get entailed concepts from the reasoner
			 */
			Set<OWLClass> entailedConcepts = reasoner.getTypes(indiv, false).getFlattened();
			/*
			 * remove top
			 */
			entailedConcepts.remove(OWLManager.getOWLDataFactory().getOWLThing());
			/*
			 * collect asserted concepts in the ontology
			 */
			Set<OWLClassAssertionAxiom> assertedAssertions = this.owlOntology.getClassAssertionAxioms(indiv);
			Set<OWLClass> assertedConcepts = new HashSet<>();
			for (OWLClassAssertionAxiom eachAssertion : assertedAssertions) {
				assertedConcepts.add(eachAssertion.getClassExpression().asOWLClass());
			}
			/*
			 * remove asserted concepts
			 */
			entailedConcepts.removeAll(assertedConcepts);
			/*
			 * put the result to the map
			 *
			 */
			this.conceptAssertionsMap.put(indiv, entailedConcepts);
		}
	}

	@Override
	public long getReasoningTime() {
		return this.reasoningTime;
	}

	protected abstract void dispose();
}
