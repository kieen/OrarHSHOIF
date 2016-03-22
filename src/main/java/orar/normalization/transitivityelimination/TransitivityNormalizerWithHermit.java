package orar.normalization.transitivityelimination;

import java.util.HashSet;
import java.util.Set;

import orar.config.Configuration;
import orar.config.DebugLevel;
import orar.factory.NormalizerDataFactory;

import org.apache.log4j.Logger;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;

/**
 * @author kien
 *         Using Hermit reasoner to compute role hierarchy, which is the input
 *         for transitivity normalization.
 *         //TODO: check to keep trans axioms in the input ontology.
 */
public class TransitivityNormalizerWithHermit implements TransitivityNormalizer {

	private Reasoner hermit;
	private OWLOntology inputOntology;
	private OWLOntology resultingOntology;

	private OWLDataFactory owlDataFactory;
	private NormalizerDataFactory normalizerDataFactory;
	private boolean eliminationIsDone;
	private Configuration config;
	private Logger logger = Logger
			.getLogger(TransitivityNormalizerWithHermit.class);

	public TransitivityNormalizerWithHermit(OWLOntology inputOntology) {
		this.inputOntology = inputOntology;
		this.hermit = new Reasoner(inputOntology);

		this.owlDataFactory = OWLManager.getOWLDataFactory();
		this.normalizerDataFactory = NormalizerDataFactory.getInstance();

		this.eliminationIsDone = false;
		this.config = Configuration.getInstance();
	}

	@Override
	public void normalizeTransitivity() {
		OWLOntologyManager ontologyManager = OWLManager
				.createOWLOntologyManager();
		try {
			resultingOntology = ontologyManager.createOntology();
			/*
			 * 1) genearte axioms simulating transivitity and add to the
			 * resulting ontology
			 */

			Set<OWLSubClassOfAxiom> axiomsSimulatingTransitivity = generateAxiomsSimulatingTransitivity();
			ontologyManager.addAxioms(resultingOntology,
					axiomsSimulatingTransitivity);

			/*
			 * get all Axioms from the inputOntology, except transitivity axioms
			 */
			Set<OWLAxiom> axiomsFromInputOntology = new HashSet<>();
			axiomsFromInputOntology.addAll(this.inputOntology
					.getTBoxAxioms(true));
			axiomsFromInputOntology.addAll(this.inputOntology
					.getRBoxAxioms(true));
			axiomsFromInputOntology.addAll(this.inputOntology
					.getABoxAxioms(true));

			/*
			 * remove transitivity axioms
			 */
			Set<OWLTransitiveObjectPropertyAxiom> transAxioms = this.inputOntology
					.getAxioms(AxiomType.TRANSITIVE_OBJECT_PROPERTY, true);
			axiomsFromInputOntology.removeAll(transAxioms);

			/*
			 * add axioms from the input ontology to the resulting ontology
			 */
			ontologyManager.addAxioms(resultingOntology,
					axiomsFromInputOntology);

		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}

		this.eliminationIsDone = true;
	}

