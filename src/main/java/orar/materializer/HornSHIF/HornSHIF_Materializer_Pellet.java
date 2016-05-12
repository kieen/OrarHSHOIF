package orar.materializer.HornSHIF;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.innerreasoner.InnerReasoner;
import orar.innerreasoner.HornSHIF.Pellet_HornSHIF_InnerReasoner;
import orar.modeling.ontology.OrarOntology;

public class HornSHIF_Materialization_Pellet extends HornSHIF_Materializer {

	public HornSHIF_Materialization_Pellet(OrarOntology normalizedOrarOntology) {
		super(normalizedOrarOntology);

	}

	@Override
	protected InnerReasoner getInnerReasoner(OWLOntology abstraction) {
		InnerReasoner reasoner = new Pellet_HornSHIF_InnerReasoner(abstraction);
		return reasoner;
	}

}
