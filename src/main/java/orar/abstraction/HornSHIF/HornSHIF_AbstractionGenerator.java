package orar.abstraction.HornSHIF;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import orar.abstraction.AbstractionGenerator;
import orar.abstraction.PairOfSubjectAndObject;
import orar.data.SharedData;
import orar.data.SharedMap;
import orar.factory.AbstractDataFactory;
import orar.modeling.ontology.OrarOntology;
import orar.type.IndividualType;

public class HornSHIF_AbstractionGenerator implements AbstractionGenerator {

	private final SharedMap sharedMap;
	private final SharedData sharedData;

	private final OWLOntologyManager manager;
	private final OWLDataFactory owlDataFactory;
	private final AbstractDataFactory abstractDataFactory;

	/*
	 * 
	 */
	private final OrarOntology orarOntology;
	private final Map<IndividualType, Set<OWLNamedIndividual>> typeMap2Individuals;

	public HornSHIF_AbstractionGenerator(OrarOntology orarOntology,
			Map<IndividualType, Set<OWLNamedIndividual>> typeMap2Individuals) {
		this.sharedMap = SharedMap.getInstance();
		this.sharedData = SharedData.getInstance();
		this.manager = OWLManager.createOWLOntologyManager();
		this.owlDataFactory = OWLManager.getOWLDataFactory();
		this.abstractDataFactory = AbstractDataFactory.getInstance();

		this.orarOntology = orarOntology;
		this.typeMap2Individuals = typeMap2Individuals;

	}

	@Override
	public OWLOntology getAbstractOntology() {

		int totalNumberOfTypes = typeMap2Individuals.keySet().size();
		List<OWLOntology> abstractOntologiesList = getAbstractOntologies(totalNumberOfTypes);
		return abstractOntologiesList.get(0);
	}

	@Override
	public List<OWLOntology> getAbstractOntologies(int numberOfTypePerOntology) {
		Set<IndividualType> types = typeMap2Individuals.keySet();
		List<OWLOntology> resultingAbstractOntologies = new ArrayList<>();
		int totalNumberOfTypes = types.size();
		int numberOfOntologies = totalNumberOfTypes / numberOfTypePerOntology
				+ (totalNumberOfTypes % numberOfTypePerOntology == 0 ? 0 : 1);

		Iterator<IndividualType> iterator = types.iterator();

		for (int i = 1; i <= numberOfOntologies; i++) {
			Set<IndividualType> chunk = new HashSet<>();
			/*
			 * Get a chunk of individual types.
			 */
			for (int j = 1; j <= numberOfTypePerOntology; j++) {

				if (iterator.hasNext()) {
					chunk.add(iterator.next());
				}
			}
			OWLOntology abstractOntforAChunk = getOneAbstractOntology(chunk);
			resultingAbstractOntologies.add(abstractOntforAChunk);
		}

		return resultingAbstractOntologies;
	}

