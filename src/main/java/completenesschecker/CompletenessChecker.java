package completenesschecker;

import orar.dlreasoner.DLReasoner;
import orar.materializer.Materializer;

/**
 * @author kien
 *
 */
public interface CompletenessChecker {
	/**
	 * 
	 * Compare results (concept assertions) by using abstraction with the
	 * results by using OWLReasoner over Horn ontologies.
	 * 
	 * @param materializer
	 * @param owlRealizer
	 */
	public void checkCompleteness(Materializer materializer, DLReasoner owlRealizer);

	/**
	 * @return true if concept assertions derived by abstraction materializer
	 *         are identical to the ones by OWLReasoner, false otherwise.
	 */
	public boolean isComplete();

	/**
	 * @return true if over approximation does not bring any new concept
	 *         assertion. In other words, the system provide complete results;
	 *         false otherwise.
	 */
	public boolean isCompleteByOverapproximationCheck();
}
