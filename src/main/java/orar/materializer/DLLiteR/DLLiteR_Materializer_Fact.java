package orar.materializer.DLLiteR;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.innerreasoner.InnerReasoner;
import orar.innerreasoner.HornSHIF.Fact_HornSHIF_InnerReasoner;
import orar.materializer.DLLiteR_MaterializerTemplate;
import orar.modeling.ontology.OrarOntology;

public class DLLiteR_Materializer_Fact extends DLLiteR_MaterializerTemplate {

	public DLLiteR_Materializer_Fact(OrarOntology normalizedOrarOntology) {
		super(normalizedOrarOntology);

	}

	@Override
	protected InnerReasoner getInnerReasoner(OWLOntology abstraction) {
		InnerReasoner reasoner = new Fact_HornSHIF_InnerReasoner(abstraction);
		return reasoner;
	}

}