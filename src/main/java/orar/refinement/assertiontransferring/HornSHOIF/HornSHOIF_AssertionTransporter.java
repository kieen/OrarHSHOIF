package orar.refinement.assertiontransferring.HornSHOIF;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import orar.config.DebugLevel;
import orar.modeling.ontology.OrarOntology;
import orar.refinement.abstractroleassertion.AbstractRoleAssertionBox;
import orar.refinement.abstractroleassertion.RoleAssertionList;
import orar.refinement.assertiontransferring.AssertionTransporterTemplate;
import orar.util.PrintingHelper;

public class HornSHOIF_AssertionTransporter extends AssertionTransporterTemplate {
	private Logger logger = Logger.getLogger(HornSHOIF_AssertionTransporter.class);

	public HornSHOIF_AssertionTransporter(OrarOntology orarOntoloy,
			Map<OWLNamedIndividual, Set<OWLClass>> abstractConceptAssertionsAsMap,
			AbstractRoleAssertionBox abstractRoleAssertionBox,
			Map<OWLNamedIndividual, Set<OWLNamedIndividual>> abstractSameasMap) {
		super(orarOntoloy);
		this.abstractConceptAssertionsAsMap = abstractConceptAssertionsAsMap;
		this.abstractRoleAssertionBox = abstractRoleAssertionBox;
		this.abstractSameasMap = abstractSameasMap;
	}

	@Override
	protected void transferSameasAssertions() {
		Iterator<Entry<OWLNamedIndividual, Set<OWLNamedIndividual>>> iterator = this.abstractSameasMap.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Entry<OWLNamedIndividual, Set<OWLNamedIndividual>> entry = iterator.next();
			// compute a set of equivalent individuals in the original ABox
			Set<OWLNamedIndividual> equivalentOriginalInds = new HashSet<OWLNamedIndividual>();
			// for the key
			OWLNamedIndividual u = entry.getKey();
			Set<OWLNamedIndividual> originalInds_of_u = this.dataForTransferingEntailments.getOriginalIndividuals(u);
			equivalentOriginalInds.addAll(originalInds_of_u);
			// for the equivalent individuals of the key.
			Set<OWLNamedIndividual> equivalentAbstractInds_of_u = entry.getValue();
			for (OWLNamedIndividual eachAbstractInd : equivalentAbstractInds_of_u) {
				equivalentOriginalInds
						.addAll(this.dataForTransferingEntailments.getOriginalIndividuals(eachAbstractInd));
			}

			if (equivalentOriginalInds.size() > 1) {
				if (this.orarOntology.addSameasAssertion(equivalentOriginalInds)) {
					this.isABoxExtended = true;
					if (this.config.getDebuglevels().contains(DebugLevel.TRANSFER_SAMEAS)) {
						logger.info("***DEBUG***TRANSFER_SAMEAS:");
						PrintingHelper.printSet(equivalentOriginalInds);
						logger.info("updated=true");
					}
					this.newSameasAssertions.add(equivalentOriginalInds);
				}
			}

		}

	}

	@Override
	protected void tranferRoleAssertionsBetweenUX() {
		transferRoleAssertionBetweenUAndXAbstract();
		transferRoleAssertionBetweenNominalAndU();
		transferRoleAssertionBetweenUAndNominals();
	}

	private void transferRoleAssertionBetweenUAndXAbstract() {
		RoleAssertionList roleAssertionList = this.abstractRoleAssertionBox.getUxRoleAssertionsForCTypeAndType();
		int size = roleAssertionList.getSize();
		for (int index = 0; index < size; index++) {
			OWLNamedIndividual abstractSubject = roleAssertionList.getSubject(index);
			OWLObjectProperty role = roleAssertionList.getRole(index);
			OWLNamedIndividual abstractObject = roleAssertionList.getObject(index);

			Set<OWLNamedIndividual> originalSubjects = this.dataForTransferingEntailments
					.getOriginalIndividuals(abstractSubject);
			Set<OWLNamedIndividual> originalObjects = this.dataForTransferingEntailments
					.getOriginalIndividuals(abstractObject);

			for (OWLNamedIndividual originalSubject : originalSubjects) {
				for (OWLNamedIndividual originalObject : originalObjects) {
					if (this.orarOntology.addRoleAssertion(originalSubject, role, originalObject)) {
						this.isABoxExtended = true;
						this.newRoleAssertions.addRoleAssertion(originalSubject, role, originalObject);

						if (this.config.getDebuglevels().contains(DebugLevel.TRANSFER_ROLEASSERTION)) {
							logger.info("***DEBUG***TRANSFER_ROLEASSERTION:");
							logger.info(originalSubject + ", " + role + ", " + originalObject);
							logger.info("updated=true");
						}
					}
				}

			}
		}
	}

	private void transferRoleAssertionBetweenUAndNominals() {
		RoleAssertionList roleAssertionList = this.abstractRoleAssertionBox.get_UandNominal_RoleAssertions();
		int size = roleAssertionList.getSize();
		for (int index = 0; index < size; index++) {
			OWLNamedIndividual abstractSubject = roleAssertionList.getSubject(index);
			OWLObjectProperty role = roleAssertionList.getRole(index);
			OWLNamedIndividual nominal = roleAssertionList.getObject(index);

			Set<OWLNamedIndividual> originalSubjects = this.dataForTransferingEntailments
					.getOriginalIndividuals(abstractSubject);

			for (OWLNamedIndividual originalSubject : originalSubjects) {

				if (this.orarOntology.addRoleAssertion(originalSubject, role, nominal)) {
					this.isABoxExtended = true;
					this.newRoleAssertions.addRoleAssertion(originalSubject, role, nominal);
					
					if (this.config.getDebuglevels().contains(DebugLevel.TRANSFER_ROLEASSERTION)) {
						logger.info("***DEBUG***TRANSFER_ROLEASSERTION:");
						logger.info(originalSubject + ", " + role + ", " + nominal);
						logger.info("updated=true");
					}
				}

			}
		}
	}

	private void transferRoleAssertionBetweenNominalAndU() {
		RoleAssertionList roleAssertionList = this.abstractRoleAssertionBox.get_NominalAndU_RoleAssertions();
		int size = roleAssertionList.getSize();
		for (int index = 0; index < size; index++) {
			OWLNamedIndividual nominal = roleAssertionList.getSubject(index);
			OWLObjectProperty role = roleAssertionList.getRole(index);
			OWLNamedIndividual abstractObject = roleAssertionList.getObject(index);

			Set<OWLNamedIndividual> originalObjects = this.dataForTransferingEntailments
					.getOriginalIndividuals(abstractObject);

			for (OWLNamedIndividual eachOriginalObject : originalObjects) {

				if (this.orarOntology.addRoleAssertion(nominal, role, eachOriginalObject)) {
					this.isABoxExtended = true;
					this.newRoleAssertions.addRoleAssertion(nominal, role, eachOriginalObject);
					
					if (this.config.getDebuglevels().contains(DebugLevel.TRANSFER_ROLEASSERTION)) {
						logger.info("***DEBUG***TRANSFER_ROLEASSERTION:");
						logger.info(nominal + ", " + role + ", " + eachOriginalObject);
						logger.info("updated=true");
					}
				}

			}
		}
	}
}
