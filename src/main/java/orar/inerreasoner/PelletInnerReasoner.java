package orar.inerreasoner;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class PelletInnerReasoner extends InnerReasonerTemplate {

	public PelletInnerReasoner(OWLOntology owlOntology) {
		super(owlOntology);

	}

	@Override
	protected OWLReasoner getOWLReasoner(OWLOntology ontology) {
		OWLReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
		return reasoner;
	}

	@Override
	protected void dispose() {
		reasoner.dispose();

	}
}
