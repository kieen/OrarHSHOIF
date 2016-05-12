package orar.materializer.HornSHIF;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.innerreasoner.InnerReasoner;
import orar.innerreasoner.HornSHIF.Konclude_HornSHIF_InnerReasoner;
import orar.modeling.ontology.OrarOntology;

public class HornSHIF_Materializer_Konclude extends HornSHIF_Materializer {
	private int port = 8080;

	public HornSHIF_Materializer_Konclude(OrarOntology normalizedOrarOntology) {
		super(normalizedOrarOntology);

	}

	public HornSHIF_Materializer_Konclude(OrarOntology normalizedOrarOntology, int port) {
		super(normalizedOrarOntology);
		this.port = port;
	}

	@Override
	protected InnerReasoner getInnerReasoner(OWLOntology abstraction) {
		InnerReasoner reasoner;
		if (port == 8080) {
			reasoner = new Konclude_HornSHIF_InnerReasoner(abstraction);
		} else {
			reasoner = new Konclude_HornSHIF_InnerReasoner(abstraction, this.port);
		}
		return reasoner;
	}

}
