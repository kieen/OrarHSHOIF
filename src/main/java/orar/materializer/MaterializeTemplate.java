package orar.materializer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import orar.abstraction.AbstractionGenerator;
import orar.abstraction.BasicTypeComputor;
import orar.abstraction.TypeComputor;
import orar.config.Configuration;
import orar.config.DebugLevel;
import orar.data.AbstractDataFactory;
import orar.data.DataForTransferingEntailments;
import orar.data.MetaDataOfOntology;
import orar.innerreasoner.InnerReasoner;
import orar.modeling.ontology.OrarOntology;
import orar.refinement.abstractroleassertion.AbstractRoleAssertionBox;
import orar.refinement.abstractroleassertion.RoleAssertionList;
import orar.refinement.assertiontransferring.AssertionTransporter;
import orar.rolereasoning.HermitRoleReasoner;
import orar.rolereasoning.RoleReasoner;
import orar.ruleengine.RuleEngine;
import orar.ruleengine.SemiNaiveRuleEngine;
import orar.type.IndividualType;
import orar.util.PrintingHelper;

public abstract class MaterializeTemplate implements Materializer {
	// input & output
	protected final OrarOntology normalizedORAROntology;
	private int numberOfRefinements;
	private int reasoningTimeInSeconds;
	protected final Configuration config;
	// logging
	private static final Logger logger = Logger.getLogger(MaterializeTemplate.class);
	// shared data
	protected final DataForTransferingEntailments dataForTransferringEntailments;
	protected final MetaDataOfOntology metaDataOfOntology;
	// fields that vary in implementations
	protected AbstractionGenerator abstractionGenerator;
	// other fields for the algorithm
	protected final TypeComputor typeComputor;
	protected Set<OWLOntology> abstractOntologies;
	protected final RuleEngine ruleEngine;

	public MaterializeTemplate(OrarOntology normalizedOrarOntology) {
		// input & output
		this.normalizedORAROntology = normalizedOrarOntology;
		this.numberOfRefinements = -1;
		this.reasoningTimeInSeconds = -1;
		this.config = Configuration.getInstance();
		// shared data
		this.dataForTransferringEntailments = DataForTransferingEntailments.getInstance();
		this.metaDataOfOntology = MetaDataOfOntology.getInstance();

		// other fields
		this.abstractOntologies = new HashSet<>();
		this.ruleEngine = new SemiNaiveRuleEngine(normalizedOrarOntology);
		this.typeComputor = new BasicTypeComputor();
	}

