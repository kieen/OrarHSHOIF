package orar.io.ontologyreader;

import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;

import orar.dlfragmentvalidator.ValidatorDataFactory;
import orar.modeling.ontology.MapbasedOrarOntology;
import orar.modeling.ontology.OrarOntology;

/**
 * Convert an OWLAPI ontology into the internal ontology data structure.
 * 
 * @author kien
 *
 */
public class OntologyConverter {
	private static final Logger logger = Logger.getLogger(OntologyConverter.class);
	private OWLOntology owlOntology;

	private OrarOntology internalOntology;

	boolean done = false;

	public OntologyConverter(OWLOntology owlOntology) {
		this.owlOntology = owlOntology;
		this.internalOntology = new MapbasedOrarOntology();

	}

	private void convert() {
		obtainSignature();
		obtainAxioms();
		obtainClassAssertions();
		obtainObjectPropertyAssertions();
		obtainSameasAssertions();
		done = true;
	}

	private void obtainSameasAssertions() {
		for (OWLSameIndividualAxiom sameasAssertions : owlOntology.getAxioms(AxiomType.SAME_INDIVIDUAL, true)) {
			Set<OWLNamedIndividual> individuals = sameasAssertions.getIndividualsInSignature();
			this.internalOntology.addSameasAssertion(individuals);
		}

	}

	private void obtainSignature() {
		this.internalOntology.addIndividualsToSignature(owlOntology.getIndividualsInSignature(true));
//		logger.info("***DEBUG*** all individuals:"+owlOntology.getIndividualsInSignature(true));
		this.internalOntology.addConceptNamesToSignature(owlOntology.getClassesInSignature(true));
		this.internalOntology.addRoleNamesToSignature(owlOntology.getObjectPropertiesInSignature(true));
	}

	private void obtainAxioms() {
		this.internalOntology.addTBoxAxioms(owlOntology.getTBoxAxioms(true));

		this.internalOntology.addTBoxAxioms(owlOntology.getRBoxAxioms(true));
		//
		// this.internalOntology.addAllTBoxAxioms(owlOntology.getAxioms(
		// AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY, true));
		// this.internalOntology.getRBoxAxioms().addAll(
		// owlOntology.getAxioms(AxiomType.FUNCTIONAL_OBJECT_PROPERTY,
		// true));
		// this.internalOntology.getRBoxAxioms().addAll(
		// owlOntology
		// .getAxioms(AxiomType.INVERSE_OBJECT_PROPERTIES, true));
	}

	private void obtainClassAssertions() {

		for (OWLClassAssertionAxiom classAssertion : owlOntology.getAxioms(AxiomType.CLASS_ASSERTION, true)) {
			OWLNamedIndividual individual = ValidatorDataFactory.getInstance()
					.getNamedIndividual(classAssertion.getIndividual());

			// OWLNamedIndividual individual =
			// classAssertion.getIndividual().asOWLNamedIndividual();

			OWLClass owlClass = classAssertion.getClassExpression().asOWLClass();
			this.internalOntology.addConceptAssertion(individual, owlClass);
		}
	}

	private void obtainObjectPropertyAssertions() {
		for (OWLObjectPropertyAssertionAxiom assertion : owlOntology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION,
				true)) {

			OWLIndividual subject = assertion.getSubject();
			OWLObjectPropertyExpression property = assertion.getProperty();
			OWLIndividual object = assertion.getObject();
			if (property instanceof OWLObjectProperty) {
				if (subject instanceof OWLNamedIndividual && object instanceof OWLNamedIndividual) {
					this.internalOntology.addRoleAssertion(subject.asOWLNamedIndividual(),
							property.asOWLObjectProperty(), object.asOWLNamedIndividual());
				}
			}

			if (property instanceof OWLObjectInverseOf) {
				if (subject instanceof OWLNamedIndividual && object instanceof OWLNamedIndividual) {
					this.internalOntology.addRoleAssertion(object.asOWLNamedIndividual(), property.getNamedProperty(),
							subject.asOWLNamedIndividual());
				}
			}

		}
	}

	public OrarOntology getInternalOntology() {
		if (!done) {
			convert();
		}
		return this.internalOntology;
	}
}
