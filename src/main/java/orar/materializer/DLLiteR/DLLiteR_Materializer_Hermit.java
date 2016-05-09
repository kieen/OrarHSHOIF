package orar.materializer.DLLiteR;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.innerreasoner.InnerReasoner;
import orar.innerreasoner.HornSHIF.Hermit_HornSHIF_InnerReasoner;
import orar.materializer.DLLiteR_MaterializeTemplate;
import orar.modeling.ontology.OrarOntology;

public class DLLiteR_Materializer_Hermit extends DLLiteR_MaterializeTemplate {

	public DLLiteR_Materializer_Hermit(OrarOntology normalizedOrarOntology) {
		super(normalizedOrarOntology);

	}

	@Override
	protected InnerReasoner getInnerReasoner(OWLOntology abstraction) {
		InnerReasoner reasoner = new Hermit_HornSHIF_InnerReasoner(abstraction);
		return reasoner;
	}

}