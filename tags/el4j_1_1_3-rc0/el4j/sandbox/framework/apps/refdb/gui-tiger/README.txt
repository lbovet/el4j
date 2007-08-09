RefDbGui Tiger
==============

IMPORTANT NOTE:
---------------

We have not cleaned up the RefDbGui Tiger application yet, and it does not run with the latest release of EL4J. This will be done in a further release.

Purpose
-------

The demo aims to provide a lightweight configuration interface to Spring RCP by leveraging the information contained in the Domain Object Model.


Design Goals
------------

- Simple things should be simple, complex things should be possible.
	
	Every gui element features a settings-based configuration interface. Whenever possible, default settings are derived from the DOM. If these are approapriate,
	no further action needs to be taken, otherwise, the settings can be overridden individually. If the desired behavior can not be expressed using settings alone, the user	can subclass generic elements or write his own.
	
	A speciality is that even behavior can be expressed using settings to some extent since some settings actually hold snippets of code (written by the user or taken from the library). For instance, to wire a search form to a table view, you simply tell the table view that its filter is the search view's query.
	

Documentation
-------------

See twiki / el4j reference documentation, section module spring rcp, subsection tiger.


Running
-------

Run ch/elca/el4j/apps/refdb/gui/MainStandalone

Maturity (or lack thereof ;) )
------------------------------

To accelerate the construction of this prototype, we have concentrated on the configuration interface. In particular, remoting the repository is currently broken.

We have yet to provide the full capabilities of refdb; the impossibility to create entities is particularly annoying.