package orar.commandline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Argument {
	static final String STATISTIC = "statistic";
	static final String LOADING_TIME = "loadtime";
	static final String REASONING_TIME = "reasoningtime";
	static final String TOTAL_TIME = "totaltime";
	static final String TBOX = "tbox", ABOX = "abox", ONTOLOGY = "ontology";
	static final String REASONER = "reasoner", KONCLUDE = "konclude", HERMIT = "hermit", PELLET = "pellet",
			FACT = "fact";
	static final String PORT = "port";
	static final String KONCLUDEPATH = "koncludepath";
	static final String SPLITTING = "splitting";
	static final String DL = "dl", DLLITE_R= "dllite_r", HORN_SHOIF = "horn_shoif";

	static final List<String> reasonerList = new ArrayList<String>(Arrays.asList(KONCLUDE, HERMIT, FACT, PELLET));

}
