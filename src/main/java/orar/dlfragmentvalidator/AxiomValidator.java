package orar.dlfragmentvalidator;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;

public interface AxiomValidator extends OWLAxiomVisitorEx<OWLAxiom> {
	public Set<DLConstructor> getDLConstructors();

	public Set<OWLAxiom> getViolatedAxioms();

	public boolean isNotViolatedProfile();

	/**
	 * @return newly generated axioms, which are validated part of violated
	 *         axioms. For example, given the axiom C or D =A, and we want to
	 *         get only horn part of this axiom. Then the new axiom: C or D
	 *         subclassof A is one of the newly generated axiom.
	 */
	public Set<OWLAxiom> getGeneratedAxioms();

	public int getNumberOfCardinalityAxioms();
}
