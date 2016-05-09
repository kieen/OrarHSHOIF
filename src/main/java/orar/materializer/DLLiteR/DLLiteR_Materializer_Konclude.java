package orar.materializer.DLLiteR;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.innerreasoner.InnerReasoner;
import orar.innerreasoner.HornSHIF.Konclude_HornSHIF_InnerReasoner;
import orar.materializer.DLLiteR_MaterializeTemplate;
import orar.modeling.ontology.OrarOntology;

public class DLLiteR_Materializer_Konclude extends DLLiteR_MaterializeTemplate {

	private int port = 8080;

	public DLLiteR_Materializer_Konclude(OrarOntology normalizedOrarOntology) {
		super(normalizedOrarOntology);

	}

	public DLLiteR_Materializer_Konclude(OrarOntology normalizedOrarOntology, int port) {
		super(normalizedOrarOntology);
		this.port = port;
	}

	@Override
	protected InnerReasoner getInnerReasoner(OWLOntology abstraction) {
		InnerReasoner reasoner;
		if (this.port == 8080) {
			reasoner = new Konclude_HornSHIF_InnerReasoner(abstraction);
		} else {
			reasoner = new Konclude_HornSHIF_InnerReasoner(abstraction, port);
		}

		return reasoner;
	}
}