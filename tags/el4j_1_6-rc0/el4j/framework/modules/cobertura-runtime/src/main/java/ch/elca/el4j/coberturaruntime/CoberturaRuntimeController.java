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
package ch.elca.el4j.coberturaruntime;

/**
 * Cobertura runtime controller interface.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public interface CoberturaRuntimeController {
	/**
	 * Starts recording of cobertura.
	 * 
	 * @return Returns <code>true</code> if the recording could be started and cobertura was not already running.
	 */
	public boolean startRecording();

	/**
	 * Stops recording of cobertura.
	 * 
	 * @return Returns <code>true</code> if the recording could be stopped and cobertura was not already stopped.
	 */
	public boolean stopRecording();

	/**
	 * @return Returns <code>true</code> if cobertura is currently recording.
	 */
	public boolean isRecording();

	/**
	 * Flushes the made records to the given data file.
	 */
	public void flushRecords();

	/**
	 * @return Returns the path to the cobertura data file.
	 */
	public String getDataFilePath();
	
	/**
	 * Generates the cobertura report of the current state.
	 * 
	 * @return Returns the directory path of the generated report.
	 */
	public String generateReport();
}