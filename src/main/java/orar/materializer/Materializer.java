package orar.materializer;

import orar.modeling.ontology.OrarOntology;

public interface Materializer {

	/**
	 * Materialize via abstraction and refinement
	 */
	public void materialize();

	/**
	 * @return number of refinement steps.
	 */
	public int getNumberOfRefinements();

	public void dispose();

	/**
	 * @return the orarontology
	 */
	public OrarOntology getOrarOntology();

	public long getReasoningTimeInSeconds();
	
	public boolean isOntologyConsistent();
}
