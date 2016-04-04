package orar.innerreasoner.HornSHOIF;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import orar.innerreasoner.InnerReasonerTemplate;

public abstract class HornSHOIF_InnerReasonerTemplate extends InnerReasonerTemplate {

	public HornSHOIF_InnerReasonerTemplate(OWLOntology owlOntology) {
		super(owlOntology);
	}

	/**
	 * compute assertions by R_t^2, (N(u)-->T(u,u))
	 */
	@Override
	protected void computeRoleAssertionForInstancesOfLoopConcepts() {
		Set<OWLNamedIndividual> individuals = new HashSet<>(this.instancesOfLoopConcepts);
		/*
		 * retain only U-individuals.
		 */
		individuals.retainAll(this.abstractDataFactory.getUAbstractIndividuals());
		for (OWLNamedIndividual eachU : individuals) {
			for (OWLObjectProperty tranRol : this.metadataOfOntology.getTransitiveRoles()) {
				Set<OWLNamedIndividual> objects = reasoner.getObjectPropertyValues(eachU, tranRol).getFlattened();
				if (objects.contains(eachU)) {
					this.roleAssertionList.addUU_LoopRoleAssertionForCType(eachU, tranRol);
				}
			}
		}
	}

	/**
	 * compute assertions by R^3_t: N(u), M(x) -->T(u,x)
	 */
	@Override
	protected void computeRoleAssertionForInstancesOfConceptHasTranRole() {
		Set<OWLNamedIndividual> individuals = new HashSet<>(this.instancesOfHasTranConcepts);
		/*
		 * retain only U-individuals.
		 */
		individuals.retainAll(this.abstractDataFactory.getUAbstractIndividuals());

		for (OWLNamedIndividual eachU : individuals) {
			for (OWLObjectProperty tranRole : this.metadataOfOntology.getTransitiveRoles()) {
				/*
				 * query for tranRole
				 */
				Set<OWLNamedIndividual> objects = reasoner.getObjectPropertyValues(eachU, tranRole).getFlattened();
				/*
				 * retain only x-individuals
				 */
				objects.retainAll(this.abstractDataFactory.getXAbstractIndividuals());

				for (OWLNamedIndividual eachObject : objects) {
					this.roleAssertionList.addUX_RoleAssertionForCTypeAndType(eachU, tranRole, eachObject);
				}

				/*
				 * query for inverse of tranRole
				 */
				OWLObjectInverseOf invTranRole = this.dataFactory.getOWLObjectInverseOf(tranRole);
				Set<OWLNamedIndividual> subjects = reasoner.getObjectPropertyValues(eachU, invTranRole).getFlattened();
				/*
				 * retain only x-individuals
				 */
				subjects.retainAll(this.abstractDataFactory.getXAbstractIndividuals());

				for (OWLNamedIndividual eachSubject : subjects) {
					this.roleAssertionList.addUX_RoleAssertionForCTypeAndType(eachSubject, tranRole, eachU);
				}

			}
		}
	}

	@Override
	protected void computeRoleAssertionForInstancesOfSingletonConcept() {
		Set<OWLNamedIndividual> individuals = new HashSet<>(this.instancesOfSingletonConcepts);
		/*
		 * retain only U-individuals.
		 */
		individuals.retainAll(this.abstractDataFactory.getUAbstractIndividuals());
		for (OWLNamedIndividual eachU : individuals) {
			for (OWLObjectProperty role : this.owlOntology.getObjectPropertiesInSignature(true)) {
				/*
				 * query for assertion of the form role(eachU, ?x)
				 */
				Set<OWLNamedIndividual> objects = reasoner.getObjectPropertyValues(eachU, role).getFlattened();
				/*
				 * retain only x-individuals
				 */
				objects.retainAll(this.abstractDataFactory.getXAbstractIndividuals());
				for (OWLNamedIndividual eachObject : objects) {
					this.roleAssertionList.addUX_RoleAssertionForCTypeAndType(eachU, role, eachObject);
				}

				/*
				 * query for assertion of the form role(x, eachU)
				 */
				OWLObjectInverseOf inverseRole = this.dataFactory.getOWLObjectInverseOf(role);
				Set<OWLNamedIndividual> subjects = reasoner.getObjectPropertyValues(eachU, inverseRole).getFlattened();
				/*
				 * retain only x-individuals
				 */
				subjects.retainAll(this.abstractDataFactory.getXAbstractIndividuals());
				for (OWLNamedIndividual eachSubject : subjects) {
					this.roleAssertionList.addUX_RoleAssertionForCTypeAndType(eachSubject, role, eachU);
				}
			}
		}
	}

	/**
	 * compute sameas assertions between representative of concept-types and
	 * those of types.
	 */
	@Override
	protected void computeEntailedSameasAssertions() {
		Set<OWLNamedIndividual> allIndividualsFromConceptType = this.abstractDataFactory.getUAbstractIndividuals();
		for (OWLNamedIndividual indiv : allIndividualsFromConceptType) {
			Set<OWLNamedIndividual> equivalentIndividuals = reasoner.getSameIndividuals(indiv).getEntities();
			/*
			 * Note to remove the indv itself as we DONT use (u=u) to transfer
			 * assertions.
			 */
			equivalentIndividuals.remove(indiv);
			if (!equivalentIndividuals.isEmpty()) {
				this.sameAsMap.put(indiv, equivalentIndividuals);
			}
		}

	}

}