	/**
	 * @param types
	 * @return An abstract ontology
	 */
	private OWLOntology getOneAbstractOntology(Set<IndividualType> types) {

		try {
			OWLOntology abstractOntology = manager.createOntology();
			Set<OWLAxiom> aBoxAssertions = new HashSet<>();
			for (IndividualType type : types) {
				/*
				 * get abstraction assertions
				 */
				aBoxAssertions.addAll(generateAssertions(type));

			}
			manager.addAxioms(abstractOntology, aBoxAssertions);
			manager.addAxioms(abstractOntology, orarOntology.getTBoxAxioms());
			return abstractOntology;
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * @param type
	 * @return a set of assertions for the given type.
	 */
	private Set<OWLAxiom> generateAssertions(IndividualType type) {
		Set<OWLAxiom> abstractAssertions = new HashSet<>();
		/*
		 * create x
		 */
		OWLNamedIndividual x = abstractDataFactory.createAbstractIndividualX();
		Set<OWLNamedIndividual> originalIndsForThisType = this.typeMap2Individuals.get(type);
		/*
		 * map X to original individuals.
		 */
		sharedMap.getXAbstract2OriginalIndividualsMap().put(x, originalIndsForThisType);

		/*
		 * create abstract class assertions for x
		 */
		abstractAssertions.addAll(getAbstractClassAssertions(x, type));

		/*
		 * role assertions for x
		 */
		abstractAssertions.addAll(getAbstractRoleAssertions(x, type));

		return abstractAssertions;
	}

	/**
	 * @param x
	 * @param type
	 * @return concept assertions x according to its type
	 */
	private Set<OWLAxiom> getAbstractClassAssertions(OWLNamedIndividual x, IndividualType type) {

		Set<OWLAxiom> classAssertions = new HashSet<OWLAxiom>();

		for (OWLClass conceptName : type.getConcepts()) {
			OWLClassAssertionAxiom assertion = owlDataFactory.getOWLClassAssertionAxiom(conceptName, x);
			classAssertions.add(assertion);
		}

		return classAssertions;
	}

	/**
	 * @param x
	 * @param type
	 * @return Pre and successor property assertions of x according to its type
	 */
	private Set<OWLAxiom> getAbstractRoleAssertions(OWLNamedIndividual x, IndividualType type) {

		Set<OWLAxiom> propertyAssertions = new HashSet<>();

		/*
		 * add predecessor roles assertions
		 */
		propertyAssertions.addAll(getPredecessorRoleAssertions(x, type));
		/*
		 * add successor role assertions
		 */
		propertyAssertions.addAll(getSuccessorRoleAssertions(x, type));

		return propertyAssertions;

	}

	private Set<OWLAxiom> getPredecessorRoleAssertions(OWLNamedIndividual x, IndividualType type) {
		Set<OWLAxiom> propertyAssertions = new HashSet<>();
		for (OWLObjectProperty preRole : type.getPredecessorRoles()) {
			// if (propertyIn == null) {
			// Printer.printSet(type.getPreRoles());
			// }
			/*
			 * generate z and get role assertion for it.
			 */
			OWLNamedIndividual z = abstractDataFactory.createAbstractIndividualZ();

			OWLObjectPropertyAssertionAxiom inPropertyAssertion = owlDataFactory
					.getOWLObjectPropertyAssertionAxiom(preRole, z, x);
			propertyAssertions.add(inPropertyAssertion);

			/*
			 * map z to original individuals
			 */
			Set<OWLNamedIndividual> originalIndsMappedToZ = new HashSet<>();

			Set<OWLNamedIndividual> originalIndsForX = sharedMap.getXAbstract2OriginalIndividualsMap().get(x);
			for (OWLNamedIndividual indForX : originalIndsForX) {
				originalIndsMappedToZ.addAll(orarOntology.getPredecessors(indForX, preRole));
			}
			sharedMap.getZAbstract2OriginalIndividualsMap().put(z, originalIndsMappedToZ);

			/*
			 * map: (z,x) --> preRole if preRole is inverse functional
			 */
			if (this.sharedData.getInverseFunctionalRoles().contains(preRole)) {
				PairOfSubjectAndObject zxPair = new PairOfSubjectAndObject(z, x);
				Set<OWLObjectProperty> existingRoles = this.sharedMap.getZXMap2Roles().get(zxPair);
				if (existingRoles == null) {
					existingRoles = new HashSet<>();
				}
				existingRoles.add(preRole);
				this.sharedMap.getZXMap2Roles().put(zxPair, existingRoles);
			}

		}
		return propertyAssertions;

	}

	private Set<OWLAxiom> getSuccessorRoleAssertions(OWLNamedIndividual x, IndividualType type) {
		Set<OWLAxiom> propertyAssertions = new HashSet<>();
		for (OWLObjectProperty succRole : type.getSuccessorRoles()) {
			/*
			 * get y and its assertion
			 */
			OWLNamedIndividual y = abstractDataFactory.createAbstractIndividualY();

			OWLObjectPropertyAssertionAxiom outPropertyAssertion = owlDataFactory
					.getOWLObjectPropertyAssertionAxiom(succRole, x, y);

			propertyAssertions.add(outPropertyAssertion);
			/*
			 * map y to original individuals
			 */
			Set<OWLNamedIndividual> originalIndsMappedToY = new HashSet<>();

			Set<OWLNamedIndividual> originalIndsForX = sharedMap.getXAbstract2OriginalIndividualsMap().get(x);
			for (OWLNamedIndividual indForX : originalIndsForX) {
				originalIndsMappedToY.addAll(orarOntology.getSuccessors(indForX, succRole));
			}
			sharedMap.getYAbstract2OriginalIndividualsMap().put(y, originalIndsMappedToY);

			/*
			 * map: (x,y) --> succRole if succRole is functional
			 */
			if (this.sharedData.getFunctionalRoles().contains(succRole)) {
				PairOfSubjectAndObject xyPair = new PairOfSubjectAndObject(x, y);
				Set<OWLObjectProperty> existingRoles = this.sharedMap.getXYMap2Roles().get(xyPair);
				if (existingRoles == null) {
					existingRoles = new HashSet<>();
				}
				existingRoles.add(succRole);
				this.sharedMap.getXYMap2Roles().put(xyPair, existingRoles);
			}

		}
		return propertyAssertions;
	}

}
