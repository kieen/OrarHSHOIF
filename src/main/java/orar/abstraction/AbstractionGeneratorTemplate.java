package orar.abstraction;

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

import orar.data.AbstractDataFactory;
import orar.data.MetaDataOfOntology;
import orar.data.DataForTransferingEntailments;
import orar.modeling.ontology.OrarOntology;
import orar.type.IndividualType;

/**
 * Template for generating abstraction from individuals types.
 * 
 * @author kien
 *
 */
public abstract class AbstractionGeneratorTemplate implements AbstractionGenerator {

	protected final DataForTransferingEntailments sharedMap;
	protected final MetaDataOfOntology sharedData;

	protected final OWLOntologyManager manager;
	protected final OWLDataFactory owlDataFactory;
	protected final AbstractDataFactory abstractDataFactory;

	/*
	 * 
	 */
	protected final OrarOntology orarOntology;
	protected final Map<IndividualType, Set<OWLNamedIndividual>> typeMap2Individuals;

	public AbstractionGeneratorTemplate(OrarOntology orarOntology,
			Map<IndividualType, Set<OWLNamedIndividual>> typeMap2Individuals) {
		this.sharedMap = DataForTransferingEntailments.getInstance();
		this.sharedData = MetaDataOfOntology.getInstance();
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

	private Set<OWLAxiom> generateAssertions(IndividualType type) {
		Set<OWLAxiom> abstractAssertions = new HashSet<>();
		/*
		 * create x
		 */
		OWLNamedIndividual x = abstractDataFactory.createAbstractIndividualX();
		/*
		 * map X to original individuals.
		 */
		Set<OWLNamedIndividual> originalIndsForThisType = this.typeMap2Individuals.get(type);
		sharedMap.getMap_XAbstractIndiv_2_OriginalIndivs().put(x, originalIndsForThisType);

		/*
		 * Mark x if x has functional succ-role
		 */
		markXHavingFunctionalRole(x, type);
		/*
		 * create abstract class assertions for x
		 */
		abstractAssertions.addAll(getConceptAssertions(x, type));

		/*
		 * create prerole assertions for x
		 */
		abstractAssertions.addAll(getPredecessorRoleAssertions(x, type));
		/*
		 * create succRole assertions for x
		 */
		abstractAssertions.addAll(getSuccessorRoleAssertions(x, type));

		/*
		 * create concept assertion for conept-type
		 */
		abstractAssertions.addAll(getConceptAssertionsForConceptType(type));

		return abstractAssertions;
	}

	/**
	 * @param x
	 * @param type
	 * @return concept assertions x according to its type
	 */
	protected Set<OWLAxiom> getConceptAssertions(OWLNamedIndividual x, IndividualType type) {

		Set<OWLAxiom> classAssertions = new HashSet<OWLAxiom>();

		for (OWLClass conceptName : type.getConcepts()) {
			OWLClassAssertionAxiom assertion = owlDataFactory.getOWLClassAssertionAxiom(conceptName, x);
			classAssertions.add(assertion);
		}

		return classAssertions;
	}

	protected Set<OWLAxiom> getPredecessorRoleAssertions(OWLNamedIndividual x, IndividualType type) {
		Set<OWLAxiom> preRoleAssertions = new HashSet<>();
		for (OWLObjectProperty preRole : type.getPredecessorRoles()) {
			// if (propertyIn == null) {
			// Printer.printSet(type.getPreRoles());
			// }
			/*
			 * generate z and get role assertion for it.
			 */
			OWLNamedIndividual z = abstractDataFactory.createAbstractIndividualZ();

			OWLObjectPropertyAssertionAxiom preRoleAssertion = owlDataFactory
					.getOWLObjectPropertyAssertionAxiom(preRole, z, x);
			preRoleAssertions.add(preRoleAssertion);

			/*
			 * Mark z if z has an inverse functional pre-role
			 */
			markZHavingInverseFunctionalRole(z, preRole);

			/*
			 * map z to original individuals
			 */
			Set<OWLNamedIndividual> originalIndsMappedToZ = new HashSet<>();

			Set<OWLNamedIndividual> originalIndsForX = sharedMap.getMap_XAbstractIndiv_2_OriginalIndivs().get(x);
			for (OWLNamedIndividual indForX : originalIndsForX) {
				originalIndsMappedToZ.addAll(orarOntology.getPredecessors(indForX, preRole));
			}
			sharedMap.getMap_ZAbstractIndiv_2_OriginalIndivs().put(z, originalIndsMappedToZ);

			/*
			 * map: (z,x) --> preRole if preRole is inverse functional
			 */
			if (this.sharedData.getInverseFunctionalRoles().contains(preRole)) {
				PairOfSubjectAndObject zxPair = new PairOfSubjectAndObject(z, x);

				this.sharedMap.getMap_ZX_2_Role().put(zxPair, preRole);
			}

		}
		return preRoleAssertions;

	}

	protected Set<OWLAxiom> getSuccessorRoleAssertions(OWLNamedIndividual x, IndividualType type) {
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

			Set<OWLNamedIndividual> originalIndsForX = sharedMap.getMap_XAbstractIndiv_2_OriginalIndivs().get(x);
			for (OWLNamedIndividual indForX : originalIndsForX) {
				originalIndsMappedToY.addAll(orarOntology.getSuccessors(indForX, succRole));
			}
			sharedMap.getMap_YAbstractIndiv_2_OriginalIndivs().put(y, originalIndsMappedToY);

			/*
			 * map: (x,y) --> succRole if succRole is functional
			 */
			if (this.sharedData.getFunctionalRoles().contains(succRole)) {
				PairOfSubjectAndObject xyPair = new PairOfSubjectAndObject(x, y);

				this.sharedMap.getMap_XY_2_Role().put(xyPair, succRole);
			}

		}
		return propertyAssertions;
	}

	protected abstract Set<OWLAxiom> getConceptAssertionsForConceptType(IndividualType type);

	protected abstract void markXHavingFunctionalRole(OWLNamedIndividual xIndividual, IndividualType type);

	protected abstract void markZHavingInverseFunctionalRole(OWLNamedIndividual zIndividual, OWLObjectProperty role);
}
