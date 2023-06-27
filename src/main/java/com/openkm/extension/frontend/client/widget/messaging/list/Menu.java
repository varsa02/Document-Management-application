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

package com.openkm.extension.frontend.client.widget.messaging.list;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.extension.frontend.client.widget.messaging.ConfirmPopup;
import com.openkm.extension.frontend.client.widget.messaging.MessagingToolBarBox;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.UtilComunicator;

/**
 * Search result menu
 *
 * @author jllort
 */
public class Menu extends Composite {
	private boolean deleteOption = true;
	private MenuBar menu;
	private MenuItem delete;

	/**
	 * Browser menu
	 */
	public Menu() {
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected

		// First initialize language values
		menu = new MenuBar(true);

		delete = new MenuItem(UtilComunicator.menuHTML("img/icon/actions/delete.png", GeneralComunicator.i18nExtension("messaging.message.delete")), true, deletePropose);
		delete.addStyleName("okm-MenuItem");
		menu.addItem(delete);
		menu.setStyleName("okm-MenuBar");
		initWidget(menu);
	}

	// Command menu to delete file
	Command deletePropose = new Command() {
		public void execute() {
			if (deleteOption) {
				if (MessagingToolBarBox.get().messageDashboard.messageStack.isProposedSubscriptionReceivedVisible() &&
						MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.isProposedSubscriptionReceivedSelected()) {
					MessagingToolBarBox.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_PROPOSED_SUBSCRIPTION_RECEIVED);
					MessagingToolBarBox.get().confirmPopup.show();

				} else if (MessagingToolBarBox.get().messageDashboard.messageStack.isProposedQueryReceivedVisible() &&
						MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.isProposedQueryReceivedSelected()) {
					MessagingToolBarBox.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_PROPOSED_QUERY_RECEIVED);
					MessagingToolBarBox.get().confirmPopup.show();

				} else if (MessagingToolBarBox.get().messageDashboard.messageStack.isMessagesSentVisible()) {
					if (MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.isMessageSentSelected()) {
						MessagingToolBarBox.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_MESSAGE_SENT);
					} else if (MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.isProposedQuerySentSelected()) {
						MessagingToolBarBox.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_PROPOSED_QUERY_SENT);
					} else if (MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.isProposedSubscriptionSentSelected()) {
						MessagingToolBarBox.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_PROPOSED_SUBSCRIPTION_SENT);
					}
					MessagingToolBarBox.get().confirmPopup.show();

				} else if (MessagingToolBarBox.get().messageDashboard.messageStack.isMessagesReceivedVisible() &&
						MessagingToolBarBox.get().messageDashboard.messageBrowser.message.table.isMessageReceivedSelected()) {
					MessagingToolBarBox.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_MESSAGE_RECEIVED);
					MessagingToolBarBox.get().confirmPopup.show();
				}
			}
			hide();
		}
	};

	/**
	 *  Refresh language values
	 */
	public void langRefresh() {
		delete.setHTML(UtilComunicator.menuHTML("img/icon/actions/delete.png", GeneralComunicator.i18nExtension("messaging.message.delete")));
	}

	/**
	 * Hide popup menu
	 */
	public void hide() {
		MessagingToolBarBox.get().messageDashboard.messageBrowser.message.menuPopup.hide();
	}
}
