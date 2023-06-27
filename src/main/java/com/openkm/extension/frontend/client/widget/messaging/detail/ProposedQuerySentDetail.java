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

package com.openkm.extension.frontend.client.widget.messaging.detail;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.bean.extension.GWTProposedQuerySent;
import com.openkm.frontend.client.constants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.SearchComunicator;
import com.openkm.frontend.client.extension.comunicator.WorkspaceComunicator;

/**
 * ProposedQuerySentDetail
 *
 * @author jllort
 *
 */
public class ProposedQuerySentDetail extends Composite {
	private ScrollPanel scrollPanel;
	private VerticalPanel vPanel;
	private FlexTable table;
	private HTML from = new HTML("");
	private HTML to = new HTML("");
	private HTML docType = new HTML("");
	private HTML date = new HTML("");
	private Anchor anchor = new Anchor("");
	private HTML content = new HTML("");

	/**
	 * Propose
	 */
	public ProposedQuerySentDetail() {
		table = new FlexTable();
		vPanel = new VerticalPanel();
		vPanel.add(table);
		vPanel.add(content);
		scrollPanel = new ScrollPanel(vPanel);

		table.setCellPadding(3);
		table.setCellSpacing(2);

		table.setHTML(0, 0, "<b>" + GeneralComunicator.i18nExtension("messaging.detail.from") + "</b>");
		table.setWidget(0, 1, from);
		table.setHTML(0, 2, "&nbsp;");
		table.setHTML(0, 3, "<b>" + GeneralComunicator.i18nExtension("messaging.detail.to") + "</b>");
		table.setWidget(0, 4, to);
		table.setHTML(0, 5, "");

		table.setHTML(1, 0, "<b>" + GeneralComunicator.i18nExtension("messaging.detail.type") + "</b>");
		table.setWidget(1, 1, docType);
		table.setHTML(1, 2, "&nbsp;");
		table.setHTML(1, 3, "<b>" + GeneralComunicator.i18nExtension("messaging.detail.date") + "</b>");
		table.setWidget(1, 4, date);

		table.setHTML(2, 0, "");
		table.setWidget(2, 1, anchor);
		table.setHTML(2, 2, "&nbsp;");
		table.setHTML(2, 3, "&nbsp;");

		table.getCellFormatter().setWidth(0, 5, "100%");
		table.getFlexCellFormatter().setColSpan(2, 3, 2);
		table.getFlexCellFormatter().setColSpan(3, 0, 6);

		// Sets wordWrap for all rows
		int cols[] = {6, 5, 4, 1};
		for (int row = 0; row < cols.length; row++) {
			for (int col = 0; col < cols[row]; col++) {
				setRowWordWarp(row, col, false, table);
			}
		}

		vPanel.setSize("100%", "100%");
		vPanel.setCellVerticalAlignment(table, HasAlignment.ALIGN_TOP);
		vPanel.setCellVerticalAlignment(content, HasAlignment.ALIGN_TOP);
		vPanel.setCellHeight(table, "70px");

		table.setStyleName("okm-Mail");
		from.addStyleName("okm-NoWrap");
		to.addStyleName("okm-NoWrap");
		docType.addStyleName("okm-NoWrap");
		date.addStyleName("okm-NoWrap");
		anchor.setStyleName("okm-Hyperlink");
		vPanel.setStyleName("okm-Mail-White");

		initWidget(scrollPanel);
	}

	/**
	 * set
	 *
	 * @param propose
	 */
	public void set(final GWTProposedQuerySent propose) {
		from.setHTML(propose.getFrom());
		to.setHTML(propose.getTo());
		String queryType = "";
		if (!propose.getParams().isDashboard()) {
			queryType = GeneralComunicator.i18nExtension("messaging.message.type.propose.query");
		} else {
			queryType = GeneralComunicator.i18nExtension("messaging.message.type.propose.user.query");
		}
		docType.setHTML(queryType);
		table.setHTML(2, 0, "<b>" + GeneralComunicator.i18nExtension("messaging.detail.query") + "</b>");
		DateTimeFormat dtf = DateTimeFormat.getFormat(GeneralComunicator.i18nExtension("general.date.pattern"));
		date.setHTML(dtf.format(propose.getSentDate()));

		Anchor anchor = new Anchor();
		String queryName = propose.getParams().getQueryName();
		anchor.setHTML(queryName);
		anchor.setTitle(queryName);
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				WorkspaceComunicator.changeSelectedTab(UIDockPanelConstants.SEARCH);
				SearchComunicator.setSavedSearch(propose.getParams());
			}
		});
		anchor.setStyleName("okm-KeyMap-ImageHover");
		Image runImage = new Image(OKMBundleResources.INSTANCE.run());
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(runImage);
		hPanel.add(anchor);
		hPanel.setCellVerticalAlignment(anchor, HasAlignment.ALIGN_MIDDLE);
		table.setWidget(2, 1, hPanel);

		content.setHTML(propose.getComment().replaceAll("\n", "</br>"));
	}


	/**
	 * Set the WordWarp for all the row cells
	 *
	 * @param row The row cell
	 * @param columns Number of row columns
	 * @param warp
	 * @param table The table to change word wrap
	 */
	private void setRowWordWarp(int row, int columns, boolean warp, FlexTable table) {
		CellFormatter cellFormatter = table.getCellFormatter();
		for (int i = 0; i < columns; i++) {
			cellFormatter.setWordWrap(row, i, warp);
		}
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		table.setHTML(0, 0, "<b>" + GeneralComunicator.i18nExtension("messaging.detail.from") + "</b>");
		table.setHTML(0, 3, "<b>" + GeneralComunicator.i18nExtension("messaging.detail.to") + "</b>");
		table.setHTML(1, 0, "<b>" + GeneralComunicator.i18nExtension("messaging.detail.type") + "</b>");
		table.setHTML(1, 3, "<b>" + GeneralComunicator.i18nExtension("messaging.detail.date") + "</b>");
		docType.setHTML(GeneralComunicator.i18nExtension("messaging.message.type.propose.query"));
		table.setHTML(2, 0, "<b>" + GeneralComunicator.i18nExtension("messaging.detail.query") + "</b>");
	}
}
