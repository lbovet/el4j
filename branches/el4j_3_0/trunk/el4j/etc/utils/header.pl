#!/usr/bin/perl
# Replace all internal headers with external ones.

# Skip header.
$_ = <>;
if (! m#^/\*#) { die "First line does not start comment."; }

$c = 1;
while ($c) {
	$_ = <>;
	if (m#^ \*/#) { $c = 0; }
	elsif (!m/^ \*/) { die "Non-comment line in header: Line ($c) '$_'";}
	else { $c++;}
}

# Insert new header.
print <<'HDR';
/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
HDR

# Rest of file.
while (<>) {
	print;
}
