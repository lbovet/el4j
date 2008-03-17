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

package ch.elca.el4j.seam.demo.action;



import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;

import org.jboss.seam.annotations.Name;

import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.bpm.CreateProcess;

import org.jboss.seam.bpm.BusinessProcess;


/**
 * 
 * This class serves as backing bean for all the pages in the holiday request
 * example. 
 * Since we use jBPM pageflow and process definition, its main 
 * purpose is to handle starting and ending tasks when certain actions are
 * performed by the user.
 * 
 * @author Frank Bitzer (FBI)
 *
 */
@Name("holidayAction")
@Scope(ScopeType.CONVERSATION)
public class HolidayAction {


	/**
	 * Starts the pageflow to create a holiday request
	 * 
	 * @return transition to input page
	 */
	@Begin(join = true, pageflow = "createRequest")
	public String createRequest() {
		
		return "createRequest";
		
	}
	
	/**
	 * Starts a process instance by sending the holiday request.
	 */
	@CreateProcess(definition = "holiday-request")
	public void sendRequest() {
	
		return;
	}

	/**
	 * Start the second pageflow for viewing and editing existing 
	 * holiday requests.
	 * 
	 * @return the transition from start state to the page with the 
	 * request list
	 */
	@Begin(join = true, pageflow = "holiday")
	public String showList() {
		
		return "startFlow";
	}
	

	
	/**
	 * Starts the task to evaluate a request.
	 */
	//@BeginTask //BeginTask annotation does not work with 
	//conversations/pageflow definitions so we have to use the API instead
	public void evaluateRequest() {
		
		
		Long taskId = Long.decode(((String) (javax.faces.context.FacesContext.getCurrentInstance()
		.getExternalContext().getRequestParameterMap().get("taskId"))));
		
		
		BusinessProcess.instance().resumeTask(taskId); //associate task with current conversation
		BusinessProcess.instance().startTask(); //call Task.start(actor.id)	
		
		return;
	}
	
	/**
	 * Ends the task for request evaluation when details were requested.
	 */
	//Annotations don't work here too, since they break the conversation
	//@EndTask(transition="request details")
	public void requestDetails() {
		
		BusinessProcess.instance().endTask("request details");
		
		return;
	}
	
	
	/**
	 * Starts task for providing details to a holiday request.
	 */
	//@BeginTask
	public void provideDetails() {
		
		Long taskId = Long.decode(((String) (javax.faces.context.FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("taskId"))));
				
				
		BusinessProcess.instance().resumeTask(taskId); //associate task with current conversation
		BusinessProcess.instance().startTask(); //call Task.start(actor.id)	
			
		
		return;
	}
	
	
	/**
	 * Ends the process by taking the transition to end-state.
	 * 
	 * Here you could insert some code to send an email containing
	 * the decision to the user or something like that...
	 */
	//@EndTask(transition="return decision")
	public void sendDecision() {
		
			
		BusinessProcess.instance().endTask("return decision");
		
		
		return;
	}
	
	/**
	 * Cancel evaluation, end task.
	 */
	//@EndTask(transition="return decision")
	public void cancelEvaluation() {
		
			
		BusinessProcess.instance().endTask("cancel evaluation");
		
		
		return;
	}
	
	/**
	 * User sent details, so end task for providing details.
	 */
	//@EndTask
	public void sendDetails() {
		
		BusinessProcess.instance().endTask("send details");
		
		return;
	}
	
	
	
}
