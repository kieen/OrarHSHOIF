package completenesschecker;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;

import orar.config.Configuration;
import orar.config.LogInfo;
import orar.dlreasoner.DLReasoner;
import orar.materializer.Materializer;
import orar.modeling.ontology.OrarOntology;
import orar.util.PrintingHelper;

public class CompletenessCheckerHorn implements CompletenessChecker {
	private static Logger logger = Logger.getLogger(CompletenessCheckerHorn.class);
	private Configuration config = Configuration.getInstance();
	private boolean hasTheSameResult;

	public CompletenessCheckerHorn() {
		this.hasTheSameResult = false;
	}

	@Override
	public void checkCompleteness(Materializer materializer, DLReasoner owlRealizer) {

		/*
		 * get result by abstraction
		 */

		materializer.materialize();
		OrarOntology orarOntology = materializer.getOrarOntology();
		Set<OWLClassAssertionAxiom> resultByAbstraction = orarOntology.getOWLAPIConceptAssertionsWHITOUTNormalizationSymbols();

		/*
		 * get result by owlrealizer
		 */
		owlRealizer.computeEntailments();
		Set<OWLClassAssertionAxiom> resultByOWLRealizer = owlRealizer.getEntailedConceptAssertions();

		this.hasTheSameResult = resultByAbstraction.equals(resultByOWLRealizer);
		logger.info("Number of derived concept assertions by abstraction materializer:" + resultByAbstraction.size());

		logger.info("Number of derived concept assertions by OWL reasoner:" + resultByOWLRealizer.size());
		if (config.getLogInfos().contains(LogInfo.COMPARED_RESULT_INFO)) {
			if (!hasTheSameResult) {

				HashSet<OWLClassAssertionAxiom> copyOfResultByAbstraction = new HashSet<>(resultByAbstraction);

				HashSet<OWLClassAssertionAxiom> copyOfResultByOWLRealizer = new HashSet<>(resultByOWLRealizer);

				copyOfResultByAbstraction.removeAll(resultByOWLRealizer);
				copyOfResultByOWLRealizer.removeAll(resultByAbstraction);

				logger.info("========Concept asesrtions by abstraction but not by OWLRealizer=============");
				PrintingHelper.printSet(logger, copyOfResultByAbstraction);

				logger.info("========Concept assertions by OWLRealizer but not by abstraction=============");
				PrintingHelper.printSet(logger, copyOfResultByOWLRealizer);

			}
		}

	}

	@Override
	public boolean isComplete() {

		return this.hasTheSameResult;
	}

	@Override
	public boolean isCompleteByOverapproximationCheck() {
		// It is proven to be always true in Horn case.
		return true;
	}
}
