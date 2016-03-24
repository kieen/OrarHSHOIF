package orar.io.ontologyreader;

import orar.dlfragmentvalidator.ValidatorDataFactory;
import orar.factory.NormalizerDataFactory;
import orar.modeling.ontology.MapbasedOrarOntology;
import orar.modeling.ontology.OrarOntology;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Convert an OWLAPI ontology into the internal ontology data structure.
 * 
 * @author kien
 *
 */
public class OntologyConverter {

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
		done = true;
	}

	private void obtainSignature() {
		this.internalOntology.addIndividualsToSignature(owlOntology.getIndividualsInSignature(true));
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

			OWLNamedIndividual subject = assertion.getSubject().asOWLNamedIndividual();
			OWLObjectProperty property = assertion.getProperty().asOWLObjectProperty();
			OWLNamedIndividual object = assertion.getObject().asOWLNamedIndividual();

			this.internalOntology.addRoleAssertion(subject, property, object);

		}
	}

	public OrarOntology getInternalOntology() {
		if (!done) {
			convert();
		}
		return this.internalOntology;
	}
}