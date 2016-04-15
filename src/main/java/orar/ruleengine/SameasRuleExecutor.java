package orar.ruleengine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import orar.modeling.ontology.OrarOntology;
import orar.refinement.abstractroleassertion.RoleAssertionList;

public class SameasRuleExecutor implements RuleExecutor {

	private final OrarOntology orarOntology;
	private boolean isABoxExtended;
	// private Queue<Set<OWLNamedIndividual>> localTodoSameas;
	private final Logger logger = Logger.getLogger(SameasRuleExecutor.class);
	public SameasRuleExecutor(OrarOntology orarOntology) {
		this.orarOntology = orarOntology;
		this.isABoxExtended = false;
	}

	@Override
	public void materialize() {
		
		// compute connect components
		List<Set<OWLNamedIndividual>> components = computeConnectedComponents();
		// put components to the map.
		for (Set<OWLNamedIndividual> component : components) {
			for (OWLNamedIndividual ind : component) {
				if (this.orarOntology.addManySameAsAssertions(ind, component)) {
					this.isABoxExtended = true;
				}
			}
		}
	}

	

	private List<Set<OWLNamedIndividual>> computeConnectedComponents() {
		Set<OWLNamedIndividual> allIndividualsInSameasMap = this.orarOntology.getSameasBox().getAllIndividuals();
		Queue<OWLNamedIndividual> todoIndividuals = new LinkedList<OWLNamedIndividual>(allIndividualsInSameasMap);
		List<Set<OWLNamedIndividual>> components = new ArrayList<Set<OWLNamedIndividual>>();
		while (!todoIndividuals.isEmpty()) {
			OWLNamedIndividual a = todoIndividuals.poll();
			// compute component for each individual a in todoIndividuals.
			Set<OWLNamedIndividual> newComponent = new HashSet<OWLNamedIndividual>();
			newComponent.add(a);
			Stack<OWLNamedIndividual> stackForDFS = new Stack<OWLNamedIndividual>();
			stackForDFS.push(a);
			while (!stackForDFS.isEmpty()) {
				OWLNamedIndividual ind = stackForDFS.pop();
				Set<OWLNamedIndividual> sameasOf_a = this.orarOntology.getSameIndividuals(ind);
				sameasOf_a.removeAll(newComponent);
				newComponent.addAll(sameasOf_a);
				stackForDFS.addAll(sameasOf_a);
				todoIndividuals.removeAll(sameasOf_a);
			}
			components.add(newComponent);
		}
		return components;
	}

	@Override
	public Set<Set<OWLNamedIndividual>> getNewSameasAssertions() {
		// return empty set in this rule
		return new HashSet<Set<OWLNamedIndividual>>();
	}

	@Override
	public Set<OWLObjectPropertyAssertionAxiom> getNewRoleAssertions() {
		// return empty set in this rule
		return new HashSet<OWLObjectPropertyAssertionAxiom>();
	}

	@Override
	public boolean isABoxExtended() {

		return this.isABoxExtended;
	}

	@Override
	public void incrementalMaterialize(Set<OWLNamedIndividual> setOfSameasIndividuals) {
//		logger.info("SameasRuleExecutor.incrementalMaterialize");
		// get union of equivalent individuals in the set.
		Set<OWLNamedIndividual> accumulatedSameasIndividuals = new HashSet<OWLNamedIndividual>();
		accumulatedSameasIndividuals.addAll(setOfSameasIndividuals);
		for (OWLNamedIndividual ind : setOfSameasIndividuals) {
			accumulatedSameasIndividuals.addAll(this.orarOntology.getSameIndividuals(ind));
		}
		// update the map
		for (OWLNamedIndividual eachIndividual : accumulatedSameasIndividuals) {
			if (this.orarOntology.addManySameAsAssertions(eachIndividual, accumulatedSameasIndividuals)) {
				this.isABoxExtended = true;
			}
		}
	}

	@Override
	public void incrementalMaterialize(OWLObjectPropertyAssertionAxiom roleAssertion) {
		// nothing to do with a role assertion.
	}

	@Override
	public void clearOldBuffer() {
		// nothing to clear

	}
}
