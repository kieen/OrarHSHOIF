package orar.io.ontologyreader;

import org.semanticweb.owlapi.model.OWLOntology;

import orar.modeling.ontology.OrarOntology;

public interface OntologyReader {
	public OrarOntology getNormalizedOrarOntology(String ontologyFileName);

	public OrarOntology getNormalizedOrarOntology(String tboxFileName, String aboxListFileName);

	public OWLOntology getOWLAPIOntology(String ontologyFileName);

	public OWLOntology getOWLAPIOntology(String tboxFile, String aboxListFile);

}
