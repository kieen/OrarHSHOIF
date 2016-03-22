package orar.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

/**
 * Store some extra information of the ontology
 * 
 * @author kien
 *
 */
public class SharedData {

	private static SharedData instance;
	private final Set<OWLNamedIndividual> nominals;
	private final Set<OWLClass> nominalConcepts;

	private final Set<OWLObjectProperty> functionalRoles;
	private final Set<OWLObjectProperty> inverseFunctionalRoles;

	private final Map<OWLObjectProperty, Set<OWLObjectPropertyExpression>> subRoleMap;

	private SharedData() {
		this.nominalConcepts = new HashSet<>();
		this.nominals = new HashSet<>();
		this.functionalRoles = new HashSet<>();
		this.inverseFunctionalRoles = new HashSet<>();
		this.subRoleMap = new HashMap<OWLObjectProperty, Set<OWLObjectPropertyExpression>>();
	}

	public static SharedData getInstance() {
		if (instance == null) {
			instance = new SharedData();
		}
		return instance;
	}

	public Set<OWLNamedIndividual> getNominals() {
		return nominals;
	}

	public Set<OWLClass> getNominalConcepts() {
		return nominalConcepts;
	}

	public Set<OWLObjectProperty> getFunctionalRoles() {
		return functionalRoles;
	}

	public Map<OWLObjectProperty, Set<OWLObjectPropertyExpression>> getSubRoleMap() {
		return subRoleMap;
	}

	public Set<OWLObjectProperty> getInverseFunctionalRoles() {
		return inverseFunctionalRoles;
	}

	public void clear() {
		this.nominalConcepts.clear();
		this.nominals.clear();
		this.functionalRoles.clear();
		this.inverseFunctionalRoles.clear();
		this.subRoleMap.clear();
	}
}
