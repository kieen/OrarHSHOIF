package orar.io.ontologyreader;

import java.io.File;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import orar.config.Configuration;
import orar.config.DebugLevel;
import orar.config.LogInfo;
import orar.dlfragmentvalidator.OWLOntologyValidator;
import orar.modeling.ontology.OrarOntology;
import orar.normalization.Normalizer;
import orar.normalization.transitivity.TransitivityNormalizer;
import orar.normalization.transitivity.TransitivityNormalizerWithHermit;
import orar.util.OntologyInfo;

public abstract class OntologyReaderTemplate implements OntologyReader {
	protected Normalizer normalizer;
	protected OWLOntologyValidator profileValidator;
	private Logger logger = Logger.getLogger(OntologyReaderTemplate.class);
	private Configuration config = Configuration.getInstance();
	private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

	protected abstract OWLOntologyValidator getOntologyValidator(OWLOntology owlOntology);

	protected abstract Normalizer getNormalizer(OWLOntology owlOntology);

	@Override
	public OrarOntology getNormalizedOrarOntology(String ontologyFileName) {

		long startParsing = System.currentTimeMillis();
		/*
		 * Get a normalized OWLAPI ontology
		 */
		OWLOntology normalizedOWLAPIOntology = getNormalizedOWLAPIOntology(ontologyFileName);

		/*
		 * Convert to AromaOntology
		 */
		OntologyConverter converter = new OntologyConverter(normalizedOWLAPIOntology);

		OrarOntology internalOntology = converter.getInternalOntology();
		internalOntology.setActualDLConstructors(profileValidator.getDLConstructors());

		if (config.getLogInfos().contains(LogInfo.INPUTONTOLOGY_INFO)) {
			logger.info("Information of the input ontology.");
			logger.info("Ontology file:" + ontologyFileName);

			logger.info("Number of individuals:" + internalOntology.getIndividualsInSignature().size());
			long numberOfCA = internalOntology.getNumberOfInputConceptAssertions();
			logger.info("Number of concept assertions:" + numberOfCA);
			long numberOfRA = internalOntology.getNumberOfInputRoleAssertions();
			logger.info("Number of role assertions:" + numberOfRA);
			long totalOfAssertions = numberOfCA + numberOfRA;
			logger.info("Number of concept assertions + role asesrtions:" + totalOfAssertions);
		}

		long endParsing = System.currentTimeMillis();
		long parsingTimeInSecond = (endParsing - startParsing) / 1000;

		if (config.getLogInfos().contains(LogInfo.PARSING_TIME)) {
			logger.info("Time (in second) for loading ontology: " + parsingTimeInSecond);
		}
		return internalOntology;

	}

