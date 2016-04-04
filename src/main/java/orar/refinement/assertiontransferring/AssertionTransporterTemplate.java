package orar.refinement.assertiontransferring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import orar.abstraction.PairOfSubjectAndObject;
import orar.config.Configuration;
import orar.config.DebugLevel;
import orar.data.DataForTransferingEntailments;
import orar.modeling.ontology.OrarOntology;
import orar.refinement.abstractroleassertion.AbstractRoleAssertionBox;
import orar.refinement.abstractroleassertion.RoleAssertionList;

public abstract class AssertionTransporterTemplate implements AssertionTransporter {
	// original ontology
	protected final OrarOntology orarOntology;
	// entailments of the abstraction
	protected Map<OWLNamedIndividual, Set<OWLClass>> abstractConceptAssertionsAsMap;
	protected AbstractRoleAssertionBox abstractRoleAssertionBox;
	protected Map<OWLNamedIndividual, Set<OWLNamedIndividual>> abstractSameasMap;
	// flag for abox updating
	protected boolean isABoxExtended;
	// debugging
	private final Configuration config;
	private static final Logger logger = Logger.getLogger(AssertionTransporterTemplate.class);
	// map/data for transferring assertions
	protected final DataForTransferingEntailments dataForTransferingEntailments;

	public AssertionTransporterTemplate(OrarOntology orarOntoloy) {
		this.orarOntology = orarOntoloy;
		// this.abstractConceptAssertionsAsMap = new HashMap<>();
		// this.abstractRoleAssertionBox = new AbstractRoleAssertionBox();
		// this.abstractSameasMap = new HashMap<>();
		this.isABoxExtended = false;
		this.config = Configuration.getInstance();
		this.dataForTransferingEntailments = DataForTransferingEntailments.getInstance();
	}

	@Override
	public void updateOriginalABox() {
		addConceptAssertions();// same for all AssertionTransporter-subclasses
		addRoleAssertions();
		addSameasAssertions();
	}

	protected abstract void addSameasAssertions();

	private void addRoleAssertions() {
		addRoleAssertionsForLoopConcepts(); // same in all
		// AssertionTransporter-subclasses. Well it is slightly different, but we handle it in 
		addRoleAssertionsForConceptsHavingTranRole();
		addRoleAssertionsForSingletonConcept();
		addRoleAssertionsForXYHavingFunctionalRoles();// same in all
														// AssertionTransporter-subclasses
		addRoleAssertionsForZXHavingInverseFunctionalRoles();// same in all
																// AssertionTransporter-subclasses

	}

	/**
	 * add role assertions by the rule R^2_<: M(a), F(a,b) --> H(a,b). Case: F
	 * is atomic.
	 */
	private void addRoleAssertionsForZXHavingInverseFunctionalRoles() {
		RoleAssertionList roleAssertionList = this.abstractRoleAssertionBox.getZxRoleAssertionsForType();
		int size = roleAssertionList.getSize();
		for (int index = 0; index < size; index++) {
			/*
			 * get role(z,x)
			 */
			OWLNamedIndividual zAbstractIndiv = roleAssertionList.getSubject(index);
			OWLNamedIndividual xAbstractIndiv = roleAssertionList.getObject(index);
			OWLObjectProperty roleInEntailedAssertion = roleAssertionList.getRole(index);
			/*
			 * each pair of (z,x) is connected by the ONLY one role in the
			 * abstraction. We use this role and original individuals
			 * corresponding to x to find the correct pair of individuals for
			 * adding role assertions.
			 * 
			 */

			PairOfSubjectAndObject pairOfZX = new PairOfSubjectAndObject(zAbstractIndiv, xAbstractIndiv);
			OWLObjectProperty roleConnectsZandX = this.dataForTransferingEntailments.getMap_ZX_2_Role().get(pairOfZX);
			if (roleConnectsZandX == null)
				continue;
			/*
			 * add role assertions to the original ABox.
			 */
			Set<OWLNamedIndividual> originalIndsCorrespondingToX = this.dataForTransferingEntailments.getOriginalIndividuals(xAbstractIndiv);
			for (OWLNamedIndividual eachOriginalIndiv : originalIndsCorrespondingToX) {
				Set<OWLNamedIndividual> allSubjects = this.orarOntology.getPredecessors(eachOriginalIndiv,
						roleConnectsZandX);
				for (OWLNamedIndividual eachSubject : allSubjects) {
					if (this.orarOntology.addRoleAssertion(eachSubject, roleInEntailedAssertion, eachOriginalIndiv)) {
						this.isABoxExtended = true;
					}
				}
			}
		}

	}

