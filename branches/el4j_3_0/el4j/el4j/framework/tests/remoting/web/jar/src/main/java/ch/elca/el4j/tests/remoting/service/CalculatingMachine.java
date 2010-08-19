/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.tests.remoting.service;

/** This interfaces sole purpose is to be extended by Calculator to 
 *  test inheritance in exported remote services.
 *  
 * 	@svnLink $Revision$;$Date$;$Author$;$URL$
 *
 *  @author Daniel Thomas (DTH)
 */
public interface CalculatingMachine {
	
	
	/** This method returns an approximation of PI.
	 * 
	 * @return an approximation of PI.
	 */
	public float getPI();

	/** This method takes the radius of a circle and computes its Area.
	 * 	
	 * @param r 
	 * 			Is the radius of the circle.
	 * @return Returns the area of the circle.
	 */
	public double getAreaOfCircle(double r);
	
	/** This method prints PI to the screen.
	 * 
	 */
	public void printPIToScreen();
	
}
