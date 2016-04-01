package orar.refinement.abstractroleassertion;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * Data structure for entailed abstract role assertions. It contains several
 * kinds of role assertions, for which transferring assertions is different.
 * 
 * @author kien
 *
 */
public class EntailedAbstractRoleAssertions {

	/**
	 * role assertions of the form R(x,y) or R(z,x), where x is the
	 * representative for combined-type, y,z are its succ/predecessor.
	 */
	private final ListOfRoleAssertion typeRoleAssertions;
	/**
	 * role assertions of the form T(u,u), where T is transitive, and u is the
	 * representative individual for concept-type
	 */
	private final ListOfRoleAssertion loopCTypeRoleAssertions;
	/**
	 * role assertions of the form R(u,x), where u is the representative for the
	 * concept-type and x is the representative for the combined-type.
	 */
	private final ListOfRoleAssertion ctypeAndTypeRoleAssertions;

	public EntailedAbstractRoleAssertions() {
		this.typeRoleAssertions = new ListOfRoleAssertion();
		this.loopCTypeRoleAssertions = new ListOfRoleAssertion();
		this.ctypeAndTypeRoleAssertions = new ListOfRoleAssertion();
	}

	/**
	 * add a role assertion whose individuals come from a (combine) type, e.g.
	 * assertions of the form R(x,y) or R(z,x)
	 * 
	 * @param subject
	 *            any representative of type
	 * @param role
	 *            any role
	 * @param object
	 *            any pre/successor of the representative of type
	 */
	public void addRoleAssertionForType(OWLNamedIndividual subject, OWLObjectProperty role, OWLNamedIndividual object) {
		this.typeRoleAssertions.addRoleAssertion(subject, role, object);
	}

	/**
	 * add a loop role assertion whose individuals come from concept type, e.g.
	 * T(u,u)
	 * 
	 * @param subjectEqualToObject
	 * @param role
	 *            a transitive role
	 */
	public void addRoleAssertionForLoopCType(OWLNamedIndividual subjectEqualToObject, OWLObjectProperty role) {
		this.loopCTypeRoleAssertions.addRoleAssertion(subjectEqualToObject, role, subjectEqualToObject);
	}

	/**
	 * add a role assertion in which individuals come from ConceptType and Type,
	 * e.g. R(u,x)
	 * 
	 * @param subject
	 *            representative of ConceptType
	 * @param role
	 *            any role
	 * @param object
	 *            representative of Type
	 */
	public void addRoleAssertionForCTypeAndType(OWLNamedIndividual subject, OWLObjectProperty role,
			OWLNamedIndividual object) {
		this.ctypeAndTypeRoleAssertions.addRoleAssertion(subject, role, object);
	}

	/*
	 * getters
	 * 
	 */
	public ListOfRoleAssertion getTypeRoleAssertions() {
		return typeRoleAssertions;
	}

	public ListOfRoleAssertion getLoopCTypeRoleAssertions() {
		return loopCTypeRoleAssertions;
	}

	public ListOfRoleAssertion getCtypeAndTypeRoleAssertions() {
		return ctypeAndTypeRoleAssertions;
	}

}
