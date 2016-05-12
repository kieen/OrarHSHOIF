package orar.materializer.HornSHOIF;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.innerreasoner.InnerReasoner;
import orar.innerreasoner.HornSHOIF.Konclude_HornSHOIF_InnerReasoner;
import orar.modeling.ontology.OrarOntology;

public class HornSHOIF_Materialization_Konclude extends HornSHOIF_Materializer {
	private int port = 8080;

	public HornSHOIF_Materialization_Konclude(OrarOntology normalizedOrarOntology) {
		super(normalizedOrarOntology);

	}

	public HornSHOIF_Materialization_Konclude(OrarOntology normalizedOrarOntology, int port) {
		super(normalizedOrarOntology);
		this.port = port;
	}

	@Override
	protected InnerReasoner getInnerReasoner(OWLOntology abstraction) {
		InnerReasoner reasoner;
		if (this.port == 8080) {
			reasoner = new Konclude_HornSHOIF_InnerReasoner(abstraction);
		} else {
			reasoner = new Konclude_HornSHOIF_InnerReasoner(abstraction, port);
		}

		return reasoner;
	}

}
