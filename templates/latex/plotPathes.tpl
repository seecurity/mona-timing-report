\section{::name:::}
LowerBound: ::lowerBound::: \\
UpperBound: ::upperBound:::
\subsection{Scatterplot}
A point in this graphic represents a measured time of a secret. The Y-axis denotes the timing value and the X-axis the $n^{th}$ measurement. Because the measurements are shown in the order they were measured, this representation allows the detection of temporal disturbances during the measurements. Take an example where the timing values suddenly plunge during the measurements, which may result in a bad data set. Another example is when the variance of the measurement changes during the measurements. Both examples can be detected quite well in a scatterplot. \newline
	\begin{figure}[ht]
	\includegraphics[width=1\textwidth]{::scatterplot:::}
	\caption[::name::: - Scatterplot (::lowerBound:::-::upperBound:::).]{Scatterplot}
	\end{figure}
	\newpage

\subsection{Box-Plot}
This \emph{whisker} (also called \emph{Box-Plot}) diagram illustrates three values that provide a good summary on the data set. It shows the upper quartile, the lower quartile, and the median. Given a data set with a reasonable amount of measurements and good quality, this diagram will probably already hint whether or not there are significant timing differences. Note that we do not show the minimum and maximum timing values here, because they tend to have many outliers.
	\begin{figure}[ht]
	\includegraphics[width=1.0\textwidth]{::boxPlot:::}
	\caption[::name::: - Box-Plot (::lowerBound:::-::upperBound:::).]{Box-Plot}

	\end{figure}
	\newpage

\subsection{Cumulative Distribution Function}
A \emph{CDF (Cumulative Distribution Function}) diagram displays the distribution of the different data sets.
	\begin{figure}[ht]
	\includegraphics[width=1.0\textwidth]{::cdf:::}
	\caption[::name::: - CDF (::lowerBound:::-::upperBound:::).]{CDF}
	\end{figure}
\newpage

\subsection{Histogram}
A \emph{Histogram} shows the distribution of the different data sets.
	\begin{figure}[ht]
	\includegraphics[width=1.0\textwidth]{::histogram:::}
	\caption[::name::: - Histogram (::lowerBound:::-::upperBound:::).]{Histogram}
	\end{figure}
\newpage
