package orar.ruleengine;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import orar.modeling.ontology.OrarOntology;
import orar.rolereasoning.AxiomOfSpecificTypeGetter;
import orar.util.Pause;

public class InverseRoleRuleExecutor implements RuleExecutor {
	private static final Logger logger = Logger.getLogger(InverseRoleRuleExecutor.class);
	private final Set<OWLObjectPropertyAssertionAxiom> newRoleAssertions;
	private final OrarOntology orarOntology;
	private final OWLDataFactory dataFactory;
	private boolean isABoxExtended;
	/**
	 * map: role --> its inverses
	 */
	private Map<OWLObjectProperty, Set<OWLObjectPropertyExpression>> inverseRoleMap;

	public InverseRoleRuleExecutor(OrarOntology orarOntology) {
		this.orarOntology = orarOntology;
		this.newRoleAssertions = new HashSet<OWLObjectPropertyAssertionAxiom>();
		this.dataFactory = OWLManager.getOWLDataFactory();
		this.isABoxExtended = false;
		this.inverseRoleMap = new HashMap<OWLObjectProperty, Set<OWLObjectPropertyExpression>>();
		/*
		 * Create a map from each atomic role to its inverses
		 */
		createMapFromRoleToItsInverses();
	}

	@Override
	public void materialize() {
		
		/*
		 * For each role R and each inverese role S of R: R(a,b) ---> add
		 * S(b,a). Be careful as S can also be an inverse of an atomic role.
		 */

		Set<OWLObjectProperty> allKeys = this.inverseRoleMap.keySet();
		for (OWLObjectProperty R : allKeys) {
			Set<OWLNamedIndividual> allSubjectsOf_R = this.orarOntology.getSubjectsInRoleAssertions(R);
			for (OWLNamedIndividual eachSubjectOf_R : allSubjectsOf_R) {
				Set<OWLNamedIndividual> allObjectsOf_R = this.orarOntology.getSuccessors(eachSubjectOf_R, R);
				Set<OWLObjectPropertyExpression> allInverseOf_R = this.inverseRoleMap.get(R);

				for (OWLObjectPropertyExpression inverseOf_R : allInverseOf_R) {
					// case of atomic role
					if (inverseOf_R instanceof OWLObjectProperty) {
						OWLObjectProperty atomicInverseOf_R = inverseOf_R.asOWLObjectProperty();
						for (OWLNamedIndividual eachObjectOf_R : allObjectsOf_R) {
							addRoleAssertion(eachObjectOf_R, atomicInverseOf_R, eachSubjectOf_R);
						}
					}
					// case of inverse role
					if (inverseOf_R instanceof OWLObjectInverseOf) {
						OWLObjectProperty inverseOfEacSuperRoleOf_R = inverseOf_R.getNamedProperty();
						for (OWLNamedIndividual eachObjectOf_R : allObjectsOf_R) {
							addRoleAssertion(eachSubjectOf_R, inverseOfEacSuperRoleOf_R, eachObjectOf_R);
						}
					}

				}
			}
		}
	}

	private void addRoleAssertion(OWLNamedIndividual subject, OWLObjectProperty eacAtomicSuperRoleOf_R,
			OWLNamedIndividual eachObjectOf_R) {
		if (this.orarOntology.addRoleAssertion(subject, eacAtomicSuperRoleOf_R, eachObjectOf_R)) {
			this.isABoxExtended = true;

			OWLObjectPropertyAssertionAxiom newRoleAssertion = this.dataFactory
					.getOWLObjectPropertyAssertionAxiom(eacAtomicSuperRoleOf_R, subject, eachObjectOf_R);
			this.newRoleAssertions.add(newRoleAssertion);

		}

	}

	private void createMapFromRoleToItsInverses() {
		Set<OWLInverseObjectPropertiesAxiom> allInverseRoleAxioms = AxiomOfSpecificTypeGetter
				.getInverseObjectPropertyAxioms(orarOntology);
		
//		logger.info("***DEBUG*** allInverseRoleAxioms:"+allInverseRoleAxioms);
//		Pause.pause();
		
		for (OWLInverseObjectPropertiesAxiom invroleAxiom : allInverseRoleAxioms) {
			OWLObjectPropertyExpression left = invroleAxiom.getFirstProperty().getSimplified();
			OWLObjectPropertyExpression right = invroleAxiom.getSecondProperty().getSimplified();

			// add entry for left
			if (left instanceof OWLObjectProperty) {
				addEntryToIverseRoleMap(left.asOWLObjectProperty(), right);
			} else if (left instanceof OWLObjectInverseOf) {
				addEntryToIverseRoleMap(left.getNamedProperty(), dataFactory.getOWLObjectInverseOf(right));
			}

			// add entry for right
			if (right instanceof OWLObjectProperty) {
				addEntryToIverseRoleMap(right.asOWLObjectProperty(), left);
			} else if (right instanceof OWLObjectInverseOf) {
				addEntryToIverseRoleMap(right.getNamedProperty(), dataFactory.getOWLObjectInverseOf(left));
			}

		}
	}

	/**
	 * add value to the existing value of the key.
	 * 
	 * @param key
	 * @param value
	 */
	private void addEntryToIverseRoleMap(OWLObjectProperty key, OWLObjectPropertyExpression value) {
		Set<OWLObjectPropertyExpression> existingValues = this.inverseRoleMap.get(key);
		if (existingValues == null) {
			existingValues = new HashSet<OWLObjectPropertyExpression>();
		}
		existingValues.add(value);
		this.inverseRoleMap.put(key, existingValues);
	}

	@Override
	public void incrementalMaterialize(Set<OWLNamedIndividual> setOfSameasIndividuals) {
		// nothing to do

	}

	@Override
	public void incrementalMaterialize(OWLObjectPropertyAssertionAxiom roleAssertion) {
		OWLObjectProperty role = roleAssertion.getProperty().asOWLObjectProperty();
		OWLNamedIndividual subject = roleAssertion.getSubject().asOWLNamedIndividual();
		OWLNamedIndividual object = roleAssertion.getObject().asOWLNamedIndividual();

		Set<? extends OWLObjectPropertyExpression> allInverseRoles = this.inverseRoleMap.get(role);
		if (allInverseRoles != null) {
			for (OWLObjectPropertyExpression eachInverseRole : allInverseRoles) {
				if (eachInverseRole instanceof OWLObjectProperty) {
					addRoleAssertion(object, eachInverseRole.asOWLObjectProperty(), subject);
				}

				if (eachInverseRole instanceof OWLObjectInverseOf) {

					addRoleAssertion(subject, eachInverseRole.getNamedProperty(), object);
				}
			}
		}

	}

	@Override
	public Set<Set<OWLNamedIndividual>> getNewSameasAssertions() {
		// return empty set
		return new HashSet<Set<OWLNamedIndividual>>();
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
