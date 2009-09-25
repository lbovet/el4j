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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Keeps statistics for all sockets / from all socket connections.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Jonas Hauenstein (JHN)
 */
public class SocketStatistics implements SocketStatisticsMXBean {

	/**
	 * Config: How many seconds to keep the statistics of sockets. Default is 600 seconds = 10 minutes
	 */
	private static int s_keepStats = 600;

	/**
	 * Internal unique id counter for sockets.
	 */
	private static long s_socketIdCounter = 0;

	/**
	 * Number of already closed sockets.
	 */
	private static long s_nrofclosedsockets = 0;

	/**
	 * List of all connections sorted by destruction / closed date (first order). and creation date (second order, if
	 * not yet closed).
	 */
	private static SortedSet<ConnectionStatistics> s_statsbydate = new TreeSet<ConnectionStatistics>();

	/**
	 * Add a new ConnectionStatics for a socket.
	 * 
	 * @return the ConnectionsStatics added to the the set
	 */
	public static synchronized ConnectionStatistics addNewConStats() {
		// explicitly synchronize on list
		synchronized (s_statsbydate) {
			long i = getNextID();
			ConnectionStatistics cs = new ConnectionStatistics(i);
			s_statsbydate.add(cs);
			cleanupStats();
			return cs;
		}
	}

	/**
	 * Fetch next id for socket and increment internal counter.
	 * 
	 * @return next unique id
	 */
	private static synchronized long getNextID() {
		return ++s_socketIdCounter;
	}

	/** {@inheritDoc} */
	public long getClosedSocketsCount() {
		return s_nrofclosedsockets;
	}

	/** {@inheritDoc} */
	public long getOpenSocketsCount() {
		return s_socketIdCounter - s_nrofclosedsockets;
	}

	/** {@inheritDoc} */
	public Set<ConnectionStatistics> getConnectionStatistics() {
		return new TreeSet<ConnectionStatistics>(s_statsbydate);
	}

	/** {@inheritDoc} */
	public int getKeepStats() {
		return s_keepStats;
	}

	/** {@inheritDoc} */
	public void setKeepStats(int ks) {
		s_keepStats = ks;
	}

	/** {@inheritDoc} */
	public void exportStatisticsCSV(String filepath) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(filepath));
			out.write("Socket ID;Creation Date;Destruction Date;Remote Adress;Remote Port;Local Port;Bytes received;Bytes sent\n");
			synchronized (s_statsbydate) {
				for (ConnectionStatistics cs : s_statsbydate) {
					out.write(cs.getStatisticsCSV());
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/** {@inheritDoc} */
	public void deleteStatistics() {
		synchronized (s_statsbydate) {
			s_statsbydate.clear();
		}
	}

	/**
	 * Set a connection as destroyed / closed. Also sets the destruction date / close date of the connection to now
	 * 
	 * @param cs
	 *            the corresponding ConnectionStatics object
	 */
	public static synchronized void setConnectionDestroyed(ConnectionStatistics cs) {
		// explicitly synchronize on list
		synchronized (s_statsbydate) {
			// remove element from set
			s_statsbydate.remove(cs);
			// set the destruction date
			cs.setDestroyedDateInt();
			s_nrofclosedsockets++;
			// re-enter element in set (to force reordering)
			s_statsbydate.add(cs);
		}
	}

	/**
	 * Print statistics of all listed sockets to stdout.
	 */
	public void showSocketsStats() {
		cleanupStats();
		// explicitly synchronize on list
		synchronized (s_statsbydate) {
			if (s_statsbydate.isEmpty()) {
				System.out.println("No statistics available");
			} else {
				for (ConnectionStatistics cs : s_statsbydate) {
					System.out.println(cs.getStatistics());
				}
			}
		}
	}

	/**
	 * Get rid of the old ConnectionStatics. Purge those, which are closed longer ago then keepstats
	 */
	private static synchronized void cleanupStats() {

		// explicitly synchronize on list
		synchronized (s_statsbydate) {
			ConnectionStatistics cs;
			Date dd;
			long timecheck = new Date().getTime() - (s_keepStats * 1000);
			Iterator<ConnectionStatistics> i = s_statsbydate.iterator();
			// remove elements until one does not match the condition in the
			// sorted list
			while (i.hasNext()) {
				cs = i.next();
				dd = cs.getDestroyedDateInt();
				if (dd != null) {
					if (dd.getTime() < timecheck) {
						i.remove();
					} else {
						break;
					}
				} else {
					break;
				}
			}
		}

	}

}
