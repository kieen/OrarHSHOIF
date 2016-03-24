package orar.io.ontologyreader;

import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import orar.io.aboxstreamreader.ABoxStreamReader;
import orar.io.aboxstreamreader.JenaABoxStreamReader;
import orar.modeling.ontology.MapbasedOrarOntology;
import orar.modeling.ontology.OrarOntology;

public class StreamOntologyReader2InternalModel {

	private final OrarOntology internalOntology;

	private OWLOntology owlOntologyTBox;
	private String aboxListFileName;
	ABoxStreamReader aboxReader;
	private boolean done = false;

	/**
	 * @param owlOntologyTBox
	 *            an OWLAPI internalOntology containing TBox, RBox axioms, and
	 *            possibly some assertions.
	 * @param aboxListFile
	 *            a file contain a list of ABox files
	 */
	public StreamOntologyReader2InternalModel(OWLOntology owlOntologyTBox, String aboxListFile) {
		this.internalOntology = new MapbasedOrarOntology();
		this.owlOntologyTBox = owlOntologyTBox;
		this.aboxListFileName = aboxListFile;

		Set<OWLObjectProperty> definedObjectProperties = this.owlOntologyTBox.getObjectPropertiesInSignature(true);
		Set<OWLClass> definedClasses = this.owlOntologyTBox.getClassesInSignature(true);

		this.aboxReader = new JenaABoxStreamReader(definedObjectProperties, definedClasses, aboxListFileName,
				internalOntology);

	}

	private void readFromTBox() {

		this.internalOntology.addTBoxAxioms(owlOntologyTBox.getTBoxAxioms(true));

		this.internalOntology.addTBoxAxioms(owlOntologyTBox.getRBoxAxioms(true));

		// this.internalOntology.addAllTBoxAxioms(owlOntology.getAxioms(
		// AxiomType.FUNCTIONAL_OBJECT_PROPERTY, true));
		//
		// this.internalOntology.addAllTBoxAxioms(owlOntology.getAxioms(
		// AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY, true));
		//
		// this.internalOntology.addAllTBoxAxioms(owlOntology.getAxioms(
		// AxiomType.INVERSE_OBJECT_PROPERTIES, true));

	}

	private void readFromABoxes() {

		aboxReader.readABoxes();

		/*
		 * Add assertions (if any) from owlOntology to internalOntology
		 */
		addClassAssertions();
		addObjectPropertyAssertions();

	}

	private void addClassAssertions() {

		for (OWLClassAssertionAxiom classAssertion : owlOntologyTBox.getAxioms(AxiomType.CLASS_ASSERTION, true)) {
			OWLNamedIndividual individual = classAssertion.getIndividual().asOWLNamedIndividual();
			OWLClass owlClass = classAssertion.getClassExpression().asOWLClass();
			this.internalOntology.addConceptAssertion(individual, owlClass);
		}
	}

	private void addObjectPropertyAssertions() {
		for (OWLObjectPropertyAssertionAxiom assertion : owlOntologyTBox.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION,
				true)) {

			OWLNamedIndividual subject = assertion.getSubject().asOWLNamedIndividual();
			OWLObjectProperty property = assertion.getProperty().asOWLObjectProperty();
			OWLNamedIndividual object = assertion.getObject().asOWLNamedIndividual();

			this.internalOntology.addRoleAssertion(subject, property, object);

		}
	}

	private void getSignature() {

		this.internalOntology.addIndividualsToSignature(owlOntologyTBox.getIndividualsInSignature(true));

		this.internalOntology.addRoleNamesToSignature(owlOntologyTBox.getObjectPropertiesInSignature(true));

		this.internalOntology.addConceptNamesToSignature(owlOntologyTBox.getClassesInSignature(true));
	}

	private void readOntology() {

		readFromTBox();
		readFromABoxes();
		/*
		 * Note: getSignature needs to be called after readFromABoxes();
		 */
		getSignature();
		this.done = true;
	}

	public OrarOntology getOntology() {
		if (!done) {
			readOntology();
		}
		return this.internalOntology;
	}

}
