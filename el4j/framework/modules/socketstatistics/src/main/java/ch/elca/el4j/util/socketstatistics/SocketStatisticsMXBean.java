/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2009 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.util.socketstatistics;

import java.util.Set;

/**
 * MBean Interface to SocketStatistics. 
 * This MBean provides the possibility to monitor open / closed socket connections
 * and their properties.
 *
 * @author Jonas Hauenstein (JHN)
 */
public interface SocketStatisticsMXBean {

	/**
	 * Get number of already closed sockets.
	 * 
	 * @return number of closed sockets
	 */
	public long getClosedSocketsCount();

	/**
	 * Get number of open sockets.
	 * 
	 * @return number of open sockets
	 */
	public long getOpenSocketsCount();

	/**
	 * Get a Set of all present ConnectionStatics for open and already closed sockets.
	 * 
	 * @return set of all tracked ConnectionStatatics
	 */
	public Set<ConnectionStatistics> getConnectionStatistics();

	/**
	 * Getter for the keepStats property which defines how many seconds stats of closed sockets are kept.
	 * 
	 * @return how many seconds closed sockets are kept in statistics
	 */
	public int getKeepStats();

	/**
	 * Setter for the keepStats property which defines how many seconds stats of closed sockets are kept.
	 * 
	 * @param ks how many seconds closed sockets will be kept in statistics
	 */
	public void setKeepStats(int ks);

	/**
	 * Generate a csv of all gathered statistics and save it on the passed path/filename.
	 * 
	 * @param filepath
	 *            path and filename of the csv output
	 */
	public void exportStatisticsCSV(String filepath);

	/**
	 * Delete all gathered statistics.
	 */
	public void deleteStatistics();

}
