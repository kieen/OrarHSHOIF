package orar.materializer.HornSHOIF;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.innerreasoner.InnerReasoner;
import orar.innerreasoner.HornSHOIF.Hermit_HornSHOIF_InnerReasoner;
import orar.modeling.ontology.OrarOntology;

public class HornSHOIF_Materializer_Hermit extends HornSHOIF_Materializer {

	public HornSHOIF_Materializer_Hermit(OrarOntology normalizedOrarOntology) {
		super(normalizedOrarOntology);

	}

	@Override
	protected InnerReasoner getInnerReasoner(OWLOntology abstraction) {
		InnerReasoner reasoner = new Hermit_HornSHOIF_InnerReasoner(abstraction);
		return reasoner;
	}

}
