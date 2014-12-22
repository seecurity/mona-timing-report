# mona-timing-report
 
## Preconditions

- Installed Java JDK and Apache ANT 
- Installed pdflatex, makeindex (e.g. from miktex on windows)
- Installed gnuplot
- To use the search function of the required programs, go to windows environment
  and add the necessary directories of gnuplot/pdflatex/makeindex to the `PATH`
  variable or use the `config.ini` to supply the path to the required programs.

## Building

Compile on command line by changing into the directory and running ant:

```bash
$ ant
```

## Usage

Run the following command to list optional parameters of the Reporting Tool:

```bash
$ java -jar ReportingTool.jar -help
```

Start the Reporting Tool with default parameters:

```bash
$ java -jar ReportingTool.jar --inputFile=/path/to/file --name=NameofMeasurement
```

## Default Parameters

- The Reporting Tool will lookup the path of pdflatex, makeindex and gnuplot.
- The input file and the name of the measurement must be specified.
- The default allocations:
	* Number of bins at the x-axis of the histogram defaults to `4`.
	* Scale of values at the x-axis of histogram defaults to `n`, which means
      the x-axis is normal (linear).
	* Lower bound of the measurement filter (defaults to `5`).
	* Upper Bound of the measurement filter (defaults to `10`).

## Configuration

The `config.ini` can be used to set parameters of the Reporting Tool. It
replaces the default values of the parameters. The values of the `config.ini`
will be superseeded by parameters specified as command line arguments.

## Writing a Valid Input File

- The first column is a identifier, they must be distinct.
- The second column is reserved by the secrets.
- The third column should be the time measurement of the secret in this row.
