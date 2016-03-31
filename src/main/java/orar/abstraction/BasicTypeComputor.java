package orar.abstraction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import orar.modeling.ontology.OrarOntology;
import orar.type.BasicIndividualTypeFactory;
import orar.type.BasicIndividualTypeFactory_UsingWeakHashMap;
import orar.type.IndividualType;
import orar.util.MapOperator;

/**
 * 
 * 
 * @author kien
 *
 */
public class BasicTypeComputor implements TypeComputor {
	private final BasicIndividualTypeFactory typeFactory;

	// private Configuration config;
	// private static Logger logger =
	// Logger.getLogger(HornSHOIF_TypeComputor.class);

	public BasicTypeComputor() {
		typeFactory = BasicIndividualTypeFactory_UsingWeakHashMap.getInstance();

		// this.config = Configuration.getInstance();
	}

	@Override
	public Map<IndividualType, Set<OWLNamedIndividual>> computeTypes(OrarOntology orarOntology) {
		Map<IndividualType, Set<OWLNamedIndividual>> typeMap2Individuals = new HashMap<>();
		Set<OWLNamedIndividual> todoIndividuals = orarOntology.getIndividualsInSignature();
		/*
		 * compute type for each individual, taking into account other equal
		 * individuals
		 */
		for (OWLNamedIndividual currentIndividual : todoIndividuals) {

			/*
			 * collect from assertions of "currentIndividual" and its equal
			 * individuals
			 */
			Set<OWLNamedIndividual> sameIndsOfCurrentIndividual = orarOntology.getSameIndividuals(currentIndividual);
			// sameInds should contains also the "currentIndividual"
			sameIndsOfCurrentIndividual.add(currentIndividual);

			// get element of accumulate type
			Set<OWLClass> concepts = getConcepts(sameIndsOfCurrentIndividual, orarOntology);
			Set<OWLObjectProperty> preRoles = getPreRoles(sameIndsOfCurrentIndividual, orarOntology);
			Set<OWLObjectProperty> sucRoles = getSuccRoles(sameIndsOfCurrentIndividual, orarOntology);

			// create type and add to the resulting map
			IndividualType type = typeFactory.getIndividualType(concepts, preRoles, sucRoles);

			// Map type to a set of individuals

			MapOperator.addValuesToMap(typeMap2Individuals, type, sameIndsOfCurrentIndividual);

		}

		return typeMap2Individuals;

	}

	/**
	 * @param individual
	 * @return asserted concepts of the {@code individual} taking into account
	 *         sameas individuals.
	 * 
	 */
	private Set<OWLClass> getConcepts(OWLNamedIndividual individual, OrarOntology orarOntology) {
		Set<OWLClass> accumulatedConcepts = new HashSet<>();
		Set<OWLNamedIndividual> sameIndsOfCurrentIndividual = orarOntology.getSameIndividuals(individual);
		// sameInds should contains also the "currentIndividual"
		sameIndsOfCurrentIndividual.add(individual);
		// accumulate types
		for (OWLNamedIndividual ind : sameIndsOfCurrentIndividual) {

			Set<OWLClass> concepts = orarOntology.getAssertedConcepts(ind);
			if (concepts == null) {
				concepts = new HashSet<>();
			}
			accumulatedConcepts.addAll(concepts);
		}
		return accumulatedConcepts;
	}

	/**
	 * @param individuals
	 * @return asserted concepts of all individual in the {@code individuals}
	 *         taking into account sameas individuals.
	 * 
	 */
	private Set<OWLClass> getConcepts(Set<OWLNamedIndividual> individuals, OrarOntology orarOntology) {
		Set<OWLClass> accumulatedConcepts = new HashSet<>();
		for (OWLNamedIndividual individual : individuals) {
			accumulatedConcepts.addAll(getConcepts(individual, orarOntology));
		}
		return accumulatedConcepts;
	}

	/**
	 * @param individuals
	 * @param orarOntology
	 * @return a set of successor roles of all individual in {@code individuals}
	 */
	private Set<OWLObjectProperty> getSuccRoles(Set<OWLNamedIndividual> individuals, OrarOntology orarOntology) {
		Set<OWLObjectProperty> accumulatedRoles = new HashSet<>();
		for (OWLNamedIndividual ind : individuals) {

			Set<OWLObjectProperty> sucRoles = orarOntology.getSuccessorRoleAssertionsAsMap(ind).keySet();

			accumulatedRoles.addAll(sucRoles);
		}
		return accumulatedRoles;

	}

	/**
	 * 
	 * @param individuals
	 * @param orarOntology
	 * @return a set of predecessor roles of all individual in
	 *         {@code individuals}
	 */
	private Set<OWLObjectProperty> getPreRoles(Set<OWLNamedIndividual> individuals, OrarOntology orarOntology) {
		Set<OWLObjectProperty> accumulatedRoles = new HashSet<>();
		for (OWLNamedIndividual ind : individuals) {

			Set<OWLObjectProperty> sucRoles = orarOntology.getPredecessorRoleAssertionsAsMap(ind).keySet();

			accumulatedRoles.addAll(sucRoles);
		}
		return accumulatedRoles;

	}

}