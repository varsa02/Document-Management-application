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

package com.openkm.extension.frontend.client.widget.wiki.finddocument;

import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.extension.frontend.client.widget.wiki.Wiki;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;


/**
 * Status
 *
 * @author jllort
 *
 */
public class Status extends PopupPanel {
	private HorizontalPanel hPanel;
	private HTML msg;
	private HTML space;
	private Image image;
	private boolean flag_getChilds = false;

	/**
	 * Status
	 */
	public Status() {
		super(false, true);
		hPanel = new HorizontalPanel();
		image = new Image(OKMBundleResources.INSTANCE.indicator());
		msg = new HTML("");
		space = new HTML("");

		hPanel.add(image);
		hPanel.add(msg);
		hPanel.add(space);

		hPanel.setCellVerticalAlignment(image, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(msg, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(image, HasAlignment.ALIGN_CENTER);
		hPanel.setCellWidth(image, "30px");
		hPanel.setCellWidth(space, "7px");

		hPanel.setHeight("25px");

		msg.setStyleName("okm-NoWrap");

		super.hide();
		setWidget(hPanel);
	}

	/**
	 * Refreshing satus
	 */
	public void refresh() {
		if (flag_getChilds) {
			int left = ((Wiki.get().findDocumentSelectPopup.getOffsetWidth() - 200) / 2) +
					Wiki.get().findDocumentSelectPopup.getAbsoluteLeft();
			int top = ((Wiki.get().findDocumentSelectPopup.getOffsetHeight() - 40) / 2) +
					Wiki.get().findDocumentSelectPopup.getAbsoluteTop();
			setPopupPosition(left, top);
			Wiki.get().findDocumentSelectPopup.scrollDocumentPanel.addStyleName("okm-PanelRefreshing");
			super.show();
		} else {
			super.hide();
			Wiki.get().findDocumentSelectPopup.scrollDocumentPanel.removeStyleName("okm-PanelRefreshing");
		}
	}

	/**
	 * Set childs flag
	 */
	public void setFlagChilds() {
		msg.setHTML(GeneralComunicator.i18n("filebrowser.status.refresh.document"));
		flag_getChilds = true;
		refresh();
	}

	/**
	 * Unset childs flag
	 */
	public void unsetFlagChilds() {
		flag_getChilds = false;
		refresh();
	}
}
