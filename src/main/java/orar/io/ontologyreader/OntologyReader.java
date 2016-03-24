package orar.io.ontologyreader;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.modeling.ontology.OrarOntology;

/**
 * A collections of methods for loading ontology.
 * 
 * @author kien
 *
 */
public interface OntologyReader {
	/**
	 * @param ontologyFileName
	 *            a file that contains both TBox and ABox axioms
	 * @return an OrarOntology in the target DL Fragment with TBox axioms are
	 *         normalized
	 */
	public OrarOntology getNormalizedOrarOntology(String ontologyFileName);

	/**
	 * @param tboxFileName
	 *            a file for TBox axioms.
	 * @param aboxListFileName
	 *            a file that contains a list of ABox files.
	 * @return an OrarOntology in the target DL Fragment with TBox axioms are
	 *         normalized
	 */
	public OrarOntology getNormalizedOrarOntology(String tboxFileName, String aboxListFileName);

	/**
	 * @param ontologyFileName
	 *            a file that contains both TBox and ABox axioms
	 * @return a non-normalized OWLAPI ontology in the target DL fragment.
	 */
	public OWLOntology getOWLAPIOntology(String ontologyFileName);

	/**
	 * @param tboxFileName
	 *            a file for TBox axioms.
	 * @param aboxListFileName
	 *            a file that contains a list of ABox files.
	 * @return a non-normalized OWLAPI ontology in the target DL fragment.
	 */
	public OWLOntology getOWLAPIOntology(String tboxFile, String aboxListFile);

}
