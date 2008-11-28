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
package ch.elca.el4j.demos.gui.forms;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.jdesktop.application.Action;
import org.springframework.stereotype.Component;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;

import ch.elca.el4j.core.context.annotations.LazyInit;
import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.gui.swing.cookswing.binding.Bindable;
import ch.elca.el4j.gui.swing.exceptions.Exceptions;
import ch.elca.el4j.gui.swing.exceptions.Handler;
import ch.elca.el4j.gui.swing.frames.ApplicationFrame;
import ch.elca.el4j.gui.swing.frames.ApplicationFrameAware;
import ch.elca.el4j.model.mixin.PropertyChangeListenerMixin;

import cookxml.cookswing.CookSwing;

/**
 * This form shows all exceptions that occurred.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
@LazyInit
@Component("exceptionsForm")
public class ExceptionsForm extends JPanel implements Bindable, Handler, ApplicationFrameAware {
	/**
	 * The list of exception entries.
	 */
	protected List<ExceptionEntry> m_exceptions;
	/**
	 * The table showing the occurred exceptions.
	 */
	protected JTable m_exceptionsTable;
	/**
	 * A text area component to show the stacktrace of the selected exception.
	 */
	protected JTextArea m_stacktrace;
	
	/**
	 * The binder instance variable.
	 */
	protected final Binder m_binder = BinderManager.getBinder(this);
	
	/**
	 * The frame this component is embedded.
	 */
	private ApplicationFrame m_applicationFrame;
	
	/**
	 * This class represents an entry in the table.
	 */
	public class ExceptionEntry {
		/**
		 * The time when the exception occurred as String.
		 */
		private String m_time;
		
		/**
		 * The exception.
		 */
		private Exception m_exception;
		
		/**
		 * @param time         The time when the exception occurred as String
		 * @param exception    The exception
		 */
		public ExceptionEntry(String time, Exception exception) {
			m_time = time;
			m_exception = exception;
		}
		
		/**
		 * @return Returns the time.
		 */
		public String getTime() {
			return m_time;
		}
		/**
		 * @return Returns the exception.
		 */
		public Exception getException() {
			return m_exception;
		}
	}
	
	/**
	 * The constructor.
	 */
	public ExceptionsForm() {
		Exceptions.getInstance().addHandler(this);
		
		setLayout(new BorderLayout());
		
		m_exceptions = PropertyChangeListenerMixin.addPropertyChangeMixin(new ArrayList<ExceptionEntry>());
		
		CookSwing cookSwing = new CookSwing(this);
		cookSwing.render("gui/exceptionForm.xml");
		
		m_binder.bindAll();
		
		m_exceptionsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = m_exceptionsTable.rowAtPoint(e.getPoint());
				if (index >= 0) {
					updateStacktrace(m_exceptions.get(index).getException());
				}
			}
		});
		
		// make first column as small as possible but not smaller than 50 pixel (a bit hacky)
		m_exceptionsTable.getColumnModel().getColumn(0).setMinWidth(50);
		m_exceptionsTable.getColumnModel().getColumn(1).setPreferredWidth(5000);
		
		// adjust font size of stacktrace
		m_stacktrace.setFont(m_stacktrace.getFont().deriveFont(12.0f));
	}
	
	/**
	 * @param e    the exception to display
	 */
	private void updateStacktrace(Exception e) {
		m_stacktrace.setText(ExceptionUtils.getStackTrace(e));
		m_stacktrace.setCaretPosition(0);
	}
	
	/**
	 * Clear the exception list.
	 */
	@Action
	public void clearList() {
		m_exceptions.clear();
		m_stacktrace.setText("");
	}
	
	/**
	 * Close this form.
	 */
	@Action
	public void close() {
		m_applicationFrame.close();
	}
	
	/** {@inheritDoc} */
	public Binder getBinder() {
		return m_binder;
	}
	
	/** {@inheritDoc} */
	public void setApplicationFrame(ApplicationFrame applicationFrame) {
		m_applicationFrame = applicationFrame;
		m_applicationFrame.setMinimizable(true);
		m_applicationFrame.setMaximizable(true);
	}
	
	/** {@inheritDoc} */
	public boolean recognize(Exception e) {
		return (e instanceof Exception);
	}
	
	/** {@inheritDoc} */
	public void handle(Exception e) {
		// ensure that this form is visible
		GUIApplication.getInstance().show("exceptionsForm");
		
		SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
		m_exceptions.add(0, new ExceptionEntry(fmt.format(new Date()), e));
		m_exceptionsTable.setRowSelectionInterval(m_exceptions.size() - 1, m_exceptions.size() - 1);
		updateStacktrace(e);
	}
}
