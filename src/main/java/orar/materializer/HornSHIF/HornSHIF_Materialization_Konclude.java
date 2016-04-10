package orar.materializer.HornSHIF;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.innerreasoner.InnerReasoner;
import orar.innerreasoner.HornSHIF.Konclude_HornSHIF_InnerReasoner;
import orar.modeling.ontology.OrarOntology;

public class HornSHIF_Materialization_Konclude extends HornSHIF_Materializer {

	public HornSHIF_Materialization_Konclude(OrarOntology normalizedOrarOntology) {
		super(normalizedOrarOntology);

	}

	@Override
	protected InnerReasoner getInnerReasoner(OWLOntology abstraction) {
		InnerReasoner reasoner = new Konclude_HornSHIF_InnerReasoner(abstraction);
		return reasoner;
	}

}