	@Override
	public void materialize() {
		// TODO: get reasoning time
		/*
		 * (1). Get meta info of the ontology, e.g. role hierarchy, entailed
		 * func/tran roles
		 */
		logger.info("Performing role reasoning ...");
		doRoleReasoning();

		/*
		 * (2). Compute deductive closure of equality, trans, functionality, and
		 * role subsumsion.
		 */
		logger.info("First time computing deductive closure...");
		ruleEngine.materialize();
		/*
		 * Start loop from (3)--()
		 */
		boolean updated = true;
		logger.info("Starting the abstraction refinement loop...");
		int currentLoop = this.numberOfRefinements + 1;
		while (updated) {
			logger.info("Current loop: " + currentLoop);
			this.dataForTransferringEntailments.clear();
			AbstractDataFactory.getInstance().clear();
			/*
			 * (3). Compute types
			 */
			logger.info("Computing types...");
			Map<IndividualType, Set<OWLNamedIndividual>> typeMap2Individuals = this.typeComputor
					.computeTypes(this.normalizedORAROntology);
			logger.info("Number of types:" + typeMap2Individuals.size());

			if (config.getDebuglevels().contains(DebugLevel.TYPE_COMPUTING)) {
				logger.info("***DEBUG*** Types:");
				PrintingHelper.printMap(typeMap2Individuals);
			}
			/*
			 * (4). Generate the abstractions
			 */
			logger.info("Generating abstractions ...");
			List<OWLOntology> abstractions = getAbstractions(typeMap2Individuals);
			if (config.getDebuglevels().contains(DebugLevel.ABSTRACTION_CREATION)) {
				logger.info("***DEBUG*** abstractions:");
				logger.info("Number of abstractions: " + abstractions.size());
				for (OWLOntology abs : abstractions) {
					logger.info("===Abstraction ontology:====");
					PrintingHelper.printSet(abs.getAxioms());
				}
			}
			/*
			 * (5). Materialize abstractions
			 */
			logger.info("Materializing the abstractions ...");
			Map<OWLNamedIndividual, Set<OWLClass>> entailedAbstractConceptAssertions = new HashMap<>();
			AbstractRoleAssertionBox entailedAbstractRoleAssertion = new AbstractRoleAssertionBox();
			Map<OWLNamedIndividual, Set<OWLNamedIndividual>> entailedSameasMap = new HashMap<>();
			for (OWLOntology abstraction : abstractions) {
				if (config.getDebuglevels().contains(DebugLevel.REASONING_ABSTRACTONTOLOGY)) {
					logger.info("***DEBUG REASONING_ABSTRACTONTOLOGY *** for abstract ontology:");
					PrintingHelper.printSet(abstraction.getAxioms());
				}

				InnerReasoner innerReasoner = getInnerReasoner(abstraction);
				innerReasoner.computeEntailments();
				// we can use putAll since individuals in different abstractsion
				// are
				// disjointed.

				entailedAbstractConceptAssertions.putAll(innerReasoner.getEntailedConceptAssertionsAsMap());
				entailedAbstractRoleAssertion.addAll(innerReasoner.getEntailedRoleAssertions());
				if (config.getDebuglevels().contains(DebugLevel.REASONING_ABSTRACTONTOLOGY)) {
					logger.info(
							"***DEBUG REASONING_ABSTRACTONTOLOGY *** entailed role assertions by abstract ontoogy:");
					logger.info("number of assertions:" + innerReasoner.getEntailedRoleAssertions().getSize());
				}
				entailedSameasMap.putAll(innerReasoner.getSameAsMap());
			}
			/*
			 * (6). Transfer assertions to the original ABox
			 */
			logger.info("Transferring the entailments ...");
			AssertionTransporter assertionTransporter = getAssertionTransporter(entailedAbstractConceptAssertions,
					entailedAbstractRoleAssertion, entailedSameasMap);
			assertionTransporter.updateOriginalABox();
			updated = assertionTransporter.isABoxExtended();
			if (updated) {
				this.numberOfRefinements++;
				RoleAssertionList newlyAddedRoleAssertions = assertionTransporter.getNewlyAddedRoleAssertions();
				Set<Set<OWLNamedIndividual>> newlyAddedSameasAssertions = assertionTransporter
						.getNewlyAddedSameasAssertions();
				/*
				 * (7). Compute deductive closure
				 */
				logger.info("Computing deductive closure wrt new entailments ...");
				ruleEngine.addTodoRoleAsesrtions(newlyAddedRoleAssertions.getSetOfRoleAssertions());
				ruleEngine.addTodoSameasAssertions(newlyAddedSameasAssertions);
				ruleEngine.incrementalMaterialize();

			}
			logger.info("Finish loop: " + currentLoop);
			currentLoop = this.numberOfRefinements + 1;
		}
	}

	protected abstract List<OWLOntology> getAbstractions(
			Map<IndividualType, Set<OWLNamedIndividual>> typeMap2Individuals);

	protected abstract AssertionTransporter getAssertionTransporter(
			Map<OWLNamedIndividual, Set<OWLClass>> entailedAbstractConceptAssertions,
			AbstractRoleAssertionBox entailedAbstractRoleAssertion,
			Map<OWLNamedIndividual, Set<OWLNamedIndividual>> entailedSameasMap);

	private void doRoleReasoning() {
		RoleReasoner roleReasoner = new HermitRoleReasoner(this.normalizedORAROntology.getTBoxAxioms());
		roleReasoner.doReasoning();
		this.metaDataOfOntology.getFunctionalRoles().addAll(roleReasoner.getFunctionalRoles());
		this.metaDataOfOntology.getInverseFunctionalRoles().addAll(roleReasoner.getInverseFunctionalRoles());
		this.metaDataOfOntology.getTransitiveRoles().addAll(roleReasoner.getTransitiveRoles());
		// this.metaDataOfOntology.getInverseRoleMap().putAll(roleReasoner.getInverseRoleMap());
		this.metaDataOfOntology.getSubRoleMap().putAll(roleReasoner.getRoleHierarchyAsMap());
	}

	protected abstract InnerReasoner getInnerReasoner(OWLOntology abstraction);

	@Override
	public int getNumberOfRefinements() {

		return this.numberOfRefinements;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public OrarOntology getOrarOntology() {
		return this.normalizedORAROntology;
	}

	@Override
	public int getReasoningTimeInSeconds() {
		return this.reasoningTimeInSeconds;
	}

}
