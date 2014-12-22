README Reporting Tool
=====================
 
Preconditions
-------------

- installed Java JDK and Apache ANT 
- installed pdflatex, makeindex (e.g. from miktex on windows)
- installed gnuplot
- to use the search function of the required programs, go to windows environment and add the necessary directories of your gnuplot/pdflatex/makeindex to the PATH variable or use the config.ini to give the path to the required programs

How to build the jar of the Reporting Tool
------------------------------------------

- compile on commandline by changing into the directory and run ant:
	$ant

How to use the Reporting Tool
-----------------------------

- to inform about the optional parameter of the Reporting Tool look into the help: 
	$java -jar ReportingTool.jar -help

- to start the Reporting Tool with default parameter:
	$java -jar ReportingTool.jar --inputFile=/path/to/file --name=NameofMeasurement


Default parameter
-----------------

- The Reporting Tool will search your own path of pdflatex, makeindex and gnuplot. 

- The input file and the name of the measurement must commit.

- The default allocations:
	o Number of bins at the x-axis of histogram is default 4.
	o Scale of values at the x-axis of histogram is default "n", this means the x-axis is normal, standing for linear.
	o Lower bound of the measurement filter (default is 5).
	o Upper Bound of the measurement filter (default is 10).


How to use the config.ini
-------------------------

The config.ini is setting the parameter of the Reporting Tool. It replaces the default values of the parameter. The values of the config.ini be replaced by the transmitted parameter in the commandline. 


How to write a valid input file
-------------------------------

- The first column is an identifier, they must be distinct.
- The second column is reserved by the secrets.
- The third column should be the time measurement of the secret in this row.
