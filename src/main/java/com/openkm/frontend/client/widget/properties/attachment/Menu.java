/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.frontend.client.widget.properties.attachment;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.ToolBarOption;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.toolbar.DocumentSecurityHelper;

/**
 * Search saved menu
 *
 * @author jllort
 */
public class Menu extends Composite {
	private boolean downloadOption = true;
	private boolean copyOption = true;
	private MenuBar attachMenu;
	private MenuItem download;
	private MenuItem copy;
	private ToolBarOption mainMenuOption;

	/**
	 * Browser menu
	 */
	public Menu() {
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected

		// First initialize language values
		attachMenu = new MenuBar(true);
		download = new MenuItem(Util.menuHTML("img/icon/actions/download.gif", Main.i18n("filebrowser.menu.download")), true, downloadFile);
		download.addStyleName("okm-MenuItem");
		attachMenu.addItem(download);
		copy = new MenuItem(Util.menuHTML("img/icon/actions/copy.gif", Main.i18n("general.menu.edit.copy")), true, copyAttachment);
		copy.addStyleName("okm-MenuItem");
		attachMenu.addItem(copy);
		attachMenu.addStyleName("okm-MenuBar");
		initWidget(attachMenu);
	}

	// Command menu to download attachement file
	Command downloadFile = new Command() {
		public void execute() {
			if (downloadOption && mainMenuOption.downloadOption) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mailViewer.downloadAttachment();
			}
			hide();
		}
	};

	// Command menu to refresh actual Directory
	Command copyAttachment = new Command() {
		public void execute() {
			if (copyOption) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mailViewer.copyAttachment();
			}
			hide();
		}
	};

	/**
	 * Refresh language values
	 */
	public void langRefresh() {
		download.setHTML(Util.menuHTML("img/icon/actions/download.gif", Main.i18n("filebrowser.menu.download")));
		copy.setHTML(Util.menuHTML("img/icon/actions/copy.gif", Main.i18n("general.menu.edit.copy")));
	}

	/**
	 * Hide popup menu
	 */
	public void hide() {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mailViewer.attachmentMenuPopup.hide();
	}

	/**
	 * Evaluates menu options
	 */
	public void evaluateMenuOptions() {
		if (downloadOption && mainMenuOption.downloadOption) {
			enable(download);
		} else {
			disable(download);
		}
		if (copyOption) {
			enable(copy);
		} else {
			disable(copy);
		}
	}

	/**
	 * set
	 */
	public void set(GWTDocument doc) {
		mainMenuOption = DocumentSecurityHelper.menuPopupEvaluation(new ToolBarOption(), doc);
		evaluateMenuOptions();
	}

	/**
	 * Enables menu item
	 *
	 * @param menuItem The menu item
	 */
	public void enable(MenuItem menuItem) {
		menuItem.addStyleName("okm-MenuItem");
		menuItem.removeStyleName("okm-MenuItem-strike");
	}

	/**
	 * Disable the menu item
	 *
	 * @param menuItem The menu item
	 */
	public void disable(MenuItem menuItem) {
		menuItem.removeStyleName("okm-MenuItem");
		menuItem.addStyleName("okm-MenuItem-strike");
	}
}
