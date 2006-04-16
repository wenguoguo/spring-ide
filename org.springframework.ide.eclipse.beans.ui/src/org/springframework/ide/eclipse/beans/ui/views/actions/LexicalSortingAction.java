/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package org.springframework.ide.eclipse.beans.ui.views.actions;

import org.eclipse.jface.action.Action;
import org.springframework.ide.eclipse.beans.ui.BeansUIImages;
import org.springframework.ide.eclipse.beans.ui.BeansUIPlugin;
import org.springframework.ide.eclipse.beans.ui.views.BeansView;

/**
 * Toolbar action for activating / deactivating lexicaly sorting of entries in
 * the beans view.
 * @author Torsten Juergeleit
 */
public class LexicalSortingAction extends Action {

	private static final String PREFIX = "View.SortAction.";

	private BeansView view;

	public LexicalSortingAction(BeansView view) {
		this.view = view;
		setText(BeansUIPlugin.getResourceString(PREFIX + "label"));
		BeansUIImages.setLocalImageDescriptors(this, "alphab_sort_co.gif");
		boolean state = view.isSortingEnabled();
		setChecked(state);
		stateChanged(state);
	}

    public void run() {
		boolean state = isChecked();
		view.setSortingEnabled(state);
		stateChanged(state);
    }

    private void stateChanged(boolean state) {
        setToolTipText(state ?
			BeansUIPlugin.getResourceString(PREFIX + "tooltip.checked") :
			BeansUIPlugin.getResourceString(PREFIX + "tooltip.unchecked"));
        setDescription(state ?
			BeansUIPlugin.getResourceString(PREFIX + "description.checked") :
			BeansUIPlugin.getResourceString(PREFIX + "description.unchecked"));
    }
}
