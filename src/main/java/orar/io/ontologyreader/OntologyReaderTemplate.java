package orar.io.ontologyreader;

import java.io.File;

import orar.config.Configuration;
import orar.config.DebugLevel;
import orar.config.LogInfo;
import orar.dlfragmentvalidator.OWLOntologyValidator;
import orar.modeling.ontology.OrarOntology;
import orar.normalization.Normalizer;
import orar.normalization.transitivityelimination.TransitivityNormalizer;
import orar.normalization.transitivityelimination.TransitivityNormalizerWithHermit;
import orar.util.OntologyInfo;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public abstract class OntologyReaderTemplate implements OntologyReader {
	// protected OWLOntologyValidator profiledOntCreator;
	protected Normalizer normalizer;
	private Logger logger = Logger.getLogger(OntologyReaderTemplate.class);
	private Configuration config = Configuration.getInstance();

	protected abstract OWLOntologyValidator getOntologyValidator(OWLOntology owlOntology);

	protected abstract Normalizer getNormalizer(OWLOntology owlOntology);

	@Override
	public OrarOntology getNormalizedOrarOntology(String ontologyFileName) {
		try {
			long startParsing = System.currentTimeMillis();
			/*
			 * Read the tboxFile
			 */
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			logger.info("Using OWLAPI to read the ontology:" + ontologyFileName + " ...");
			OWLOntology inputOntology;
			inputOntology = manager.loadOntologyFromOntologyDocument(new File(ontologyFileName));
			/*
			 * Logging:start
			 */
			if (config.getLogInfos().contains(LogInfo.INPUTONTOLOGY_INFO)) {
				logger.info("Statistic of the input ontology:" + ontologyFileName);
				OntologyInfo.printSize(inputOntology);
			}
			/*
			 * Logging:end
			 */

			/*
			 * Get ontology in desired DL fragment: SHOIF
			 */

			OWLOntologyValidator profileValidator = getOntologyValidator(inputOntology);
			profileValidator.validateOWLOntology();
			OWLOntology ontologyInDesiredDLFragment = profileValidator.getOWLOntologyInTheTargetedDLFragment();
			logger.info("Number of max cardinality axioms:" + profileValidator.getNumberOfMaxCardinalityAxioms());
			// logger.info("Number of class assertions in Horn ALCHOIF
			// Ontology:"
			// + profiledOntology.getAxioms(AxiomType.CLASS_ASSERTION,
			// true).size());

			/*
			 * Normalize the ontology into normal form
			 */
			Normalizer normalizer = getNormalizer(ontologyInDesiredDLFragment);
			OWLOntology ontologyInNormalForm = normalizer.getNormalizedOntology();
			logger.info("Number of class assertions in Normalized Ontology:"
					+ ontologyInNormalForm.getAxioms(AxiomType.CLASS_ASSERTION, true).size());

			/*
			 * eliminate transitivity
			 */
			TransitivityNormalizer tranEliminator = new TransitivityNormalizerWithHermit(ontologyInNormalForm);
			tranEliminator.normalizeTransitivity();
			OWLOntology normalizedOntology = tranEliminator.getResultingOntology();

			/*
			 * Debug
			 */
			if (config.getDebuglevels().contains(DebugLevel.NORMALIZATION)) {
				logger.info("");
				logger.info("***DEBUG: Normalized Ontology");

				OntologyInfo.printTBoxAxioms(normalizedOntology);

				OntologyInfo.printABoxAxioms(normalizedOntology);
				logger.info("***DEBUG: End");
				logger.info("");
			}

			/*
			 * Convert to AromaOntology
			 */
			OntologyConverter converter = new OntologyConverter(normalizedOntology);

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

			/*
			 * Remove unused ontololgies
			 */
			manager.removeOntology(inputOntology);
			inputOntology = null;
			manager.removeOntology(ontologyInDesiredDLFragment);
			ontologyInDesiredDLFragment = null;
			manager.removeOntology(normalizedOntology);
			normalizedOntology = null;
			long endParsing = System.currentTimeMillis();
			long parsingTimeInSecond = (endParsing - startParsing) / 1000;

			if (config.getLogInfos().contains(LogInfo.PARSING_TIME)) {
				logger.info("Time (in second) for loading ontology: " + parsingTimeInSecond);
			}
			return internalOntology;
		} catch (OWLOntologyCreationException e) {

			e.printStackTrace();
		}
		return null;
	}

	@Override
	public OrarOntology getNormalizedOrarOntology(String tboxFileName, String aboxListFileName) {
		try {
			long startParsing = System.currentTimeMillis();
			/*
			 * Read the tboxFile
			 */
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			logger.info("Using OWLAPI to read TBox file:" + tboxFileName + " ...");
			OWLOntology inputTBox;
			inputTBox = manager.loadOntologyFromOntologyDocument(new File(tboxFileName));
			/*
			 * Get ontology in the desied DL fragment
			 */

			OWLOntologyValidator ontologyValidator = getOntologyValidator(inputTBox);
			ontologyValidator.validateOWLOntology();
			OWLOntology ontologyInTheDLFragment = ontologyValidator.getOWLOntologyInTheTargetedDLFragment();
			logger.info("Number of max cardinality axioms:" + ontologyValidator.getNumberOfMaxCardinalityAxioms());
			/*
			 * Normalize ontology into normal form. ALCHOIF and SHOIF have the
			 * same procedure.
			 */
			Normalizer normalizer = getNormalizer(ontologyInTheDLFragment);
			OWLOntology ontologyInNormalForm = normalizer.getNormalizedOntology();

			/*
			 * eliminate transitivity
			 */
			TransitivityNormalizer tranEliminator = new TransitivityNormalizerWithHermit(ontologyInNormalForm);
			tranEliminator.normalizeTransitivity();
			OWLOntology normalizedOntology = tranEliminator.getResultingOntology();
			/*
			 * Read aboxes in stream mannner
			 */
			StreamOntologyReader2InternalModel streamReader = new StreamOntologyReader2InternalModel(normalizedOntology,
					aboxListFileName);

			OrarOntology internalOntology = streamReader.getOntology();
			internalOntology.setActualDLConstructors(ontologyValidator.getDLConstructors());
			if (config.getLogInfos().contains(LogInfo.INPUTONTOLOGY_INFO)) {
				printOntologyInfo(internalOntology);
			}

			manager.removeOntology(inputTBox);
			inputTBox = null;
			manager.removeOntology(ontologyInTheDLFragment);
			ontologyInTheDLFragment = null;
			manager.removeOntology(normalizedOntology);
			normalizedOntology = null;
			long endParsing = System.currentTimeMillis();
			long parsingTimeInSecond = (endParsing - startParsing) / 1000;

			if (config.getLogInfos().contains(LogInfo.PARSING_TIME)) {
				logger.info("Time (in second) for loading ontology: " + parsingTimeInSecond);
			}
			return internalOntology;
		} catch (OWLOntologyCreationException e) {

			e.printStackTrace();
		}
		return null;
	}

	@Override
	public OWLOntology getOWLAPIOntology(String ontologyFileName) {
		try {
			long startParsing = System.currentTimeMillis();
			/*
			 * Read the ontologyFile
			 */
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			logger.info("Using OWLAPI to parse the ontology:" + ontologyFileName + " ...");
			OWLOntology inputOntology;
			inputOntology = manager.loadOntologyFromOntologyDocument(new File(ontologyFileName));

			/*
			 * Get the fragment
			 */

			OWLOntologyValidator profiledOntCreator = getOntologyValidator(inputOntology);
			profiledOntCreator.validateOWLOntology();
			OWLOntology profiledOntology = profiledOntCreator.getOWLOntologyInTheTargetedDLFragment();
			logger.info("Number of max cardinality axioms:" + profiledOntCreator.getNumberOfMaxCardinalityAxioms());
			/*
			 * Remove unused ontololgies
			 */
			manager.removeOntology(inputOntology);
			inputOntology = null;

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
			 * Read the tboxFile
			 */
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			logger.info("Using OWLAPI to parse TBox file:" + tboxFile + " ...");
			OWLOntology inputTBox;
			inputTBox = manager.loadOntologyFromOntologyDocument(new File(tboxFile));

			/*
			 * Get ontology in the DL fragment
			 */

			OWLOntologyValidator profiledOntCreator = getOntologyValidator(inputTBox);
			profiledOntCreator.validateOWLOntology();
			OWLOntology profiledOntology = profiledOntCreator.getOWLOntologyInTheTargetedDLFragment();
			logger.info("Number of max cardinality axioms:" + profiledOntCreator.getNumberOfMaxCardinalityAxioms());
			/*
			 * Read assertions in stream manner
			 */
			StreamOntologyReader2OWLAPI streamReader = new StreamOntologyReader2OWLAPI(profiledOntology, aboxListFile);

			OWLOntology owlOntology = streamReader.getOWLAPIOntology();
			if (config.getLogInfos().contains(LogInfo.INPUTONTOLOGY_INFO)) {
				printOntologyInfo(owlOntology);
			}

			manager.removeOntology(inputTBox);
			inputTBox = null;
			manager.removeOntology(profiledOntology);
			profiledOntology = null;

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
