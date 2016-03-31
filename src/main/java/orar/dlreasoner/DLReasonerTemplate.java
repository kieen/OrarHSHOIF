package orar.dlreasoner;

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
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public abstract class DLReasonerTemplate implements DLReasoner {
	private Logger logger = Logger.getLogger(DLReasonerTemplate.class);
	protected final Set<OWLClassAssertionAxiom> conceptAssertions;
	protected final Set<OWLObjectPropertyAssertionAxiom> roleAssertions;
	protected final Map<OWLNamedIndividual, Set<OWLNamedIndividual>> sameasAssertionAsMap;
	protected OWLOntology owlOntology;
	protected long reasoningTime;
	protected OWLReasoner reasoner;
	protected OWLDataFactory dataFactory;
	boolean entailmentComputed;

	public DLReasonerTemplate(OWLOntology owlOntology) {
		this.conceptAssertions = new HashSet<>();
		this.roleAssertions = new HashSet<>();
		this.sameasAssertionAsMap = new HashMap<>();
		this.owlOntology = owlOntology;
		this.dataFactory = OWLManager.getOWLDataFactory();
		this.entailmentComputed = false;
	}

	@Override
	public Set<OWLClassAssertionAxiom> getEntailedConceptAssertions() {
		if (!this.entailmentComputed) {
			computeEntailments();
		}
		return this.conceptAssertions;
	}

	@Override
	public Set<OWLObjectPropertyAssertionAxiom> getEntailedRoleAssertions() {
		if (!this.entailmentComputed) {
			computeEntailments();
		}
		return this.roleAssertions;
	}

	@Override
	public Map<OWLNamedIndividual, Set<OWLNamedIndividual>> getEntailedSameasAssertions() {
		if (!this.entailmentComputed) {
			computeEntailments();
		}
		return this.sameasAssertionAsMap;
	}

	@Override
	public long getReasoningTime() {

		return this.reasoningTime;
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
		this.reasoningTime = (endTime - startTime) / 1000; // get seconds
	}

	/**
	 * compute entailed same individuals
	 */
	private void computeEntailedSameasAssertions() {
		Set<OWLNamedIndividual> allIndividuals = this.owlOntology.getIndividualsInSignature(true);
		for (OWLNamedIndividual indiv : allIndividuals) {
			Set<OWLNamedIndividual> equivalentIndividuals = reasoner.getSameIndividuals(indiv).getEntities();

			this.sameasAssertionAsMap.put(indiv, equivalentIndividuals);
		}

	}

	/**
	 * compute entailed role assertions
	 */
	private void computeEntailedRoleAssertions() {
		Set<OWLNamedIndividual> allIndividuals = this.owlOntology.getIndividualsInSignature(true);
		Set<OWLObjectProperty> allRoles = this.owlOntology.getObjectPropertiesInSignature(true);
		for (OWLNamedIndividual individual : allIndividuals) {
			for (OWLObjectProperty role : allRoles) {
				Set<OWLNamedIndividual> objects = reasoner.getObjectPropertyValues(individual, role).getFlattened();
				for (OWLNamedIndividual object : objects) {
					OWLObjectPropertyAssertionAxiom newRoleAssertion = this.dataFactory
							.getOWLObjectPropertyAssertionAxiom(role, individual, object);
					this.roleAssertions.add(newRoleAssertion);
				}
			}
		}

	}

	/**
	 * compute entailed concept assertions
	 */
	private void computeEntailedConceptAssertions() {
		Set<OWLNamedIndividual> allIndividuals = this.owlOntology.getIndividualsInSignature(true);
		for (OWLNamedIndividual indiv : allIndividuals) {
			Set<OWLClass> entailedConcepts = reasoner.getTypes(indiv, false).getFlattened();
			entailedConcepts.remove(OWLManager.getOWLDataFactory().getOWLThing());
			for (OWLClass entailedConcept : entailedConcepts) {
				OWLClassAssertionAxiom newConceptAssertion = this.dataFactory.getOWLClassAssertionAxiom(entailedConcept,
						indiv);
				this.conceptAssertions.add(newConceptAssertion);
			}

		}
	}

}
