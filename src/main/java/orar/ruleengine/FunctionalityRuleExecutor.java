package orar.ruleengine;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import orar.data.MetaDataOfOntology;
import orar.modeling.ontology.OrarOntology;

public class FunctionalityRuleExecutor implements RuleExecutor {

	private final Set<Set<OWLNamedIndividual>> newSameasAssertions;
	private final OrarOntology orarOntology;
	private final MetaDataOfOntology metaDataOfOntology;
	private final Logger logger = Logger.getLogger(FunctionalityRuleExecutor.class);

	public FunctionalityRuleExecutor(OrarOntology orarOntology) {
		this.orarOntology = orarOntology;
		this.newSameasAssertions = new HashSet<Set<OWLNamedIndividual>>();
		this.metaDataOfOntology = MetaDataOfOntology.getInstance();

	}

	@Override
	public void materialize() {
		// for functional roles
		mergeSuccessorsOfFunctionalRole();
		mergePredecessorsOfInvFunctionalRole();

	}

	private void mergePredecessorsOfInvFunctionalRole() {
		// for inverse functional roles
		Set<OWLObjectProperty> allInvFuncRoles = this.metaDataOfOntology.getInverseFunctionalRoles();
//		logger.info("********DEBUG**** allInvFuncRoles: "+allInvFuncRoles);
		for (OWLObjectProperty eachInvFuncRole : allInvFuncRoles) {
			Set<OWLNamedIndividual> allObjects = this.orarOntology.getObjectsInRoleAssertions(eachInvFuncRole);
//			logger.info("********DEBUG**** allObjects: "+allObjects);
			for (OWLNamedIndividual eachObject : allObjects) {
				Set<OWLNamedIndividual> allSubjects = this.orarOntology
						.getPredecessorsTakingEqualityIntoAccount(eachObject, eachInvFuncRole);
//				logger.info("********DEBUG**** allSubjects: "+allSubjects);
				if (allSubjects.size() > 1) {
					this.newSameasAssertions.add(allSubjects);
				}
			}
		}

	}

	private void mergeSuccessorsOfFunctionalRole() {
		Set<OWLObjectProperty> allFuncRoles = this.metaDataOfOntology.getFunctionalRoles();
		for (OWLObjectProperty eachFuncRole : allFuncRoles) {
//			logger.info("functional role: " + eachFuncRole);
			Set<OWLNamedIndividual> allSubjects = this.orarOntology.getSubjectsInRoleAssertions(eachFuncRole);
			for (OWLNamedIndividual eachSubject : allSubjects) {
//				logger.info("each Subject: " + eachSubject);
				Set<OWLNamedIndividual> allObjects = this.orarOntology
						.getSuccessorsTakingEqualityIntoAccount(eachSubject, eachFuncRole);
//				logger.info("all objects:" + allObjects);
				if (allObjects.size() > 1) {
					this.newSameasAssertions.add(allObjects);
				}
			}
		}

	}

	@Override
	public void incrementalMaterialize(Set<OWLNamedIndividual> setOfSameasIndividuals) {
//		logger.info("FunctionalityRuleExecutor.incrementalMaterialize is called");
		mergeSuccessorsForFunctionalRoles(setOfSameasIndividuals);
		mergePredecessorsForFunctionalRoles(setOfSameasIndividuals);
	}

	private void mergePredecessorsForFunctionalRoles(Set<OWLNamedIndividual> setOfSameasIndividuals) {
		Set<OWLObjectProperty> allInvFuncRoles = this.metaDataOfOntology.getInverseFunctionalRoles();
		for (OWLObjectProperty eachInvFuncRole : allInvFuncRoles) {
			Set<OWLNamedIndividual> allSubjects = new HashSet<OWLNamedIndividual>();
			for (OWLNamedIndividual eachIndividual : setOfSameasIndividuals) {
				Set<OWLNamedIndividual> subjects = this.orarOntology.getPredecessors(eachIndividual, eachInvFuncRole);
				allSubjects.addAll(subjects);
			}
			if (allSubjects.size() > 1) {
				this.newSameasAssertions.add(allSubjects);
			}
		}

	}

	private void mergeSuccessorsForFunctionalRoles(Set<OWLNamedIndividual> setOfSameasIndividuals) {
		Set<OWLObjectProperty> allFuncRoles = this.metaDataOfOntology.getFunctionalRoles();
		for (OWLObjectProperty eachFuncRole : allFuncRoles) {
			Set<OWLNamedIndividual> allObjects = new HashSet<OWLNamedIndividual>();
			for (OWLNamedIndividual eachIndividual : setOfSameasIndividuals) {
				Set<OWLNamedIndividual> objects = this.orarOntology
						.getSuccessorsTakingEqualityIntoAccount(eachIndividual, eachFuncRole);
				allObjects.addAll(objects);
			}
			if (allObjects.size() > 1) {
				this.newSameasAssertions.add(allObjects);

			}
		}
	}

	@Override
	public void incrementalMaterialize(OWLObjectPropertyAssertionAxiom roleAssertion) {
		OWLObjectProperty role = roleAssertion.getProperty().asOWLObjectProperty();
		OWLNamedIndividual subject = roleAssertion.getSubject().asOWLNamedIndividual();
		OWLNamedIndividual object = roleAssertion.getObject().asOWLNamedIndividual();
		if (this.metaDataOfOntology.getFunctionalRoles().contains(role)) {
			mergeSuccessorsForFunctaionalRole(role, subject, object);
		}

	}

	private void mergeSuccessorsForFunctaionalRole(OWLObjectProperty role, OWLNamedIndividual subject,
			OWLNamedIndividual object) {
		Set<OWLNamedIndividual> allObjects = this.orarOntology.getSuccessorsTakingEqualityIntoAccount(subject, role);
		allObjects.add(object);
		if (allObjects.size() > 1) {
			this.newSameasAssertions.add(allObjects);
		}

	}

	@Override
	public Set<Set<OWLNamedIndividual>> getNewSameasAssertions() {

		return this.newSameasAssertions;
	}

	@Override
	public Set<OWLObjectPropertyAssertionAxiom> getNewRoleAssertions() {
		// return nothings
		return (new HashSet<OWLObjectPropertyAssertionAxiom>());
	}

	@Override
	public boolean isABoxExtended() {
		// we don't check it here. So we return false.
		return false;
	}

	@Override
	public void clearOldBuffer() {
		this.newSameasAssertions.clear();
	}

}
