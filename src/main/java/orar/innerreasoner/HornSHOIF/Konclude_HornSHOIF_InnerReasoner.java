package orar.innerreasoner.HornSHOIF;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.owllink.OWLlinkHTTPXMLReasoner;
import org.semanticweb.owlapi.owllink.OWLlinkHTTPXMLReasonerFactory;
import org.semanticweb.owlapi.owllink.OWLlinkReasonerConfiguration;
import org.semanticweb.owlapi.owllink.builtin.requests.GetFlattenedObjectPropertySources;
import org.semanticweb.owlapi.owllink.builtin.requests.GetFlattenedObjectPropertyTargets;
import org.semanticweb.owlapi.owllink.builtin.response.ResponseMessage;
import org.semanticweb.owlapi.owllink.builtin.response.SetOfIndividuals;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import orar.config.Configuration;
import orar.config.DebugLevel;
import orar.util.PrintingHelper;

public class Konclude_HornSHOIF_InnerReasoner extends HornSHOIF_InnerReasonerTemplate {
	private int portNumber;
	private ExecuteWatchdog watchdog;
	private static Logger logger = Logger.getLogger(Konclude_HornSHOIF_InnerReasoner.class);
	private ByteArrayOutputStream stdout;
	private PumpStreamHandler pumpStreamHander;
	private final int POOLING_INTERVAL = 5; // miliseconds to check if Kondlude
											// is ready
	private final String KONCLUDE_READY = "Listening on port";
	DefaultExecuteResultHandler resultHandler;

	public Konclude_HornSHOIF_InnerReasoner(OWLOntology owlOntology) {
		super(owlOntology);
		this.portNumber = 8080;// default value
		/*
		 * for running Konclude via command line
		 */
		this.stdout = new ByteArrayOutputStream();
		this.pumpStreamHander = new PumpStreamHandler(stdout);
		this.resultHandler = new DefaultExecuteResultHandler();

	}

	public Konclude_HornSHOIF_InnerReasoner(OWLOntology ontology, int portNumber) {
		super(ontology);
		this.portNumber = portNumber;
		/*
		 * for running Konclude via command line
		 */
		this.stdout = new ByteArrayOutputStream();
		this.pumpStreamHander = new PumpStreamHandler(stdout);
		this.resultHandler = new DefaultExecuteResultHandler();

	}

