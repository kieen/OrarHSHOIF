package orar.ruleengine;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import orar.data.MetaDataOfOntology;
import orar.modeling.ontology.OrarOntology;

public class SubRoleRuleExecutor implements RuleExecutor {
	private final Set<OWLObjectPropertyAssertionAxiom> newRoleAssertions;
	private final OrarOntology orarOntology;
	private final MetaDataOfOntology metaDataOfOntology;
	private final OWLDataFactory dataFactory;
	private boolean isABoxExtended;

	public SubRoleRuleExecutor(OrarOntology orarOntology) {
		this.orarOntology = orarOntology;
		this.newRoleAssertions = new HashSet<>();
		this.metaDataOfOntology = MetaDataOfOntology.getInstance();
		this.dataFactory = OWLManager.getOWLDataFactory();
		this.isABoxExtended = false;
	}

	@Override
	public void materialize() {
		Set<OWLObjectProperty> allRolesHavingSuperRoles = this.metaDataOfOntology.getSubRoleMap().keySet();
		for (OWLObjectProperty R : allRolesHavingSuperRoles) {
			Set<OWLNamedIndividual> allSubjectsOf_R = this.orarOntology.getSubjectsInRoleAssertions(R);
			for (OWLNamedIndividual eachSubjectOf_R : allSubjectsOf_R) {
				Set<OWLNamedIndividual> allObjectsOf_R = this.orarOntology.getSuccessors(eachSubjectOf_R, R);
				Set<? extends OWLObjectPropertyExpression> allSuperRolesOf_R = this.metaDataOfOntology.getSubRoleMap()
						.get(R);
				for (OWLObjectPropertyExpression eachSuperRoleOf_R : allSuperRolesOf_R) {
					// case of atomic role
					if (eachSuperRoleOf_R instanceof OWLObjectProperty) {
						OWLObjectProperty eacAtomicSuperRoleOf_R = eachSuperRoleOf_R.asOWLObjectProperty();
						for (OWLNamedIndividual eachObjectOf_R : allObjectsOf_R) {
							addRoleAssertion(eachSubjectOf_R, eacAtomicSuperRoleOf_R, eachObjectOf_R);
						}
					}
					// case of inverse role
					if (eachSuperRoleOf_R instanceof OWLObjectInverseOf) {
						OWLObjectProperty inverseOfEacSuperRoleOf_R = eachSuperRoleOf_R.getNamedProperty();
						for (OWLNamedIndividual eachObjectOf_R : allObjectsOf_R) {
							addRoleAssertion(eachObjectOf_R, inverseOfEacSuperRoleOf_R, eachSubjectOf_R);
						}
					}

				}
			}
		}

	}

	private void addRoleAssertion(OWLNamedIndividual eachSubjectOf_R, OWLObjectProperty eacAtomicSuperRoleOf_R,
			OWLNamedIndividual eachObjectOf_R) {
		if (this.orarOntology.addRoleAssertion(eachSubjectOf_R, eacAtomicSuperRoleOf_R, eachObjectOf_R)) {
			this.isABoxExtended = true;
			if (isTranOrCountingOrInverseRole(eacAtomicSuperRoleOf_R)) {
				OWLObjectPropertyAssertionAxiom newRoleAssertion = this.dataFactory
						.getOWLObjectPropertyAssertionAxiom(eacAtomicSuperRoleOf_R, eachSubjectOf_R, eachObjectOf_R);
				this.newRoleAssertions.add(newRoleAssertion);
			}
		}

	}

	private boolean isTranOrCountingOrInverseRole(OWLObjectProperty role) {
		return (this.metaDataOfOntology.getTransitiveRoles().contains(role)
				|| this.metaDataOfOntology.getFunctionalRoles().contains(role)
				|| this.metaDataOfOntology.getInverseFunctionalRoles().contains(role)
				|| this.metaDataOfOntology.getInverseRoleMap().keySet().contains(role));

	}

	@Override
	public void incrementalMaterialize(Set<OWLNamedIndividual> setOfSameasIndividuals) {
		// nothing to do with sameas
	}

	@Override
	public void incrementalMaterialize(OWLObjectPropertyAssertionAxiom roleAssertion) {
		OWLObjectProperty role = roleAssertion.getProperty().asOWLObjectProperty();
		OWLNamedIndividual subject = roleAssertion.getSubject().asOWLNamedIndividual();
		OWLNamedIndividual object = roleAssertion.getObject().asOWLNamedIndividual();
		Set<? extends OWLObjectPropertyExpression> allSuperRoles = this.metaDataOfOntology.getSubRoleMap().get(role);
		if (allSuperRoles != null) {
			for (OWLObjectPropertyExpression eachSuperRole : allSuperRoles) {
				if (eachSuperRole instanceof OWLObjectProperty) {
					addRoleAssertion(subject, eachSuperRole.asOWLObjectProperty(), object);
				}

				if (eachSuperRole instanceof OWLObjectInverseOf) {
					addRoleAssertion(object, eachSuperRole.getNamedProperty(), subject);
				}
			}
		}
	}

	@Override
	public Set<Set<OWLNamedIndividual>> getNewSameasAssertions() {
		// return empty set
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
