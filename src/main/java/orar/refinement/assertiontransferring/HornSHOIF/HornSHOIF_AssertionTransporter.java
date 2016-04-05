package orar.refinement.assertiontransferring.HornSHOIF;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import orar.modeling.ontology.OrarOntology;
import orar.refinement.abstractroleassertion.AbstractRoleAssertionBox;
import orar.refinement.abstractroleassertion.RoleAssertionList;
import orar.refinement.assertiontransferring.AssertionTransporterTemplate;

public class HornSHOIF_AssertionTransporter extends AssertionTransporterTemplate {

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
	protected void addSameasAssertions() {
		Iterator<Entry<OWLNamedIndividual, Set<OWLNamedIndividual>>> iterator = this.abstractSameasMap.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Entry<OWLNamedIndividual, Set<OWLNamedIndividual>> entry = iterator.next();
			// compute a set of equivalent individuals in the original ABox
			Set<OWLNamedIndividual> equivalentOriginalInds = new HashSet<>();
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

			// add sameas assertions to the original Abox.
			boolean newAssertionsAdded = false;
			for (OWLNamedIndividual eachOriginalInd : equivalentOriginalInds) {
				if (this.orarOntology.addManySameAsAssertions(eachOriginalInd, equivalentOriginalInds)) {
					newAssertionsAdded = true;
				}
			}
			if (newAssertionsAdded) {
				this.isABoxExtended = true;
				this.newSameasAssertions.add(equivalentOriginalInds);
			}
		}

	}

	@Override
	protected void tranferRoleAssertionsBetweenUX() {
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
					}
				}

			}
		}

	}

	
	
}
