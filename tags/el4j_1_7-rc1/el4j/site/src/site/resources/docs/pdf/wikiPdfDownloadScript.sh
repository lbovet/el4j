#!/bin/sh

# this scripts helps to download files from the wiki

wget -v -O  ReferenceDoc.pdf http://wiki.elca.ch/twiki/el4j/bin/genpdf/EL4J/ReferenceDoc?pdfstruct=book&pdfnumberedtoc=true
wget -v -O  FrequentlyAskedQuestions.pdf http://wiki.elca.ch/twiki/el4j/bin/genpdf/EL4J/FrequentlyAskedQuestions
wget -v -O  TroubleshootingGuide.pdf http://wiki.elca.ch/twiki/el4j/bin/genpdf/EL4J/TroubleshootingGuideEl4j
wget -v -O  GettingStarted.pdf http://wiki.elca.ch/twiki/el4j/bin/genpdf/EL4J/GettingStarted?pdfstruct=book&pdfnumberedtoc=true
wget -v -O  SetupEL4J.pdf http://wiki.elca.ch/twiki/el4j/bin/genpdf/EL4J/SetupEL4J?pdfstruct=book&pdfnumberedtoc=true
wget -v -O  UniqueEL4JFeatures.pdf http://wiki.elca.ch/twiki/el4j/bin/genpdf/EL4J/FeaturesOfEl4j
# do this better by hand: 
# wget -v -O  MavenCheatSheet_EL4J.pdf http://wiki.elca.ch/twiki/el4j/bin/genpdf/EL4J/MavenCheatSheet?skin=pdf

echo "Create MavenCheatSheet_EL4J manually: Print it (printable version) in browser to PDF"
 