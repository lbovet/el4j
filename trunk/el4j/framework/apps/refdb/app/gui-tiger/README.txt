RefDbGui Tiger
==============

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

See package.html in java/ch/elca/el4j/services/richclient/config.


Running
-------

Run ch/elca/el4j/apps/refdb/gui/MainStandalone


Design considerations
---------------------

It is nearly an industry standard to specify configuration in XML. We chose to use Java because Java IDEs provide much better support at development time (code completion, type checks, ...) and code snippets can be inlined into configuration settings (no need to put a 3 line method in a seperate file). We feel that these considerations outweigh the disadvantage that the GUI configuration can only be adjusted in the presence of a compiler.

In guis, components are typically wired using listeners, i.e. components are wired by providing the actions that keep the gui current. We take an alternative approach that directly formalizes the currency criteria (which we call GUI-invariants) and have framework infrastructure handle the neccessary updates. Like invariants in ordinary code, this supports abstraction by seperating the "what" from the "how". By removing this burden from the developers, this change encourages developers to think more deeply about the "what". Moreover, it consolidates the code defining a given abstract state that was previously spread across all contributing listeners to one place, making contradictory expectations obvious. Finally (unlike listeners) the simplified view provided by these invariants can be communicated to users of a system, contributing to a more "ubiquitous language". Nevertheless, this is a major paradigm shift, which causes a couple of problems. One is interoperability with legacy code, which is provided by setting a SettableObservableValue within listeners rsp. by subscribing to an ObservableValue's change notifications with the desired listener. Another is that people have to rethink old habits, which increases the required effort to learn to use this system effectively. Except providing top-notch documentation, there is little we can do about that.

Maturity (or lack thereof ;) )
------------------------------

To accelerate the construction of this prototype, we have concentrated on the configuration interface. In particular, remoting the repository is currently broken.

We have yet to provide the full capabilities of refdb; the impossibility to create entities is particularly annoying.