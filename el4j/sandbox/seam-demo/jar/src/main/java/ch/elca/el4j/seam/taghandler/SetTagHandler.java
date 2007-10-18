package ch.elca.el4j.seam.taghandler;

/*
This code is derived from code taken from "ninth avenue"'s "Seamless Java EE Utility Library"
The code was licensed under the Apache License Version 2.0.
See http://www.ninthavenue.com.au/extras/seamless for details
*/

import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

/**
* An implementation of c:set which evaluates(!!) the value expression when
* the variable is set, creating a facelet-scoped attribute.
*
* @author  Baeni Christoph (CBA) (APL licensed code from ninthavenue.com.au)
*/
public class SetTagHandler extends TagHandler {

	private final TagAttribute var;
	private final TagAttribute value;
	
	public SetTagHandler(TagConfig config) {
		super(config);
		this.value = this.getRequiredAttribute("value");
		this.var = this.getRequiredAttribute("var");
	}
	
	/** evaluate and set the attribute in the facelet scope */
	public void apply(FaceletContext ctx, UIComponent parent) {
		ctx.setAttribute(var.getValue(ctx), value.getObject(ctx));
	}
}