package orar.io.aboxstreamreader;

import java.util.Set;

import org.apache.jena.riot.system.StreamRDFBase;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import orar.modeling.ontology.OrarOntology;

/**
 * A RDF triple processing class based on Jena. It reads each triple from a file
 * and then add it to the OrarOntology; the triple is handled in
 * {@code #triple(Triple) method}.<br>
 * Like OWLAPI, we only accepts assertions whose concepts and roles are defined,
 * e.g. in the TBox. This requires a set of predefined concept and role names.
 * {@code #definedConceptNames, #definedRoleNames}
 * 
 * See more {@link https://jena.apache.org/documentation/io/rdf-input.html } and
 * {@link https://github.com/apache/jena/blob/master/jena-arq/src-examples/arq/examples/riot/ExRIOT_4.java}
 * for further information on using this class.
 * 
 * @author kien
 *
 */
public class JenaStreamRDF2InternalModel extends StreamRDFBase {
	private final String OWL_NAMESPACE = "http://www.w3.org/2002/07/owl#";
	private final String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private final String OWL_ONTOLOGY = OWL_NAMESPACE + "Ontology";
	private final String OWL_IMPORTS = OWL_NAMESPACE + "imports";

	private final String OWL_NAMEDINDIVIDUAL = OWL_NAMESPACE + "NamedIndividual";
	private final String OWL_CLASS = OWL_NAMESPACE + "Class";
	private final String OWL_OBJECT_PROPERTY = OWL_NAMESPACE + "ObjectProperty";
	private final String OWL_DATA_PROPERTY = OWL_NAMESPACE + "DatatypeProperty";

	private final String RDF_TYPE = RDF_NAMESPACE + "type";
	private int countDeclairedAssertions;
	private int countAllAssertions;
	private int countConceptAssertions;
	private int countRoleAssertions;

	private static final Logger logger = Logger.getLogger(JenaStreamRDF2InternalModel.class);
	private final Set<OWLObjectProperty> definedRoleNames;
	private final Set<OWLClass> definedConceptNames;
	// private Map<OWLNamedIndividual, Set<OWLClass>> classAssertionMap;
	// private Map<OWLNamedIndividual, Map<OWLObjectProperty,
	// Set<OWLNamedIndividual>>> objectPropertyAssertionMap;

	private OWLDataFactory owlDataFactory;
	private final OrarOntology orarOntology;

	public JenaStreamRDF2InternalModel(Set<OWLObjectProperty> definedRoleNames, Set<OWLClass> definedConceptNames,
			OrarOntology resultingOntology) {

		this.definedRoleNames = definedRoleNames;
		this.definedConceptNames = definedConceptNames;
		this.orarOntology = resultingOntology;
		this.owlDataFactory = OWLManager.getOWLDataFactory();
		this.countDeclairedAssertions = 0;
		this.countAllAssertions = 0;
		this.countConceptAssertions = 0;
		this.countRoleAssertions = 0;

	}

	@Override
	public void triple(Triple triple) {
		Node subject = triple.getSubject();
		Node predicate = triple.getPredicate();
		Node object = triple.getObject();
		this.countAllAssertions++;
		if (isClassAssertion(subject, predicate, object)) {
			addClassAssertion(subject, object);
			countDeclairedAssertions++;
			countConceptAssertions++;
			// printTriple(subject, predicate, object);
		}

		if (isObjectPropertyAssertion(subject, predicate, object)) {
			addObjectPropertyAssertion(subject, predicate, object);
			countDeclairedAssertions++;
			countRoleAssertions++;
			// printTriple(subject, predicate, object);
		}
		// TODO: take care of import
	}

	private void addClassAssertion(Node subject, Node object) {
		/*
		 * add class assertion.
		 */
		OWLClass owlClass = owlDataFactory.getOWLClass(IRI.create(object.toString()));
		OWLNamedIndividual owlNamedIndividual = owlDataFactory.getOWLNamedIndividual(IRI.create(subject.toString()));

		this.orarOntology.addConceptAssertion(owlNamedIndividual, owlClass);

		/*
		 * update in Signature
		 */
		this.orarOntology.addIndividualToSignature(owlNamedIndividual);
		this.orarOntology.addConceptNameToSignature(owlClass);

	}

