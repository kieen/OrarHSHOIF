package orar.materializer.HornSHOIF;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.innerreasoner.InnerReasoner;
import orar.innerreasoner.HornSHOIF.Konclude_HornSHOIF_InnerReasoner;
import orar.modeling.ontology.OrarOntology;

public class HornSHOIF_Materialization_Konclude extends HornSHOIF_Materializer {

	public HornSHOIF_Materialization_Konclude(OrarOntology normalizedOrarOntology) {
		super(normalizedOrarOntology);

	}

	@Override
	protected InnerReasoner getInnerReasoner(OWLOntology abstraction) {
		InnerReasoner reasoner = new Konclude_HornSHOIF_InnerReasoner(abstraction);
		return reasoner;
	}

}
