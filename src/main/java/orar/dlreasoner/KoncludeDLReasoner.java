package orar.dlreasoner;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.owllink.OWLlinkHTTPXMLReasonerFactory;
import org.semanticweb.owlapi.owllink.OWLlinkReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import orar.config.Configuration;

public class KoncludeDLReasoner extends DLReasonerTemplate {
	private int portNumber;
	private ExecuteWatchdog watchdog;
	private static Logger logger = Logger.getLogger(KoncludeDLReasoner.class);

	public KoncludeDLReasoner(OWLOntology owlOntology) {
		super(owlOntology);
		this.portNumber = 8080;// default value

	}

	public KoncludeDLReasoner(OWLOntology ontology, int portNumber) {
		super(ontology);
		this.portNumber = portNumber;
	}

	private void startKoncludeServer() {
		// CommandLine ulimit= new CommandLine("ulimit");
		// ulimit.addArgument("-m");
		// ulimit.addArgument("3500000");
		//

		CommandLine cmdLine = new CommandLine(Configuration.getInstance().getKONCLUDE_BINARY_PATH());
		// "/Users/kien/konclude/Konclude0.6/Binaries/Konclude");
		// "/Users/kien/koncludemac/Konclude-static");

		cmdLine.addArgument("owllinkserver");
		cmdLine.addArgument("-p");
		cmdLine.addArgument(Integer.toString(portNumber));

		/*
		 * +=Konclude.Logging.MinLoggingLevel=100 for stopping log info printed
		 * on the screen
		 */
		cmdLine.addArgument("+=Konclude.Logging.MinLoggingLevel=100");

		// cmdLine.addArgument("Konclude.Calculation.Memory.AllocationLimitation=true");
		// cmdLine.addArgument("Konclude.Calculation.Memory.MaximumAllocationSize=4000000000");//
		// in
		// // bytes

		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		/*
		 * set timeout for 24 hours
		 */
		watchdog = new ExecuteWatchdog(72 * 60 * 60 * 1000);
		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(1);
		// PumpStreamHandler ps = new PumpStreamHandler();
		// executor.setStreamHandler(ps);
		// executor.setStreamHandler(null);
		executor.setWatchdog(watchdog);
		try {
			executor.execute(cmdLine, resultHandler);

		} catch (ExecuteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * Sleep for 1s, waiting for the server is ready. TODO: need a better
		 * way to make sure that Konclude server is ready.
		 */
		try {
			Thread.sleep(3000); // 1000 milliseconds is one second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		logger.info("Konclude has been started.");
	}

	private void stopKoncludeServer() {

		watchdog.destroyProcess();
		// TODO: it works so far but it could be improved.
		try {
			Thread.sleep(3000); // 1000 milliseconds is one second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		if (reasoner != null)
			reasoner.dispose();
		logger.info("Konclude server has been stoped.");
	}

	@Override
	protected OWLReasoner getOWLReasoner(OWLOntology ontology) {
		startKoncludeServer();
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
	protected void dispose() {
		reasoner.dispose();
		stopKoncludeServer();
	}

}