	/**
	 * add role assertions by the rule R^2_<: M(a), F(a,b) --> H(a,b). Case: F
	 * is the inverse of an atomic role.
	 */
	private void addRoleAssertionsForXYHavingFunctionalRoles() {
		RoleAssertionList roleAssertionList = this.abstractRoleAssertionBox.getXyRoleAssertionsForType();
		int size = roleAssertionList.getSize();
		for (int index = 0; index < size; index++) {
			/*
			 * get role(x,y)
			 */
			OWLNamedIndividual xAbstractIndiv = roleAssertionList.getSubject(index);
			OWLObjectProperty roleInEntailedAssertion = roleAssertionList.getRole(index);
			OWLNamedIndividual yAbstractIndiv = roleAssertionList.getObject(index);
			/*
			 * each pair of (x,y) is connected by the ONLY one role in the
			 * abstraction. We use this role and original individuals
			 * corresponding to x to find the correct pair of individuals for
			 * adding role assertions.
			 * 
			 */

			PairOfSubjectAndObject pairOfXY = new PairOfSubjectAndObject(xAbstractIndiv, yAbstractIndiv);
			OWLObjectProperty roleConnectsXandY = this.dataForTransferingEntailments.getMap_XY_2_Role().get(pairOfXY);
			if (roleConnectsXandY == null)
				continue;
			/*
			 * add role assertions
			 */
			Set<OWLNamedIndividual> originalIndsCorrespondingToX = this.dataForTransferingEntailments.getOriginalIndividuals(xAbstractIndiv);
			for (OWLNamedIndividual eachOriginalIndiv : originalIndsCorrespondingToX) {
				Set<OWLNamedIndividual> allObjects = this.orarOntology.getSuccessors(eachOriginalIndiv,
						roleConnectsXandY);
				for (OWLNamedIndividual eachObject : allObjects) {
					if (this.orarOntology.addRoleAssertion(eachOriginalIndiv, roleInEntailedAssertion, eachObject)) {
						this.isABoxExtended = true;
					}
				}
			}
		}

	}

	protected abstract void addRoleAssertionsForSingletonConcept();

	protected abstract void addRoleAssertionsForConceptsHavingTranRole();

	private void addRoleAssertionsForLoopConcepts() {
		RoleAssertionList roleAssertionList = this.abstractRoleAssertionBox.getLoopRoleAssertions();
		int size = roleAssertionList.getSize();
		for (int index = 0; index < size; index++) {
			OWLNamedIndividual xInd = roleAssertionList.getSubject(index);
			OWLObjectProperty role = roleAssertionList.getRole(index);
			// get original individuals
			Set<OWLNamedIndividual> allOriginalInds = this.dataForTransferingEntailments.getOriginalIndividuals(xInd);
			// add assertions to the orignal ABox
			for (OWLNamedIndividual eachOriginalInd : allOriginalInds) {
				this.orarOntology.addRoleAssertion(eachOriginalInd, role, eachOriginalInd);
			}
		}
	}

	/**
	 * add concept assertions based on concept assertions of representatives
	 * (X,Y,Z) for combined-types.
	 */
	private void addConceptAssertions() {
		Iterator<Entry<OWLNamedIndividual, Set<OWLClass>>> iterator = this.abstractConceptAssertionsAsMap.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Entry<OWLNamedIndividual, Set<OWLClass>> entry = iterator.next();
			OWLNamedIndividual abstractInd = entry.getKey();
			Set<OWLClass> concepts = entry.getValue();
			if (concepts != null) {
				Set<OWLNamedIndividual> originalIndividuals = this.dataForTransferingEntailments.getOriginalIndividuals(abstractInd);
				for (OWLNamedIndividual originalInd : originalIndividuals) {

					// /*
					// * Debug
					// */
					// if (originalInd.equals(bug_individual)){
					// logger.info("***something wrong might come from here
					// ***");
					// logger.info("individual:"+bug_individual);
					// logger.info("Concepts:"+concepts);
					// logger.info("Reason:");
					// logger.info("By assertion from bstract
					// individual:"+abstractInd);
					// //if (sharedData.get)
					//
					// }
					if (config.getDebuglevels().contains(DebugLevel.UPDATING_CONCEPT_ASSERTION)) {
						logger.info("*** Update concept assertions in the original ABox ***");
						logger.info("Individual:" + originalInd);
						logger.info("has new concepts:" + concepts);
						logger.info("Reason: get from concept assertion of the abstract individual:" + abstractInd);
						logger.info("*=====================================================*");
					}
					/*
					 * end of debug
					 */

					if (this.orarOntology.addManyConceptAssertions(originalInd, concepts)) {
						this.isABoxExtended = true;
					}
				}
			}
		}

	}

	
	@Override
	public boolean isABoxExtended() {

		return this.isABoxExtended;
	}

}
