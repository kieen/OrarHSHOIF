package orar.ruleengine;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import orar.data.MetaDataOfOntology;
import orar.modeling.ontology.OrarOntology;

public class TransitivityRuleExecutor implements RuleExecutor {
	private final Set<OWLObjectPropertyAssertionAxiom> newRoleAssertions;
	private final OrarOntology orarOntology;
	private final MetaDataOfOntology metaDataOfOntology;
	private final OWLDataFactory dataFactory;
	private boolean isABoxExtended;

	private final Logger logger =Logger.getLogger(TransitivityRuleExecutor.class);
	public TransitivityRuleExecutor(OrarOntology orarOntology) {
		this.orarOntology = orarOntology;
		this.newRoleAssertions = new HashSet<>();
		this.metaDataOfOntology = MetaDataOfOntology.getInstance();
		this.dataFactory = OWLManager.getOWLDataFactory();
		this.isABoxExtended = false;
	}

	@Override
	public void materialize() {
		// get all transitive role assertions.
		Queue<OWLObjectPropertyAssertionAxiom> todoTranRoleAssertions = getAllTransitiveRoleAssertions();
		logger.info("all tran role assertions:"+todoTranRoleAssertions);
		while (!todoTranRoleAssertions.isEmpty()) {
			OWLObjectPropertyAssertionAxiom roleT_a_b = todoTranRoleAssertions.poll();
			OWLNamedIndividual a = roleT_a_b.getSubject().asOWLNamedIndividual();
			OWLNamedIndividual b = roleT_a_b.getObject().asOWLNamedIndividual();
			OWLObjectProperty T = roleT_a_b.getProperty().asOWLObjectProperty();
			Set<OWLNamedIndividual> many_c = this.orarOntology.getSuccessorsTakingEqualityIntoAccount(b, T);
			for (OWLNamedIndividual c : many_c) {
				if (this.orarOntology.addRoleAssertion(a, T, c)) {
					this.isABoxExtended = true;
					OWLObjectPropertyAssertionAxiom newRoleT_a_c = dataFactory.getOWLObjectPropertyAssertionAxiom(T, a,
							c);
					this.newRoleAssertions.add(newRoleT_a_c);
					todoTranRoleAssertions.add(newRoleT_a_c);
				}
			}
		}

	}

	private Queue<OWLObjectPropertyAssertionAxiom> getAllTransitiveRoleAssertions() {
		Queue<OWLObjectPropertyAssertionAxiom> transRoleAssertions = new LinkedList<>();
		Set<OWLObjectProperty> allTranRoles = this.metaDataOfOntology.getTransitiveRoles();
		for (OWLObjectProperty eachTranRole : allTranRoles) {
			Set<OWLNamedIndividual> subjects = this.orarOntology.getSubjectsInRoleAssertions(eachTranRole);
			for (OWLNamedIndividual eachSubject : subjects) {
				Set<OWLNamedIndividual> objects = this.orarOntology.getSuccessors(eachSubject, eachTranRole);
				for (OWLNamedIndividual eachObject : objects) {
					transRoleAssertions.add(
							this.dataFactory.getOWLObjectPropertyAssertionAxiom(eachTranRole, eachSubject, eachObject));
				}
			}
		}
		return transRoleAssertions;
	}

	@Override
	public void incrementalMaterialize(Set<OWLNamedIndividual> setOfSameasIndividuals) {
		Set<OWLObjectProperty> allTranRoles = this.metaDataOfOntology.getTransitiveRoles();
		for (OWLObjectProperty eachTranRole : allTranRoles) {
			Set<OWLNamedIndividual> allPredecessors = new HashSet<>();
			Set<OWLNamedIndividual> allSuccessors = new HashSet<>();
			for (OWLNamedIndividual eachIndividual : setOfSameasIndividuals) {
				allPredecessors.addAll(
						this.orarOntology.getPredecessorsTakingEqualityIntoAccount(eachIndividual, eachTranRole));
				allSuccessors
						.addAll(this.orarOntology.getSuccessorsTakingEqualityIntoAccount(eachIndividual, eachTranRole));
			}
			for (OWLNamedIndividual eachPre : allPredecessors) {
				for (OWLNamedIndividual eachSuc : allSuccessors) {
					if (this.orarOntology.addRoleAssertion(eachPre, eachTranRole, eachSuc)) {
						this.isABoxExtended = true;
						this.newRoleAssertions.add(
								this.dataFactory.getOWLObjectPropertyAssertionAxiom(eachTranRole, eachPre, eachSuc));
					}
				}
			}
		}

	}

	@Override
	public void incrementalMaterialize(OWLObjectPropertyAssertionAxiom roleAssertion) {
		OWLNamedIndividual a = roleAssertion.getSubject().asOWLNamedIndividual();
		OWLNamedIndividual b = roleAssertion.getObject().asOWLNamedIndividual();
		OWLObjectProperty T = roleAssertion.getProperty().asOWLObjectProperty();

		// T(a,b), T(b,c) --> T(a,c)
		Set<OWLNamedIndividual> many_c = this.orarOntology.getSuccessorsTakingEqualityIntoAccount(b, T);
		for (OWLNamedIndividual c : many_c) {
			if (this.orarOntology.addRoleAssertion(a, T, c)) {
				this.isABoxExtended = true;
				OWLObjectPropertyAssertionAxiom newRoleT_a_c = dataFactory.getOWLObjectPropertyAssertionAxiom(T, a, c);
				this.newRoleAssertions.add(newRoleT_a_c);

			}
		}

		// T(d,a),T(a,b) --> T(d,b)
		Set<OWLNamedIndividual> many_d = this.orarOntology.getPredecessors(a, T);
		for (OWLNamedIndividual d : many_d) {
			if (this.orarOntology.addRoleAssertion(d, T, b)) {
				this.isABoxExtended = true;
				OWLObjectPropertyAssertionAxiom newRoleT_d_b = dataFactory.getOWLObjectPropertyAssertionAxiom(T, d, b);
				this.newRoleAssertions.add(newRoleT_d_b);

			}
		}
	}

	@Override
	public Set<Set<OWLNamedIndividual>> getNewSameasAssertions() {
		// nothing to do with sameas
		return new HashSet<>();
	}

	@Override
	public Set<OWLObjectPropertyAssertionAxiom> getNewRoleAssertions() {

		return this.newRoleAssertions;
	}

	@Override
	public boolean isABoxExtended() {
		return this.isABoxExtended;
	}

	@Override
	public void clearOldBuffer() {
		this.newRoleAssertions.clear();

	}

}
