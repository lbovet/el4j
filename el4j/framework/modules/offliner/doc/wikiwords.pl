#!/usr/bin/perl
# Script to take some of the annoyances out of twiki syntax
# when writing in a text editor.

$pre = 0;

while (<>) {
	handle_line();

	if (/^[ \t]*<\/pre>/) {
		$pre = 0;
	}
	handle_pre() if ($pre);
	
	if (/^[ \t]*<pre>/) {
		$pre = 1;
	} 
	
	print;
}

# -----------

# Default operations for a line.
sub handle_line {
	# Tabs at line start are purely decorative - except in code.
	s/^\t*// unless ($pre);
	
	# Turn off wiki-words by default because 99% of the time something in
	# UpperCamelCase is simply a java class name.
	# Except of course in code.
	s/(\b[A-Z][a-zA-Z]*[a-z]+[A-Z]+[a-z]+[a-zA-Z]*\b)/!\1/g unless ($pre);

	# For importing javadocs directly. Handles code and header tags.
	# Removes leading comment /** , " * * , */ signs.
	# Also allows "H1 Title-here" on a plain text line.
	# Note that due to twiki "simple" syntax, things like
	# "Some<i> text in italic</i>."
	# can go wrong.
	s/^\/\*\*//;
	s/^ \* //;
	s/^[ ]?\*\///;
	s/<p>//;
	s#<code>([a-zA-Z]*)</code>#=\1=#g;
	s/^[ ]*<?H1>?[ ]*/---+ /;
	s/^[ ]*<?H2>?[ ]*/---++ /;
	s/^[ ]*<?H3>?[ ]*/---+++ /;
	s/^[ ]*<?H4>?[ ]*/---++++ /;
	s/<\/H[0-9]>//;
	# OFF s/<\/?i>/_/;

	# Avoid having to count exact multiples of three spaces.
	# Note "-" is used for enums as "*" is filtered in the javadoc section.
	s/^[ ]*[0-9]+\. /   1. /;
	s/^[ ]*[0-9]+\.([0-9]+)\.? /      1. /;
	s/^[ ]*[0-9]+\.[0-9]+\.([0-9]+)\.? /         1. /;
	s/^[ ]*-[ ]+/   * /;
	s/^[ ]*--[ ]+/      * /;
	s/^[ ]*---[ ]+/         * /;
	s/^[ ]*\$[ ]+([^:]*)[ ]+:[ ]/   \$ \1 : /;
}

# Fromat bean definitions and code correctly.
sub handle_pre {
	s/<!--/%GREEN%\&lt;!--/g;
	s/-->/--\&gt;%ENDCOLOR%/g;
	s/</\&lt;/g;
	s/>/\&gt;/g;
	s#(//.*)#%GREEN%\1%ENDCOLOR%#;
	s#(/\*[^*])#%GREEN%\1#;
	s#/\*$#%GREEN%/*#;
	s#/\*\*#%BLUE%/**#;
	s#\*/#\*/%ENDCOLOR%#;
}

