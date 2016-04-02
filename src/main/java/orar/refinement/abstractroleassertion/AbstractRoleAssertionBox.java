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
public class AbstractRoleAssertionBox {

	/**
	 * role assertions of the form R(x,y) , where x is the representative for
	 * combined-type, y its successor.
	 */
	private final RoleAssertionList xyRoleAssertionsForType;

	/**
	 * role assertions of the form R(z,x), where x is the representative for
	 * combined-type, z its predecessor.
	 */
	private final RoleAssertionList zxRoleAssertionsForType;
	/**
	 * role assertions of the form T(u,u), where T is transitive, and u is the
	 * representative individual for concept-type
	 */
	private final RoleAssertionList uuLoopRoleAssertionsForCType;
	/**
	 * role assertions of the form R(u,x), where u is the representative for the
	 * concept-type and x is the representative for the combined-type.
	 */
	private final RoleAssertionList uxRoleAssertionsForCTypeAndType;

	public AbstractRoleAssertionBox() {
		this.xyRoleAssertionsForType = new RoleAssertionList();
		this.zxRoleAssertionsForType = new RoleAssertionList();
		this.uuLoopRoleAssertionsForCType = new RoleAssertionList();
		this.uxRoleAssertionsForCTypeAndType = new RoleAssertionList();
	}

	/**
	 * add a role assertion whose individuals come from a (combine) type, e.g.
	 * assertions of the form R(x,y)
	 * 
	 * @param subject
	 *            any representative of type
	 * @param role
	 *            any role
	 * @param object
	 *            any successor of the representative of type
	 */
	public void addXY_RoleAssertionForType(OWLNamedIndividual subject, OWLObjectProperty role,
			OWLNamedIndividual object) {
		this.xyRoleAssertionsForType.addRoleAssertion(subject, role, object);
	}

	/**
	 * add a role assertion whose individuals come from a (combine) type, e.g.
	 * assertions of the form R(z,x)
	 * 
	 * @param subject
	 *            any predecessor
	 * @param role
	 *            any role
	 * @param object
	 *            any representative of type
	 */
	public void addZX_RoleAssertionForType(OWLNamedIndividual subject, OWLObjectProperty role,
			OWLNamedIndividual object) {
		this.zxRoleAssertionsForType.addRoleAssertion(subject, role, object);
	}

	/**
	 * add a loop role assertion whose individuals come from concept type, e.g.
	 * T(u,u)
	 * 
	 * @param subjectEqualToObject
	 * @param role
	 *            a transitive role
	 */
	public void addUU_LoopRoleAssertionForCType(OWLNamedIndividual subjectEqualToObject, OWLObjectProperty role) {
		this.uuLoopRoleAssertionsForCType.addRoleAssertion(subjectEqualToObject, role, subjectEqualToObject);
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
	public void addUX_RoleAssertionForCTypeAndType(OWLNamedIndividual subject, OWLObjectProperty role,
			OWLNamedIndividual object) {
		this.uxRoleAssertionsForCTypeAndType.addRoleAssertion(subject, role, object);
	}

	/*
	 * getters
	 * 
	 */
	public RoleAssertionList getTypeRoleAssertions() {
		return xyRoleAssertionsForType;
	}

	public RoleAssertionList getLoopCTypeRoleAssertions() {
		return uuLoopRoleAssertionsForCType;
	}

	public RoleAssertionList getCtypeAndTypeRoleAssertions() {
		return uxRoleAssertionsForCTypeAndType;
	}

}
