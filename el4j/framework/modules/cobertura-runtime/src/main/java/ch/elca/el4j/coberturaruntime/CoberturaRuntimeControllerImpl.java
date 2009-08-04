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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.reporting.Main;
import net.sourceforge.cobertura.util.ConfigurationUtil;

/**
 * Bean to control the cobertura runtime behavior.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 */
public class CoberturaRuntimeControllerImpl implements CoberturaRuntimeController {
	/**
	 * Property key for the cobertura data directory.
	 */
	public static final String COBERTURA_RUNTIME_DATA_DIRECTORY = "cobertura-runtime.dataDirectory";
	
	/**
	 * Property key for the cobertura data filename.
	 */
	public static final String COBERTURA_RUNTIME_DATA_FILENAME = "cobertura-runtime.dataFilename";
	
	/**
	 * Property key for the cobertura data directory.
	 */
	public static final String COBERTURA_RUNTIME_SRC_COL_DIR_NAME = "cobertura-runtime.sourceCollectorDirectoryName";
	
	/**
	 * Property key for the cobertura keep reports flag.
	 */
	public static final String COBERTURA_RUNTIME_KEEP_REPORTS = "cobertura-runtime.keepReports";
	
	/**
	 * Is the cobertura data directory where all info will be saved.
	 */
	protected final File m_coberturaDataDirectory;
	
	/**
	 * Is the cobertura data filename.
	 */
	protected final String m_coberturaDataFilename;
	
	/**
	 * Is the directory where the collected sources are.
	 */
	protected final File m_coberturaCollectedSourcesDirectory;
	
	/**
	 * If <code>true</code> every report will have its own directory.
	 */
	protected final boolean m_keepReports;

	/**
	 * If <code>false</code> the controller is running in online mode.
	 */
	protected final boolean m_isOfflineMode;
	
	/**
	 * If <code>false</code> then cobertura is not recording data. Default is <code>true</code>.
	 */
	private boolean m_isRecordingData = true;
	
	/**
	 * Is the data file where cobertura data will be saved.
	 */
	private File m_coberturaDataFile;
	
	/**
	 * Constructor to init the cobertura data directory in online mode.
	 */
	public CoberturaRuntimeControllerImpl() {
		this(false);
	}
	
	/**
	 * Constructor to init the cobertura data directory.
	 * 
	 * @param offlineMode <code>true</code> if no app is running.
	 */
	public CoberturaRuntimeControllerImpl(boolean offlineMode) {
		ConfigurationUtil config = new ConfigurationUtil();
		
		// Load the data dir path
		String path = config.getProperty(COBERTURA_RUNTIME_DATA_DIRECTORY, null);
		if (!StringUtils.hasText(path)) {
			throw new RuntimeException("Property '" + COBERTURA_RUNTIME_DATA_DIRECTORY + "' is not defined! "
				+ "It should be defined in file 'cobertura.properties'!");
		}
		m_coberturaDataDirectory = new File(path);
		
		// Load the data filename
		String dataFilename = config.getProperty(COBERTURA_RUNTIME_DATA_FILENAME, null);
		if (!StringUtils.hasText(dataFilename)) {
			throw new RuntimeException("Property '" + COBERTURA_RUNTIME_DATA_FILENAME + "' is not defined! "
				+ "It should be defined in file 'cobertura.properties'!");
		}
		m_coberturaDataFilename = dataFilename;
		
		// Load the sourceCollectorDirectoryName
		String sourceDir = config.getProperty(COBERTURA_RUNTIME_SRC_COL_DIR_NAME, null);
		if (!StringUtils.hasText(sourceDir)) {
			throw new RuntimeException("Property '" + COBERTURA_RUNTIME_SRC_COL_DIR_NAME + "' is not defined! "
				+ "It should be defined in file 'cobertura.properties'!");
		}
		m_coberturaCollectedSourcesDirectory = new File(m_coberturaDataDirectory, sourceDir);
		
		// Load the keepReports
		String keepReportsString = config.getProperty(COBERTURA_RUNTIME_KEEP_REPORTS, null);
		if (!StringUtils.hasText(keepReportsString)) {
			throw new RuntimeException("Property '" + COBERTURA_RUNTIME_KEEP_REPORTS
				+ "' (boolean value) is not defined! "
				+ "It should be defined in file 'cobertura.properties'!");
		}
		m_keepReports = Boolean.parseBoolean(keepReportsString);
		
		// Set the cobertura data file
		m_isOfflineMode = offlineMode;
		m_coberturaDataFile = new File(m_coberturaDataDirectory, m_coberturaDataFilename);
		if (m_isOfflineMode) {
			m_isRecordingData = false;
			CoverageDataFileHandler.loadCoverageData(m_coberturaDataFile);
		} else {
			File defaultDataFile = CoverageDataFileHandler.getDefaultDataFile();
			if (!defaultDataFile.equals(m_coberturaDataFile)) {
				throw new RuntimeException("The configured '" + m_coberturaDataFile.getAbsolutePath() 
					+ "' and the already set cobertura data file path '" + defaultDataFile.getAbsolutePath()
					+ "' are not the same!");
			}
		}
		
	}
	
