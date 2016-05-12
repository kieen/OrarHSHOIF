package orar.materializer.HornSHOIF;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.innerreasoner.InnerReasoner;
import orar.innerreasoner.HornSHOIF.Fact_HornSHOIF_InnerReasoner;
import orar.modeling.ontology.OrarOntology;

public class HornSHOIF_Materializer_Fact extends HornSHOIF_Materializer {

	public HornSHOIF_Materializer_Fact(OrarOntology normalizedOrarOntology) {
		super(normalizedOrarOntology);

	}

	@Override
	protected InnerReasoner getInnerReasoner(OWLOntology abstraction) {
		InnerReasoner reasoner = new Fact_HornSHOIF_InnerReasoner(abstraction);
		return reasoner;
	}

}
