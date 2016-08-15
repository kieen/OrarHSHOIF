*Orar verion 0.2 supports realization/concept-materialization and consistency checking tasks for DL-Lite-HOD, 
 an extension of DL-Lite_core with disjunctions and nominals. 
 -This version has been tested with Java 1.7 on MacOS 10.10. 
 -Currently Orar can parse ABox files in RDF/XML, N3, or Turtle format; and TBox file in any format 
 supported by OWLAPI, e.g. RDF/XML, OWL/XML.
 -Orar uses OWLAPI parsing style. That is, when parsing the ABoxes it recognizes only assertions of 
 atomic concepts and roles that are defined in the TBox.
 -Orar also accepts an input TBox not in DL-Lite-HOD. In this case, the violated axioms will be ignored, 
  and, therefore, the derived assertions returned by Orar might differ from the entailed assertions of the input ontology.

*There is a sample ontology and binary file for Konclude (MacOS/Linux) in the folder "tutorial".  
 Please follow the instructions below for using Orar to materialize that ontology.

*Use case 1: run Orar with the following options/input:
   + compute the concept materialization of the ontology
   + run Orar with DLLite ontologies
   + use HermiT as an internal reasoner for the abstraction
   + print out the statistic of the (materialized) ontology and the reasoning time
   + the input TBox is: tbox.owl
   + the input ABoxes are: abox1.owl, abox2.owl, which are indicated in the file aboxList.txt
   
  Suppose we are now in the folder "tutorial". 
  We now use the following command to run Orar.jar with the above options and input:
   
  java -jar ../Orar.jar -task materialization -dl dllite_hod -reasoner hermit -statistic -reasoningtime -tbox ./tbox.owl -abox ./aboxList.txt

*Use case 2: run Orar with the following options/input:
   + check consistency of the ontology
   + run Orar with DLLite ontologies
   + use HermiT as an internal reasoner for the abstraction 
   + print out the statistic of the (materialized) ontology and the reasoning time
   + the input TBox is: tbox.owl
   + the input ABoxes are: abox1.owl, abox2.owl, which are indicated in the file aboxList.txt
  
  Suppose we are now in the folder "tutorial". 
  We now use the following command to run Orar.jar with the above options and input:
   
  java -jar ../Orar.jar -task consistency -dl dllite_hod -reasoner hermit -statistic -reasoningtime -tbox ./tbox.owl -abox ./aboxList.txt


*Use case 3: run Orar with the same options/input as in use case 1 but use Konclude as the inner reasoner for the abstraction.
 Suppose we are now in the folder "tutorial". We use the following command.
   
 java -jar ../Orar.jar -task materialization -dl dllite_hod -reasoner konclude -koncludepath ./KoncludeMac -port 9090 -statistic -reasoningtime -tbox ./tbox.owl -abox ./aboxList.txt
 
 (Note that you have to replace KoncludeMac by KoncludeLinux if you are running on a Linux-based machine, e.g. Ubuntu)

*To run Orar with other options, use the following command to see the detailed options.
 java -jar Orar.jar -help
 or 
 java -jar ../Orar.jar -help
 if you are in the tutorial folder. 