	/** {@inheritDoc} */
	public synchronized boolean startRecording() {
		if (m_isOfflineMode || m_isRecordingData) {
			return false;
		}
		
		flushRecords();
		try {
			Thread.sleep(1000);
			ProjectData coverageData = CoverageDataFileHandler.loadCoverageData(m_coberturaDataFile);
			setGlobalCoverageDataAndDataFile(coverageData, m_coberturaDataFile);
		} catch (Exception e) {
			throw new RuntimeException("Exception while trying to start recording!", e);
		}
		
		m_isRecordingData = true;
		return true;
	}
	
	/** {@inheritDoc} */
	public synchronized boolean stopRecording() {
		if (m_isOfflineMode || !m_isRecordingData) {
			return false;
		}
		
		flushRecords();
		try {
			Thread.sleep(1000);
			File tempDataFile = File.createTempFile("cobertura-temp-", ".ser");
			FileCopyUtils.copy(m_coberturaDataFile, tempDataFile);
			tempDataFile.deleteOnExit();
			ProjectData coverageData = CoverageDataFileHandler.loadCoverageData(tempDataFile);
			setGlobalCoverageDataAndDataFile(coverageData, tempDataFile);
		} catch (Exception e) {
			throw new RuntimeException("Exception while trying to stop recording!", e);
		}
		
		m_isRecordingData = false;
		return true;
	}
	
	/**
	 * Sets the global coverage data and the data file via reflection, due this point is really 
	 * poorly made in cobertura.
	 * 
	 * @param coverageData Is the loaded cobertura data.
	 * @param coberturaDataFile Is the file reference of the cobertura data.
	 */
	protected synchronized void setGlobalCoverageDataAndDataFile(ProjectData coverageData, File coberturaDataFile) {
//		try {
//			Method globalProjectDataSetter = ProjectData.class.getDeclaredMethod(
//				"setGlobalProjectData", ProjectData.class);
//			globalProjectDataSetter.invoke(null, coverageData);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("Exception while trying to set the global coverage data!", e);
//		}
		ProjectData.setGlobalProjectData(coverageData);
		
//		try {
//			Method defaultDataFileSetter = CoverageDataFileHandler.class.getDeclaredMethod(
//				"setDefaultDataFile", File.class);
//			defaultDataFileSetter.invoke(null, coberturaDataFile);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("Exception while trying to set the default coverage data file!", e);
//		}
		CoverageDataFileHandler.setDefaultDataFile(coberturaDataFile);
	}

	/** {@inheritDoc} */
	public synchronized boolean isRecording() {
		if (m_isOfflineMode) {
			return false;
		}
		return m_isRecordingData;
	}
	
	/** {@inheritDoc} */
	public synchronized void flushRecords() {
		if (m_isOfflineMode) {
			return;
		}
		ProjectData.saveGlobalProjectData();
	}

	/** {@inheritDoc} */
	public synchronized String getDataFilePath() {
		return m_coberturaDataFile != null ? m_coberturaDataFile.getAbsolutePath() : "UNKNOWN!";
	}

	/** {@inheritDoc} */
	public synchronized String generateReport() {
		// First flush the records
		flushRecords();
		
		// Create timestamp string
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		String timestampString = sdf.format(new Date());
		
		// Create a new directory for the new report
		// If the dir already exists delete it first
		String reportDirectoryName;
		if (m_keepReports) {
			reportDirectoryName = "report-" + timestampString;
		} else {
			reportDirectoryName = "report";
		}
		File reportDirectory = new File(m_coberturaDataDirectory, reportDirectoryName);
		String reportDirectoryPath = reportDirectory.getAbsolutePath();
		if (reportDirectory.exists()) {
			try {
				FileUtils.deleteDirectory(reportDirectory);
			} catch (IOException e) {
				new RuntimeException("Could not delete the old report directory '" + reportDirectoryPath + "'!", e);
			}
		}
		reportDirectory.mkdirs();
		
		// Copy the data file to the report dir
		String newDataFileName = "cobertura-" + timestampString + ".ser";
		File dataFile = new File(reportDirectory, newDataFileName);
		try {
			FileCopyUtils.copy(m_coberturaDataFile, dataFile);
		} catch (IOException e) {
			new RuntimeException("Could not copy the cobertura data file!", e);
		}
		String dataFilePath = dataFile.getAbsolutePath();
		
		// Generate the report
		List<String> arguments = new ArrayList<String>();
		arguments.add("--datafile");
		arguments.add(dataFilePath);
		arguments.add("--destination");
		arguments.add(reportDirectoryPath);
		arguments.add(m_coberturaCollectedSourcesDirectory.getAbsolutePath());
		try {
			Main.main(arguments.toArray(new String[arguments.size()]));
		} catch (Exception e) {
			new RuntimeException("Exception while generating the report!", e);
		}
		
		return reportDirectoryPath;
	}
}
