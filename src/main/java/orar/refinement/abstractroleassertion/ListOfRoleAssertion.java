package orar.refinement.abstractroleassertion;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * A list of role assertions.
 * 
 * @author kien
 *
 */
public class ListOfRoleAssertion {

	private final List<OWLNamedIndividual> subjects;
	private final List<OWLObjectProperty> roles;
	private final List<OWLNamedIndividual> objects;

	public ListOfRoleAssertion() {

		this.subjects = new ArrayList<>();
		this.roles = new ArrayList<>();
		this.objects = new ArrayList<>();
	}

	public void addRoleAssertion(OWLNamedIndividual subject, OWLObjectProperty role, OWLNamedIndividual object) {
		this.subjects.add(subject);
		this.roles.add(role);
		this.objects.add(object);

	}

	public int getSize() {

		return this.subjects.size();
	}

	/**
	 * @param index
	 * @return the subject individual in the role assertion at the position
	 *         {@code index}
	 */
	public OWLNamedIndividual getSubject(int index) {
		return this.subjects.get(index);
	}

	/**
	 * @param index
	 * @return the role in the role assertion at the position {@code index}
	 */
	public OWLObjectProperty getRole(int index) {
		return this.roles.get(index);
	}

	/**
	 * @param index
	 * @return the object individual in the role assertion at the position
	 *         {@code index}
	 */
	public OWLNamedIndividual getObject(int index) {
		return this.objects.get(index);
	}

}
