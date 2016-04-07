package orar.innerreasoner;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import orar.config.Configuration;
import orar.config.DebugLevel;
import orar.data.MetaDataOfOntology;

/**
 * Add axioms to mark singleton concepts and individuals having counting role
 * successors.
 * 
 * @author kien
 *
 */
public class MarkingAxiomAdder {
	private MetaDataOfOntology metaDataOfOntology;
	private final Set<OWLClass> singletonConcepts;
	private final Set<OWLClass> loopConcepts;
	private final Set<OWLClass> hasTranConcepts;

	private OWLOntology owlOntology;
	private final OWLDataFactory owlDataFactory;
	private final String singletonConceptIRI = "http://www.orar.com#SingletonConcept";
	private final String loopConceptIRI = "http://www.orar.com#LoopConcept";
	private final String hasTranConceptIRI = "http://www.orar.com#HasTranConcept";

	private final Configuration config;
	private int singletonConceptCounter = 0;
	private int loopConceptCounter = 0;
	private int hasTranConceptCounter = 0;

	private OWLOntologyManager ontoManager;
	private Logger logger = Logger.getLogger(MarkingAxiomAdder.class);

	public MarkingAxiomAdder(OWLOntology owlOntology) {
		this.metaDataOfOntology = MetaDataOfOntology.getInstance();
		this.owlOntology = owlOntology;
		this.owlDataFactory = OWLManager.getOWLDataFactory();
		this.config = Configuration.getInstance();
		this.ontoManager = OWLManager.createOWLOntologyManager();

		this.singletonConcepts = new HashSet<>();
		this.loopConcepts = new HashSet<>();
		this.hasTranConcepts = new HashSet<>();
	}

	public void addMarkingAxioms() {
		addAxiomsForSingletonConcept();
		addAxiomsForSelfLoopConcepts();
		addAxiomsForHasTranConcept();
		if (config.getDebuglevels()
				.contains(DebugLevel.MARKING_INDIVIDUAL_OF_SINGLETONCONCEPT_AND_HAVINGCOUNTINGCONCEPT)) {
			logger.info("Resulting TBox:");
			for (OWLAxiom axiom : this.owlOntology.getTBoxAxioms(true)) {
				logger.info(axiom);
			}
		}
	}

	private OWLClass getFreshSingletonConcept() {
		this.singletonConceptCounter++;
		String iriString = this.singletonConceptIRI + this.singletonConceptCounter;
		OWLClass newConcept = owlDataFactory.getOWLClass(IRI.create(iriString));
		this.singletonConcepts.add(newConcept);
		return newConcept;
	}

	private OWLClass getFreshLoopConcept() {
		this.loopConceptCounter++;
		String iriString = this.loopConceptIRI + this.loopConceptCounter;
		OWLClass newConcept = owlDataFactory.getOWLClass(IRI.create(iriString));
		this.loopConcepts.add(newConcept);
		return newConcept;
	}

	private OWLClass getFreshHasTranConcept() {
		this.hasTranConceptCounter++;
		String iriString = this.hasTranConceptIRI + this.hasTranConceptCounter;
		OWLClass newConcept = owlDataFactory.getOWLClass(IRI.create(iriString));
		this.hasTranConcepts.add(newConcept);
		return newConcept;
	}