	private void addObjectPropertyAssertion(Node subject, Node predicate, Node object) {

		OWLNamedIndividual subjectInd = owlDataFactory.getOWLNamedIndividual(IRI.create(subject.toString()));
		OWLNamedIndividual objectInd = owlDataFactory.getOWLNamedIndividual(IRI.create(object.toString()));
		OWLObjectProperty property = owlDataFactory.getOWLObjectProperty(IRI.create(predicate.toString()));
		// logger.info("property:"+property);
		this.orarOntology.addRoleAssertion(subjectInd, property, objectInd);
		/*
		 * Update in Signature
		 */
		this.orarOntology.addIndividualToSignature(subjectInd);
		this.orarOntology.addIndividualToSignature(objectInd);
		// this.owlObjectPropertyInABoxSignature.add(property);

	}

	// private void printTriple(Node subject, Node predicate, Node object) {
	//
	// logger.info("Triples #" + countDeclairedAssertions);
	//
	// logger.info(subject);
	// logger.info(predicate);
	// logger.info(object);
	//
	// }

	private boolean isClassAssertion(Node subject, Node predicate, Node object) {

		if (isDeclarationTriple(subject, predicate, object))
			return false;
		if (isObjectPropertyAssertion(subject, predicate, object))
			return false;

		boolean isClassAssertion = predicate.toString().equals(RDF_TYPE);
		OWLClass owlClass = owlDataFactory.getOWLClass(IRI.create(object.toString()));
		boolean isPredefinedClass = this.definedConceptNames.contains(owlClass);

		return (isClassAssertion && isPredefinedClass);
	}

	private boolean isObjectPropertyAssertion(Node subject, Node predicate, Node object) {

		OWLObjectProperty property = owlDataFactory.getOWLObjectProperty(IRI.create(predicate.toString()));
		boolean isData = subject.isLiteral() || object.isLiteral();
		/*
		 * We need to check subject and object because some ontology is not so
		 * "cleaned". Same name could be used for both object and data
		 * proroperty
		 */
		// return (!isData);
		return (!isData && this.definedRoleNames.contains(property));
		// Node subject = triple.getSubject();
		// Node object = triple.getObject();
		//
		// return (subject.isLiteral() || object.isLiteral() );
	}

	/**
	 * @param triple
	 * @return true if this triple is a declaration one, e.g. to declare
	 *         ontology, property, class, individual.
	 */
	private boolean isDeclarationTriple(Node subject, Node predicate, Node object) {

		if (predicate.toString().equals(RDF_TYPE)) {
			if (object.toString().equals(OWL_CLASS) || object.toString().equals(OWL_NAMEDINDIVIDUAL)
					|| object.toString().equals(OWL_OBJECT_PROPERTY) || object.toString().equals(OWL_DATA_PROPERTY)
					|| object.toString().equals(OWL_ONTOLOGY)) {
				return true;
			}
		}
		return false;
	}

	 /**
	 * @param triple
	 * @return true if this triple is a triple to import other ontologies.
	 */
	 private boolean isImportTriple(Node subject, Node predicate, Node object)
	 {
	 if (predicate.toString().equals(OWL_IMPORTS)) {
	 return true;
	 }
	 return false;
	 }

	@Override
	public void finish() {
		logger.info("Number of parsed triples up to now: " + countAllAssertions);

		logger.info("Number of declaired triples up to now: " + countDeclairedAssertions);
		logger.info("Number of (declaried) concept assertions: " + this.countConceptAssertions);
		logger.info("Number of (declaried) role assertions: " + this.countRoleAssertions);

		orarOntology.setNumberOfInputConceptAssertions(countConceptAssertions);

		orarOntology.setNumberOfInputRoleAssertions(countRoleAssertions);
	}

}
