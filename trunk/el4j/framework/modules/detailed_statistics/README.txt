Module Detailed Statistics:


Some of the sources for this module came from legacy classes, which had to be radically cleaned up and refactored and there are still 
things that are not satisfactory.

The diagram, for instance, is very small and the text on arrays is not displayed in some browsers (Firefox less then version 2, e.g.)
(for a viewer take a look at Squiggle http://xmlgraphics.apache.org/batik/svgviewer.html )
Moreover, there are still dozent of Magic Numbers in the source code, especially in class SVGGraphCreator.
Next, the Class ch.elca.el4j.services.statistics.detailed.processing.DataProcessor still needs a 
method "enrichData" that is there just for legacy reasons (and should be removed).
Finally, this module is missing precondition and testcases.

