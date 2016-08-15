*Orar verion 0.1 supports the full materialization task of Horn SHOIF ontologies.
 -This version has been tested with Java 1.7 on MacOS 10.10. 
 -Currently Orar can parse ABox files in RDF/XML, N3, or Turtle format; and TBox file in any format 
 supported by OWLAPI, e.g. RDF/XML, OWL/XML.
 -Orar uses OWLAPI parsing style. That is, when parsing the ABoxes it recognizes only assertions of 
 atomic concepts and roles that are defined in the TBox.
 -Orar also accepts an input TBox not in Horn SHOIF. In this case, axioms not in Horn SHOIF will be ignored, 
  and, therefore, the derived assertions returned by Orar might differ from the entailed assertions of the input ontology.

*There is a sample ontology and binary file for Konclude (MacOS/Linux) in the folder "tutorial".  
 Please follow the instructions below for using Orar to materialize that ontology.

*Use case 1: run Orar with the following options/input:
   + use HermiT as an internal reasoner to materialize the abstractions
   + compute the full materialization of the ontology
   + print out the statistic of the (materialized) ontology 
   + print out the reasoning time
   + the input TBox is: tbox.owl
   + the input ABoxes are: abox1.owl, abox2.owl (in RDF/XML format), which are indicated in the file aboxList.txt
   + print out all entailed atomic assertions (in OWL/XML format) to a file, e.g. materializedABox.owl
 Suppose we are now in the folder "tutorial". 
 We now use the following command to run Orar.jar with the above options and input:
   
 java -jar ../Orar.jar -reasoner hermit -statistic -reasoningtime -tbox ./tbox.owl -abox ./aboxList.txt -outputabox ./materializedABox.owl


*Use case 2: run Orar with the following options/input:
   + use Konclude as an internal reasoner to materialize the abstractions
   + compute the full materialization of the ontology
   + print out the statistic of the (materialized) ontology
   + print out the loading time 
   + print out the reasoning time
   + the input TBox is: tbox.owl
   + the input ABoxes are: abox1.owl, abox2.owl (in RDF/XML format), which are indicated in the file aboxList.txt
 Suppose we are now in the folder "tutorial". 
 We now use the following command to run Orar.jar with the above options and input. 
   
 java -jar ../Orar.jar -reasoner konclude -koncludepath ./KoncludeMac -port 9090 -statistic -reasoningtime -loadtime -tbox ./tbox.owl -abox ./aboxList.txt

 (Note that you have to replace KoncludeMac by KoncludeLinux if you are running on a Linux-based machine, e.g. Ubuntu)

*To run Orar with other options, use the following command to see the detailed options.
  java -jar Orar.jar -help
or 
 java -jar ../Orar.jar -help
if you are in the tutorial folder. 




