package com.silvermindsoftware.hitch;

import com.silvermindsoftware.hitch.swing.EnhancedSpinnerListModel;
import com.silvermindsoftware.hitch.swing.EnhancedSpinnerListFormatter;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import java.util.List;

/**
 * Copyright 2007 Brandon Goodin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class HitchUtil {

	/**
	 * Use this when you are using a JSpinner with a list of complex objects. The
	 * standard ListFormatter fails to retain the value of the object in the
	 * JFormattedTextField when using auto-completion and a commitEdit is called.
	 * This method will enhance the JSpinner to work properly and retain the found
	 * object.
	 *
	 * @param items
	 * @return
	 */
	public static JSpinner getEnhancedJSpinner(List items) {

		EnhancedSpinnerListModel slm = new EnhancedSpinnerListModel(items);

		JSpinner spinner = new JSpinner(slm);

		// set the proper formatter on the spinner editor's JFormattedTextField
		((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setFormatterFactory(new
				DefaultFormatterFactory(new EnhancedSpinnerListFormatter(slm)));

		return spinner;
	}

	/**
	 * @see com.silvermindsoftware.hitch.HitchUtil#getEnhancedJSpinner(java.util.List) 
	 *
	 * @param items
	 * @return
	 */
	public static JSpinner getEnhancedJSpinner(Object[] items) {

		EnhancedSpinnerListModel slm = new EnhancedSpinnerListModel(items);

		JSpinner spinner = new JSpinner(slm);

		// set the proper formatter on the spinner editor's JFormattedTextField
		((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setFormatterFactory(new
				DefaultFormatterFactory(new EnhancedSpinnerListFormatter(slm)));

		return spinner;
	}


}
