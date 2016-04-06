package orar.ruleengine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import orar.modeling.ontology.OrarOntology;

public class SemiNaiveRuleEngine implements RuleEngine {
	private Queue<Set<OWLNamedIndividual>> todoSameasAssertions;
	private Queue<OWLObjectPropertyAssertionAxiom> todoRoleAssertions;
	private final OrarOntology orarOntology;
	private final Logger logger = Logger.getLogger(SemiNaiveRuleEngine.class);
	private RuleExecutor sameasRule;
	private RuleExecutor subroRule;
	private RuleExecutor tranRule;
	private RuleExecutor funcRule;
	private List<RuleExecutor> ruleExecutors;

	public SemiNaiveRuleEngine(OrarOntology orarOntology) {
		this.orarOntology = orarOntology;
		this.todoSameasAssertions = new LinkedList<>();
		this.todoRoleAssertions = new LinkedList<>();

		this.sameasRule = new SameasRuleExecutor(orarOntology);
		this.funcRule = new FunctionalityRuleExecutor(orarOntology);
		this.tranRule = new TransitivityRuleExecutor(orarOntology);
		this.subroRule = new SubRoleRuleExecutor(orarOntology);
		this.ruleExecutors= new ArrayList<>();
		this.ruleExecutors.add(sameasRule);
		this.ruleExecutors.add(subroRule);
		this.ruleExecutors.add(tranRule);
		this.ruleExecutors.add(funcRule);

	}

	@Override
	public void materialize() {
		for (RuleExecutor ruleEx : this.ruleExecutors) {
			ruleEx.materialize();
			this.todoRoleAssertions.addAll(ruleEx.getNewRoleAssertions());
			this.todoSameasAssertions.addAll(ruleEx.getNewSameasAssertions());
		}
		incrementalMaterialize();
	}

	@Override
	public void incrementalMaterialize() {
		while (!this.todoRoleAssertions.isEmpty() || !this.todoSameasAssertions.isEmpty()) {
			while (!this.todoSameasAssertions.isEmpty()) {
				Set<OWLNamedIndividual> setOfSameasIndividuals = this.todoSameasAssertions.poll();
				for (RuleExecutor ruleEx : this.ruleExecutors) {
					ruleEx.clearOldBuffer();
					ruleEx.incrementalMaterialize(setOfSameasIndividuals);
					this.todoRoleAssertions.addAll(ruleEx.getNewRoleAssertions());
					this.todoSameasAssertions.addAll(ruleEx.getNewSameasAssertions());
				}
			}

			while (!this.todoRoleAssertions.isEmpty()) {
				OWLObjectPropertyAssertionAxiom aRoleAssertion = this.todoRoleAssertions.poll();
				for (RuleExecutor ruleEx : this.ruleExecutors) {
					ruleEx.clearOldBuffer();
					ruleEx.incrementalMaterialize(aRoleAssertion);
					this.todoRoleAssertions.addAll(ruleEx.getNewRoleAssertions());
					this.todoSameasAssertions.addAll(ruleEx.getNewSameasAssertions());
				}
			}
		}
	}

	@Override
	public OrarOntology getOntology() {

		return this.orarOntology;
	}

}