	/**
	 * Read a file containing an OWLAPI ontology (could be both TBox and ABox);
	 * normalize it and return the normalized one.
	 * 
	 * @param fileNameToOWLAPIOntology
	 * @return the normalized OWLAPI ontology
	 */
	private OWLOntology getNormalizedOWLAPIOntology(String fileNameToOWLAPIOntology) {
		try {
			/*
			 * Read the tboxFile
			 */
			OWLOntology inputOntology = getInputOWLAPIOntology(fileNameToOWLAPIOntology);

			/*
			 * Get ontology in target DL fragment
			 */

			OWLOntology ontologyInDesiredDLFragment = getOntologyInTargetDLFragment(inputOntology);

			/*
			 * Normalize the ontology into normal form
			 */

			OWLOntology ontologyInNormalForm = getOntologyInTheNormalForm(ontologyInDesiredDLFragment);

			/*
			 * adding auxiliary axioms w.r.t transitivity
			 */

			OWLOntology ontologyInNormalFormWithAddedAuxiliaryAxiomsForTransitivity = getOntologyWithAuxiliaryAxiomsForTransitivity(
					ontologyInNormalForm);

			return ontologyInNormalFormWithAddedAuxiliaryAxiomsForTransitivity;
		} catch (OWLOntologyCreationException e) {

			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param owlOntologyFileName
	 * @return an OWLAPI Ontology
	 * @throws OWLOntologyCreationException
	 */
	private OWLOntology getInputOWLAPIOntology(String owlOntologyFileName) throws OWLOntologyCreationException {
		logger.info("Using OWLAPI to read the ontology:" + owlOntologyFileName + " ...");
		OWLOntology inputOntology;
		inputOntology = manager.loadOntologyFromOntologyDocument(new File(owlOntologyFileName));
		/*
		 * Logging:start
		 */
		if (config.getLogInfos().contains(LogInfo.INPUTONTOLOGY_INFO)) {
			logger.info("Statistic of the input ontology:" + owlOntologyFileName);
			OntologyInfo.printSize(inputOntology);
		}
		/*
		 * Logging:end
		 */

		return inputOntology;
	}

	/**
	 * @param inputOntology
	 * @return an ontology in the target DL Fragment
	 */
	private OWLOntology getOntologyInTargetDLFragment(OWLOntology inputOntology) {
		profileValidator = getOntologyValidator(inputOntology);
		profileValidator.validateOWLOntology();
		OWLOntology ontologyInTargetDLFragment = profileValidator.getOWLOntologyInTheTargetedDLFragment();
		manager.removeOntology(inputOntology);
		return ontologyInTargetDLFragment;
	}

	/**
	 * @param ontologyInTargetDLFragment
	 * @return the normalized ontology
	 */
	private OWLOntology getOntologyInTheNormalForm(OWLOntology ontologyInTargetDLFragment) {
		normalizer = getNormalizer(ontologyInTargetDLFragment);
		OWLOntology ontologyInNormalForm = normalizer.getNormalizedOntology();
		logger.info("Number of class assertions in Normalized Ontology:"
				+ ontologyInNormalForm.getAxioms(AxiomType.CLASS_ASSERTION, true).size());
		manager.removeOntology(ontologyInTargetDLFragment);
		return ontologyInNormalForm;
	}

	/**
	 * @param ontologyInNormalForm
	 * @return ontology with added auxiliary axioms wrt transitivity
	 */
	private OWLOntology getOntologyWithAuxiliaryAxiomsForTransitivity(OWLOntology ontologyInNormalForm) {
		TransitivityNormalizer transNormalizer = new TransitivityNormalizerWithHermit(ontologyInNormalForm);
		transNormalizer.normalizeTransitivity();
		OWLOntology ontologyInNormalFormAndAddedAuxiliaryAxiomsForTransitivity = transNormalizer.getResultingOntology();
		/*
		 * Debug
		 */
		if (config.getDebuglevels().contains(DebugLevel.NORMALIZATION)) {
			logger.info("");
			logger.info("***DEBUG: Normalized Ontology");

			OntologyInfo.printTBoxAxioms(ontologyInNormalFormAndAddedAuxiliaryAxiomsForTransitivity);

			OntologyInfo.printABoxAxioms(ontologyInNormalFormAndAddedAuxiliaryAxiomsForTransitivity);
			logger.info("***DEBUG: End");
			logger.info("");
		}
		/*
		 * Debug:End
		 */
		manager.removeOntology(ontologyInNormalForm);
		return ontologyInNormalFormAndAddedAuxiliaryAxiomsForTransitivity;

	}

	@Override
	public OrarOntology getNormalizedOrarOntology(String tboxFileName, String aboxListFileName) {

		long startParsing = System.currentTimeMillis();
		/*
		 * get a normalized owlapi ontology
		 */
		OWLOntology ontologyInNormalFormAndAddedAuxiliaryAxiomsForTransitivity = getNormalizedOWLAPIOntology(
				tboxFileName);

		/*
		 * Read aboxes in stream mannner
		 */
		StreamOntologyReader2InternalModel streamReader = new StreamOntologyReader2InternalModel(
				ontologyInNormalFormAndAddedAuxiliaryAxiomsForTransitivity, aboxListFileName);

		OrarOntology internalOntology = streamReader.getOntology();
		internalOntology.setActualDLConstructors(profileValidator.getDLConstructors());
		if (config.getLogInfos().contains(LogInfo.INPUTONTOLOGY_INFO)) {
			printOntologyInfo(internalOntology);
		}

		long endParsing = System.currentTimeMillis();
		long parsingTimeInSecond = (endParsing - startParsing) / 1000;

		if (config.getLogInfos().contains(LogInfo.PARSING_TIME)) {
			logger.info("Time (in second) for loading ontology: " + parsingTimeInSecond);
		}
		return internalOntology;

	}

	@Override
	public OWLOntology getOWLAPIOntology(String ontologyFileName) {
		try {
			long startParsing = System.currentTimeMillis();
			/*
			 * Read the ontologyFile
			 */

			OWLOntology inputOntology = getInputOWLAPIOntology(ontologyFileName);

			/*
			 * Get the ontology in the target DL fragment
			 */

			OWLOntology profiledOntology = getOntologyInTargetDLFragment(inputOntology);
			/*
			 * Remove unused ontololgies
			 */

			long endParsing = System.currentTimeMillis();
			long parsingTimeInSecond = (endParsing - startParsing) / 1000;

			if (config.getLogInfos().contains(LogInfo.PARSING_TIME)) {
				logger.info("Time (in second) for loading ontology: " + parsingTimeInSecond);
			}

			return profiledOntology;
		} catch (OWLOntologyCreationException e) {

			e.printStackTrace();
		}
		return null;
	}

	@Override
	public OWLOntology getOWLAPIOntology(String tboxFile, String aboxListFile) {
		try {
			long startParsing = System.currentTimeMillis();
			/*
			 * Read the ontologyFile
			 */

			OWLOntology inputOntology = getInputOWLAPIOntology(tboxFile);

			/*
			 * Get the ontology in the target DL fragment
			 */

			OWLOntology profiledOntology = getOntologyInTargetDLFragment(inputOntology);

			/*
			 * Read assertions in stream manner
			 */
			StreamOntologyReader2OWLAPI streamReader = new StreamOntologyReader2OWLAPI(profiledOntology, aboxListFile);

			OWLOntology owlOntology = streamReader.getOWLAPIOntology();
			if (config.getLogInfos().contains(LogInfo.INPUTONTOLOGY_INFO)) {
				printOntologyInfo(owlOntology);
			}

			long endParsing = System.currentTimeMillis();
			long parsingTimeInSecond = (endParsing - startParsing) / 1000;

			if (config.getLogInfos().contains(LogInfo.PARSING_TIME)) {
				logger.info("Time (in second) for loading ontology: " + parsingTimeInSecond);
			}

			return owlOntology;
		} catch (OWLOntologyCreationException e) {

			e.printStackTrace();
		}
		return null;
	}

	private void printOntologyInfo(OrarOntology internalOntology) {
		logger.info("Information of the input ontology.");

		logger.info("Number of individuals:" + internalOntology.getIndividualsInSignature().size());

		long numberOfCA = internalOntology.getNumberOfInputConceptAssertions();
		logger.info("Number of concept assertions:" + numberOfCA);

		long numberOfRA = internalOntology.getNumberOfInputRoleAssertions();
		logger.info("Number of role assertions:" + numberOfRA);

		long totalOfAssertions = numberOfCA + numberOfRA;
		logger.info("Number of concept assertions + role asesrtions:" + totalOfAssertions);
	}

	private void printOntologyInfo(OWLOntology owlOntology) {
		logger.info("Information of the input ontology.");

		logger.info("Number of individuals:" + owlOntology.getIndividualsInSignature(true).size());
		long numberOfCA = owlOntology.getAxioms(AxiomType.CLASS_ASSERTION, true).size();
		logger.info("Number of concept assertions:" + numberOfCA);
		long numberOfRA = owlOntology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION, true).size();
		logger.info("Number of role assertions:" + numberOfRA);
		long totalOfAssertions = numberOfCA + numberOfRA;
		logger.info("Number of concept assertions + role asesrtions:" + totalOfAssertions);
	}
}
