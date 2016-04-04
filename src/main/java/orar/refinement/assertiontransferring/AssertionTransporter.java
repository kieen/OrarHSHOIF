package orar.refinement.assertiontransferring;

/**
 * Adding assertions to the original ABox based on the entailments of the
 * abstraction.
 * 
 * @author kien
 *
 */
public interface AssertionTransporter {

	/**
	 * Adding assertions to the original ABox based on the entailments of the
	 * abstraction.
	 */
	public void updateOriginalABox();

	/**
	 * @return true if the original ABox has been extended; false otherwise
	 */
	public boolean isABoxExtended();
}
