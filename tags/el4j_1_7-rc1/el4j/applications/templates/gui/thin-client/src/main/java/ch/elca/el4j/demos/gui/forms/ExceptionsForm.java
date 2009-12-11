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
import java.lang.reflect.InvocationTargetException;
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

import cookxml.cookswing.CookSwing;

import ch.elca.el4j.core.context.annotations.LazyInit;
import ch.elca.el4j.services.gui.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.services.gui.swing.GUIApplication;
import ch.elca.el4j.services.gui.swing.cookswing.binding.Bindable;
import ch.elca.el4j.services.gui.swing.exceptions.Exceptions;
import ch.elca.el4j.services.gui.swing.exceptions.Handler;
import ch.elca.el4j.services.gui.swing.frames.ApplicationFrame;
import ch.elca.el4j.services.gui.swing.frames.ApplicationFrameAware;

/**
 * This form shows all exceptions that occurred.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
@LazyInit
@Component("exceptionsForm")
public class ExceptionsForm extends JPanel implements Bindable, Handler, ApplicationFrameAware {
	/**
	 * The list of exception entries.
	 */
	protected List<ExceptionEntry> exceptions;
	/**
	 * The table showing the occurred exceptions.
	 */
	protected JTable exceptionsTable;
	/**
	 * A text area component to show the stacktrace of the selected exception.
	 */
	protected JTextArea stacktrace;
	
	/**
	 * The binder instance variable.
	 */
	protected final Binder binder = BinderManager.getBinder(this);
	
	/**
	 * The frame this component is embedded.
	 */
	private ApplicationFrame applicationFrame;
	
	/**
	 * This class represents an entry in the table.
	 */
	public class ExceptionEntry {
		/**
		 * The time when the exception occurred as String.
		 */
		private String time;
		
		/**
		 * The exception.
		 */
		private Exception exception;
		
		/**
		 * @param time         The time when the exception occurred as String
		 * @param exception    The exception
		 */
		public ExceptionEntry(String time, Exception exception) {
			this.time = time;
			this.exception = exception;
		}
		
		/**
		 * @return Returns the time.
		 */
		public String getTime() {
			return time;
		}
		/**
		 * @return Returns the exception.
		 */
		public Exception getException() {
			return exception;
		}
	}
	
	/**
	 * The constructor.
	 */
	public ExceptionsForm() {
		Exceptions.getInstance().addHandler(this);
		
		setLayout(new BorderLayout());
		
		exceptions = PropertyChangeListenerMixin.addPropertyChangeMixin(new ArrayList<ExceptionEntry>());
		
		CookSwing cookSwing = new CookSwing(this);
		cookSwing.render("gui/exceptionForm.xml");
		
		binder.bindAll();
		
		exceptionsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = exceptionsTable.rowAtPoint(e.getPoint());
				if (index >= 0) {
					updateStacktrace(exceptions.get(index).getException());
				}
			}
		});
		
		// make first column as small as possible but not smaller than 50 pixel (a bit hacky)
		exceptionsTable.getColumnModel().getColumn(0).setMinWidth(50);
		exceptionsTable.getColumnModel().getColumn(1).setPreferredWidth(5000);
		
		// adjust font size of stacktrace
		stacktrace.setFont(stacktrace.getFont().deriveFont(12.0f));
	}
	
	/**
	 * @param e    the exception to display
	 */
	private void updateStacktrace(Exception e) {
		stacktrace.setText(ExceptionUtils.getStackTrace(e));
		stacktrace.setCaretPosition(0);
	}
	
	/**
	 * Clear the exception list.
	 */
	@Action
	public void clearList() {
		exceptions.clear();
		stacktrace.setText("");
	}
	
	/**
	 * Close this form.
	 */
	@Action
	public void close() {
		applicationFrame.close();
	}
	
	/** {@inheritDoc} */
	public Binder getBinder() {
		return binder;
	}
	
	/** {@inheritDoc} */
	public void setApplicationFrame(ApplicationFrame applicationFrame) {
		this.applicationFrame = applicationFrame;
		applicationFrame.setMinimizable(true);
		applicationFrame.setMaximizable(true);
	}
	
	/** {@inheritDoc} */
	public boolean recognize(Exception e) {
		return (e != null);
	}
	
	/** {@inheritDoc} */
	public int getPriority() {
		return -100000;
	}
	
	/** {@inheritDoc} */
	public boolean handle(Exception e) {
		Exception actualException = e;
		// unwrap exception if necessary (Swing wraps Exceptions into InvocationTargetExceptions)
		if (e instanceof InvocationTargetException && e.getCause() instanceof Exception) {
			actualException = (Exception) e.getCause();
		}
		
		// ensure that this form is visible
		GUIApplication.getInstance().show("exceptionsForm");
		
		SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
		exceptions.add(0, new ExceptionEntry(fmt.format(new Date()), actualException));
		exceptionsTable.setRowSelectionInterval(exceptions.size() - 1, exceptions.size() - 1);
		updateStacktrace(actualException);
		
		return false;
	}
}