	private Set<OWLSubClassOfAxiom> generateAxiomsSimulatingTransitivity() {

		Set<OWLSubClassOfAxiom> generatedAxioms = new HashSet<>();

		hermit.precomputeInferences(InferenceType.OBJECT_PROPERTY_HIERARCHY);
		Set<OWLObjectPropertyExpression> allTransRoles = getTransitiveRoles();
		Set<OWLSubClassOfAxiom> todoSubClassAxioms = getSubClassOfAxioms();

		for (OWLObjectPropertyExpression eachTransRole : allTransRoles) {

			Set<OWLObjectPropertyExpression> superRolesOfEachTransRole = hermit
					.getSuperObjectProperties(eachTransRole, false)
					.getFlattened();
			/*
			 * Note to add also eachTransRole since OWLAPI return strict super
			 * roles.
			 */
			superRolesOfEachTransRole.add(eachTransRole);

			for (OWLSubClassOfAxiom eachSubClassAxiom : todoSubClassAxioms) {
				if (eachSubClassAxiom.getSuperClass() instanceof OWLObjectAllValuesFrom) {

					OWLObjectAllValuesFrom superClass = (OWLObjectAllValuesFrom) eachSubClassAxiom
							.getSuperClass();
					OWLObjectPropertyExpression roleOfTheAxiom = superClass
							.getProperty();
					if (superRolesOfEachTransRole.contains(roleOfTheAxiom)) {
						OWLClassExpression fillerOfTheAxiom = superClass
								.getFiller();
						OWLClassExpression subClass = eachSubClassAxiom
								.getSubClass();
						Set<OWLSubClassOfAxiom> newAxioms = getNormalizedAxiomsForEachSubClassAxiom(
								subClass, eachTransRole, fillerOfTheAxiom);
						generatedAxioms.addAll(newAxioms);
						/*
						 * Debug:start
						 */
						if (config.getDebuglevels().contains(
								DebugLevel.TRANSITIVITY_ELIMINATION)) {
							logger.info("*** DEBUG: transivitity elimination");
							logger.info("Axiom to be considered:"
									+ eachSubClassAxiom);
							logger.info("Transitive role:" + eachTransRole);
							logger.info("Generated axioms:" + newAxioms);
							logger.info("*** ");
						}
						/*
						 * Debug:end
						 */
					}
				}
			}
		}
		return generatedAxioms;
	}

	private Set<OWLSubClassOfAxiom> getNormalizedAxiomsForEachSubClassAxiom(
			OWLClassExpression subClass,
			OWLObjectPropertyExpression eachTransRole,
			OWLClassExpression fillerOfTheAxiom) {

		Set<OWLSubClassOfAxiom> newAxioms = new HashSet<>();

		OWLClass newConcept = normalizerDataFactory
				.getFreshConceptForTransitivity();
		OWLObjectAllValuesFrom forAllRestriction = owlDataFactory
				.getOWLObjectAllValuesFrom(eachTransRole, newConcept);

		OWLSubClassOfAxiom axiom1 = owlDataFactory.getOWLSubClassOfAxiom(
				subClass, forAllRestriction);
		newAxioms.add(axiom1);

		OWLSubClassOfAxiom axiom2 = owlDataFactory.getOWLSubClassOfAxiom(
				newConcept, forAllRestriction);
		newAxioms.add(axiom2);

		OWLSubClassOfAxiom axiom3 = owlDataFactory.getOWLSubClassOfAxiom(
				newConcept, fillerOfTheAxiom);
		newAxioms.add(axiom3);

		return newAxioms;
	}

	private Set<OWLSubClassOfAxiom> getSubClassOfAxioms() {
		Set<OWLSubClassOfAxiom> subClassOfAxioms = new HashSet<>();
		SubClassOfAxiomCollector axiomCollector = new SubClassOfAxiomCollector();
		Set<OWLAxiom> tboxAxioms = this.inputOntology.getTBoxAxioms(true);
		for (OWLAxiom axiom : tboxAxioms) {
			OWLSubClassOfAxiom subClassOfAxiom = axiom.accept(axiomCollector);
			if (axiom.accept(axiomCollector) != null) {
				subClassOfAxioms.add(subClassOfAxiom);
			}

		}
		return subClassOfAxioms;
	}

	private Set<OWLObjectPropertyExpression> getTransitiveRoles() {
		Set<OWLObjectPropertyExpression> transRoles = new HashSet<>();
		Set<OWLTransitiveObjectPropertyAxiom> transAxioms = this.inputOntology
				.getAxioms(AxiomType.TRANSITIVE_OBJECT_PROPERTY, true);
		for (OWLTransitiveObjectPropertyAxiom axiom : transAxioms) {
			transRoles.add(axiom.getProperty());
		}
		return transRoles;
	}

	@Override
	public OWLOntology getResultingOntology() {
		if (!this.eliminationIsDone) {
			normalizeTransitivity();
		}
		return this.resultingOntology;
	}
}
