package orar.abstraction;

import java.util.List;

import org.semanticweb.owlapi.model.OWLOntology;

public interface AbstractionGenerator {

	/**
	 * @param numberOfTypesPerAbstraction
	 *            the number of types per abstraction (in case of splitting
	 *            abstraction into smaller ones)
	 * 
	 * @return a list of OWLAPI abstract ontologies
	 */
	public List<OWLOntology> getAbstractOntologies(int numberOfTypesPerAbstraction);

	/**
	 *
	 * @return the abstract ontology generated from all types.s
	 */
	public OWLOntology getAbstractOntology();
}