	/**
	 * add axioms to mark individuals of singleton concept. Basically singleton
	 * concepts is reachable to a nominal via a chain of inverse functional
	 * roles.
	 */
	private void addAxiomsForSingletonConcept() {
		Set<OWLAxiom> addedAxioms = new HashSet<>();
		for (OWLClass nomC : metaDataOfOntology.getNominalConcepts()) {
			OWLClass singConcept = getFreshSingletonConcept();
			/*
			 * add nomC :subClassOf: singConcept
			 */
			addedAxioms.add(owlDataFactory.getOWLSubClassOfAxiom(nomC, singConcept));
			/*
			 * add singConcept :subClassOf : forall F. singConcept; where F is a
			 * funcational
			 */
			for (OWLObjectProperty functRole_F : this.metaDataOfOntology.getFunctionalRoles()) {
				OWLObjectAllValuesFrom forAll_F_singConcept = owlDataFactory.getOWLObjectAllValuesFrom(functRole_F,
						singConcept);
				OWLSubClassOfAxiom subClassAxiom = owlDataFactory.getOWLSubClassOfAxiom(singConcept,
						forAll_F_singConcept);
				addedAxioms.add(subClassAxiom);
			}

			/*
			 * add exist H. singConcept :SubclassOf: singConcept; where H is
			 * inverse functional
			 */
			for (OWLObjectProperty invfunctRole_H : this.metaDataOfOntology.getInverseFunctionalRoles()) {
				OWLObjectSomeValuesFrom exist_H_singConcept = owlDataFactory.getOWLObjectSomeValuesFrom(invfunctRole_H,
						singConcept);
				OWLSubClassOfAxiom subClassAxiom = owlDataFactory.getOWLSubClassOfAxiom(exist_H_singConcept,
						singConcept);
				addedAxioms.add(subClassAxiom);
			}

		}
		/*
		 * add axioms to the ontology
		 */
		this.ontoManager.addAxioms(owlOntology, addedAxioms);
	}

	/**
	 * Add axioms to mark concept \exists(T).T \sqcap \exists T^-.Top, e.g. in
	 * rule R_t^2. Note that we cannot formalize role conjunctions. <br>
	 * This marks individuals in rule R_t^2, e.g. N(a)--> T(a,a).<br>
	 * Note that this should be called after we add axioms for
	 * singleton-concepts.
	 */
	private void addAxiomsForSelfLoopConcepts() {
		for (OWLObjectProperty transRole : this.metaDataOfOntology.getTransitiveRoles()) {
			OWLObjectInverseOf inverOfTransRole = this.owlDataFactory.getOWLObjectInverseOf(transRole);
			OWLClass topConcept = this.owlDataFactory.getOWLThing();
			OWLObjectSomeValuesFrom conjunct1 = this.owlDataFactory.getOWLObjectSomeValuesFrom(transRole, topConcept);
			OWLObjectSomeValuesFrom conjunct2 = this.owlDataFactory.getOWLObjectSomeValuesFrom(inverOfTransRole,
					topConcept);

			OWLClass freshConcept = getFreshLoopConcept();
			OWLObjectIntersectionOf conjunction = this.owlDataFactory.getOWLObjectIntersectionOf(conjunct1, conjunct2);
			OWLSubClassOfAxiom newAxiom = this.owlDataFactory.getOWLSubClassOfAxiom(conjunction, freshConcept);
			this.ontoManager.addAxiom(owlOntology, newAxiom);
		}
	}

	/**
	 * add axiom of the form: \exists T.N, where T is transitive-role and N is a
	 * singleton concept. <br>
	 * This marks individuals in rule R_t^3: N(a), N(b) --> T(a,b).
	 */
	private void addAxiomsForHasTranConcept() {
		for (OWLClass singletonConcept : this.singletonConcepts) {
			OWLClass newConcept = getFreshHasTranConcept();
			for (OWLObjectProperty transRole : this.metaDataOfOntology.getTransitiveRoles()) {
				OWLClassExpression existsTransRole_SingletonConcept = this.owlDataFactory
						.getOWLObjectSomeValuesFrom(transRole, singletonConcept);
				OWLAxiom newAxiom = this.owlDataFactory.getOWLSubClassOfAxiom(existsTransRole_SingletonConcept,
						newConcept);
				this.ontoManager.addAxiom(owlOntology, newAxiom);
			}
		}
	}

	public Set<OWLClass> getSingletonConcepts() {
		return singletonConcepts;
	}

	public OWLOntology getOwlOntology() {
		return owlOntology;
	}

	public Set<OWLClass> getLoopConcepts() {
		return loopConcepts;
	}

	public Set<OWLClass> getHasTranConcepts() {
		return hasTranConcepts;
	}

	public String getSingletonConceptIRI() {
		return singletonConceptIRI;
	}

	public String getLoopConceptIRI() {
		return loopConceptIRI;
	}

	public String getHasTranConceptIRI() {
		return hasTranConceptIRI;
	}

}