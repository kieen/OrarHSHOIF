package orar.innerreasoner;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uk.ac.manchester.cs.jfact.JFactFactory;

public class FactInnerReasoner extends InnerReasonerTemplate {

	public FactInnerReasoner(OWLOntology owlOntology) {
		super(owlOntology);

	}

	@Override
	protected OWLReasoner getOWLReasoner(OWLOntology ontology) {
		OWLReasonerFactory reasonerFactory = new JFactFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

		return reasoner;
	}

	@Override
	protected void dispose() {
		reasoner.dispose();

	}

}
