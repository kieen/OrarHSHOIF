package orar.abstraction.HornSHOIF;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import orar.abstraction.HornSHIF.HornSHIF_AbstractionGenerator;
import orar.modeling.ontology.OrarOntology;
import orar.type.IndividualType;

public class HornSHOIF_AbstractionGenerator extends HornSHIF_AbstractionGenerator {

	public HornSHOIF_AbstractionGenerator(OrarOntology orarOntology,
			Map<IndividualType, Set<OWLNamedIndividual>> typeMap2Individuals) {
		super(orarOntology, typeMap2Individuals);

	}

	@Override
	protected Set<OWLAxiom> getConceptAssertionsForConceptType(IndividualType type) {
		Set<OWLAxiom> abstractAssertions = new HashSet<OWLAxiom>();
		/*
		 * create u
		 */
		OWLNamedIndividual u = abstractDataFactory.createAbstractIndividualU();
		/*
		 * map u to original individuals.
		 */
		Set<OWLNamedIndividual> originalIndsForThisType = this.typeMap2Individuals.get(type);
		sharedMap.getMap_UAbstractIndiv_2_OriginalIndivs().put(u, originalIndsForThisType);
		/*
		 * create abstract class assertions for u
		 */
		abstractAssertions.addAll(getConceptAssertions(u, type));
		return abstractAssertions;
	}

}
