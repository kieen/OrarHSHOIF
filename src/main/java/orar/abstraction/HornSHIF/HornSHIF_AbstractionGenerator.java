package orar.abstraction.HornSHIF;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import orar.abstraction.AbstractionGeneratorTemplate;
import orar.modeling.ontology.OrarOntology;
import orar.type.IndividualType;

public class HornSHIF_AbstractionGenerator extends AbstractionGeneratorTemplate {

	public HornSHIF_AbstractionGenerator(OrarOntology orarOntology,
			Map<IndividualType, Set<OWLNamedIndividual>> typeMap2Individuals) {
		super(orarOntology, typeMap2Individuals);

	}

	@Override
	protected Set<OWLAxiom> generateAssertions(IndividualType type) {
		Set<OWLAxiom> abstractAssertions = new HashSet<>();
		/*
		 * create x
		 */
		OWLNamedIndividual x = abstractDataFactory.createAbstractIndividualX();
		Set<OWLNamedIndividual> originalIndsForThisType = this.typeMap2Individuals.get(type);
		/*
		 * map X to original individuals.
		 */
		sharedMap.getMap_XAbstractIndiv_2_OriginalIndivs().put(x, originalIndsForThisType);

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

}
