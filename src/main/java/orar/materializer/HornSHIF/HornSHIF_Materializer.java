package orar.materializer.HornSHIF;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import orar.abstraction.AbstractionGenerator;
import orar.abstraction.HornSHIF.HornSHIF_AbstractionGenerator;
import orar.innerreasoner.InnerReasoner;
import orar.materializer.MaterializerTemplate;
import orar.modeling.ontology.OrarOntology;
import orar.refinement.abstractroleassertion.AbstractRoleAssertionBox;
import orar.refinement.assertiontransferring.AssertionTransporter;
import orar.refinement.assertiontransferring.HornSHIF.HornSHIF_AssertionTransporter;
import orar.type.IndividualType;

public abstract class HornSHIF_Materializer extends MaterializerTemplate {

	public HornSHIF_Materializer(OrarOntology normalizedOrarOntology) {
		super(normalizedOrarOntology);

	}

	@Override
	protected List<OWLOntology> getAbstractions(Map<IndividualType, Set<OWLNamedIndividual>> typeMap2Individuals) {
		int numberOfTypesPerOntology = this.config.getNumberOfTypePerOntology();
		/*
		 * 
		 */
		if (numberOfTypesPerOntology < 0) {
			numberOfTypesPerOntology = typeMap2Individuals.size();
		}
		AbstractionGenerator abstractionGenerator = new HornSHIF_AbstractionGenerator(normalizedORAROntology,
				typeMap2Individuals);
		List<OWLOntology> abstractions = abstractionGenerator.getAbstractOntologies(numberOfTypesPerOntology);
		return abstractions;
	}

	@Override
	protected AssertionTransporter getAssertionTransporter(
			Map<OWLNamedIndividual, Set<OWLClass>> entailedAbstractConceptAssertions,
			AbstractRoleAssertionBox entailedAbstractRoleAssertion,
			Map<OWLNamedIndividual, Set<OWLNamedIndividual>> entailedSameasMap) {
		AssertionTransporter assertionTransporter = new HornSHIF_AssertionTransporter(normalizedORAROntology,
				entailedAbstractConceptAssertions, entailedAbstractRoleAssertion);
		return assertionTransporter;
	}

	@Override
	protected abstract InnerReasoner getInnerReasoner(OWLOntology abstraction);

}
