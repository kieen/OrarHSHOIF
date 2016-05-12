package orar.innerexplanation;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.impl.blackbox.checker.InconsistentOntologyExplanationGeneratorFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public abstract class InnerConsistencyCheckerTemplate implements InnerConsistencyChecker {
	protected final OWLOntology owlOntology;
	private final Set<Explanation<OWLAxiom>> explanations;
	// private OWLReasonerFactory reasonerFactory;
	private final long timeoutInSeconds;
	private OWLDataFactory dataFactory;

	public InnerConsistencyCheckerTemplate(OWLOntology owlOntology, long timeoutInSeconds) {
		this.owlOntology = owlOntology;
		this.explanations = new HashSet<>();
		this.timeoutInSeconds = timeoutInSeconds;
		this.dataFactory = OWLManager.getOWLDataFactory();
	}

	protected abstract OWLReasoner getReasoner();

	protected abstract OWLReasonerFactory getOWLReasonerFactory();

	@Override
	public boolean isConsistent() {
		OWLReasoner owlReasoner = getReasoner();
		return owlReasoner.isConsistent();

	}

	@Override
	public Set<Explanation<OWLAxiom>> getExplanations(int maxNumberOfExplanations) {
		computeExplanation(maxNumberOfExplanations);
		return this.explanations;
	}

	private void computeExplanation(int maxNumberOfExplanations) {
		OWLReasonerFactory reasonerFactory = getOWLReasonerFactory();

		/*
		 * Create the explanation generator factory which uses reasoners
		 * provided by the specified reasoner factory. 1000 is the timeout for
		 * reasoning; not clear s or ms.
		 * 
		 */

		InconsistentOntologyExplanationGeneratorFactory explGeneratorFac = new InconsistentOntologyExplanationGeneratorFactory(
				reasonerFactory, this.timeoutInSeconds);

		/*
		 * Now create the actual explanation generator for our ontology
		 * 
		 */
		ExplanationGenerator<OWLAxiom> explGenerator = explGeneratorFac.createExplanationGenerator(this.owlOntology);

		/*
		 * Ask for explanations for some entailment. For inconsistency we ask
		 * for top implies bottom.
		 * 
		 */
		OWLAxiom entailment = dataFactory.getOWLSubClassOfAxiom(dataFactory.getOWLThing(), dataFactory.getOWLNothing());

		/*
		 * Get our explanations.
		 */

		this.explanations.addAll(explGenerator.getExplanations(entailment, maxNumberOfExplanations));
	}

}