	private void startKoncludeServer() {
		CommandLine cmdLine = new CommandLine(Configuration.getInstance().getKONCLUDE_BINARY_PATH());

		cmdLine.addArgument("owllinkserver");
		cmdLine.addArgument("-p");
		cmdLine.addArgument(Integer.toString(portNumber));

		/*
		 * +=Konclude.Logging.MinLoggingLevel=100 for stopping log info printed
		 * on the screen
		 */
		// cmdLine.addArgument("+=Konclude.Logging.MinLoggingLevel=100");

		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		/*
		 * set timeout for 24 hours
		 */
		watchdog = new ExecuteWatchdog(72 * 60 * 60 * 1000);
		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(1);
		executor.setStreamHandler(pumpStreamHander);

		executor.setWatchdog(watchdog);
		try {
			executor.execute(cmdLine, resultHandler);

		} catch (ExecuteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * pooling Konclude to see if it is ready
		 */
		while (true) {
			try {
				Thread.sleep(POOLING_INTERVAL);
				String result = stdout.toString();
				if (result.toLowerCase().contains(KONCLUDE_READY.toLowerCase())) {
					logger.info(result);
					break;
				}
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
		logger.info("Konclude has been started.");
	}

	private void stopKoncludeServer() {

		watchdog.destroyProcess();
		// TODO: it works so far but it could be improved.
		try {
			Thread.sleep(1000); // 1000 milliseconds is one second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		if (reasoner != null)
			reasoner.dispose();
		logger.info("Konclude server has been stoped.");
	}

	@Override
	protected OWLReasoner getOWLReasoner(OWLOntology ontology) {
		// /*
		// * Do some tricks to overcome a bug, error while querying inverse role
		// * assertion, of Konclude.<br> What we do is notice that there is
		// * inverse role of each role in the ontology. Then Konclude
		// (SOMETIMES) does his job.
		// *
		// *
		// */
		// // TODO: remove this part of code when Konclude's bug is fixed.
		// OWLOntologyManager ontoManager=ontology.getOWLOntologyManager();
		// Set<OWLObjectProperty> allRoles =
		// ontology.getObjectPropertiesInSignature(true);
		// allRoles.remove(this.dataFactory.getOWLTopObjectProperty());
		// allRoles.remove(this.dataFactory.getOWLBottomObjectProperty());
		// for (OWLObjectProperty role : allRoles) {
		// OWLObjectInverseOf invRole =
		// this.dataFactory.getOWLObjectInverseOf(role);
		// OWLInverseObjectPropertiesAxiom invAxiom =
		// this.dataFactory.getOWLInverseObjectPropertiesAxiom(role,
		// invRole);
		// ontoManager.addAxiom(ontology, invAxiom);
		// }

		/*
		 * add some assertions only in case of Konclude to reduce the number of
		 * http requests while getting results from Konclude
		 */

		this.axiomsAdder.addAxiomsForPredecessorOfSingletonConcept();
		/*
		 * start Konclude server
		 */
		startKoncludeServer();
		/*
		 * get reasoner
		 */
		try {
			URL url = new URL("http://localhost:" + portNumber);

			OWLlinkReasonerConfiguration reasonerConfiguration = new OWLlinkReasonerConfiguration(url);
			logger.info("Connected to Konclude server at: " + url.toString());
			OWLlinkHTTPXMLReasonerFactory factory = new OWLlinkHTTPXMLReasonerFactory();
			return factory.createNonBufferingReasoner(ontology, reasonerConfiguration);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	protected void computeRoleAssertionForInstancesOfSingletonConcept() {

		Set<OWLNamedIndividual> individuals = new HashSet<>(this.instancesOfSingletonConcepts);
		if (config.getDebuglevels().contains(DebugLevel.REASONING_ABSTRACTONTOLOGY)) {
			logger.info("***DEBUG*** individuals are instances of singleton concepts:");
			PrintingHelper.printSet(individuals);
		}
		/*
		 * retain only U-individuals.
		 */
		individuals.retainAll(this.abstractDataFactory.getUAbstractIndividuals());

		if (config.getDebuglevels().contains(DebugLevel.REASONING_ABSTRACTONTOLOGY)) {
			logger.info("***DEBUG*** individuals U are instances of singleton	 concepts:");
			PrintingHelper.printSet(individuals);
		}
		for (OWLNamedIndividual eachU : individuals) {
			Set<OWLObjectProperty> allRoles = this.owlOntology.getObjectPropertiesInSignature(true);
			allRoles.remove(this.dataFactory.getOWLTopObjectProperty());
			allRoles.remove(this.dataFactory.getOWLBottomObjectProperty());
			for (OWLObjectProperty role : allRoles) {
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

			}
		}
		getInverseRoleAssertionOfInstancesOfSingletonConcepts();
	}

	private void getInverseRoleAssertionOfInstancesOfSingletonConcepts() {
		Set<OWLNamedIndividual> allPredecessorsOfSingletonConcepts = new HashSet<>(
				this.instancesOfPredecessorOfSingletonConcept);
		// retain only to U
		allPredecessorsOfSingletonConcepts.retainAll(this.abstractDataFactory.getUAbstractIndividuals());
		for (OWLNamedIndividual ind_u : allPredecessorsOfSingletonConcepts) {
			for (OWLObjectProperty role_R : this.rolesForPredecessorOfSingletonConcept) {
				/*
				 * query for assertion of the form role(eachU, ?x)
				 */
				Set<OWLNamedIndividual> objects = reasoner.getObjectPropertyValues(ind_u, role_R).getFlattened();
				/*
				 * retain only x-individuals
				 */
				objects.retainAll(this.abstractDataFactory.getXAbstractIndividuals());
				for (OWLNamedIndividual eachObject : objects) {
					this.roleAssertionList.addUX_RoleAssertionForCTypeAndType(ind_u, role_R, eachObject);
				}
			}
		}
	}

	@Override
	protected void dispose() {
		reasoner.dispose();
		stopKoncludeServer();
	}

}
