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

package com.openkm.frontend.client.widget.notify;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTUser;
import com.openkm.frontend.client.service.OKMAuthService;
import com.openkm.frontend.client.service.OKMAuthServiceAsync;
import com.openkm.frontend.client.util.OKMBundleResources;

import java.util.List;

/**
 * NotifyUser
 *
 * @author jllort
 */
public class NotifyUser extends Composite {
	private final OKMAuthServiceAsync authService = GWT.create(OKMAuthService.class);

	public static final int DEFAULT = 1;
	public static final int FILTER = 2;
	private HorizontalPanel hPanel;
	private UserScrollTable notifyUsersTable;
	private UserScrollTable userTable;
	private VerticalPanel buttonPanel;
	private Image addButton;
	private Image removeButton;
	private NotifyHandler notifyChange;
	private List<String> selectedUsers;

	/**
	 * NotifyUser
	 */
	public NotifyUser(NotifyHandler notifyChange) {
		this.notifyChange = notifyChange;
		hPanel = new HorizontalPanel();
		notifyUsersTable = new UserScrollTable(true);
		notifyUsersTable.addDoubleClickHandler(removeTableHandler);
		userTable = new UserScrollTable(false);
		userTable.addDoubleClickHandler(addTableHandler);

		buttonPanel = new VerticalPanel();
		addButton = new Image(OKMBundleResources.INSTANCE.add());
		removeButton = new Image(OKMBundleResources.INSTANCE.remove());

		HTML space = new HTML("");
		buttonPanel.add(addButton);
		buttonPanel.add(space); // separator
		buttonPanel.add(removeButton);

		buttonPanel.setCellHeight(space, "40px");

		addButton.addClickHandler(addButtonHandler);
		removeButton.addClickHandler(removeButtonHandler);
		addButton.setStyleName("okm-Hyperlink");
		removeButton.setStyleName("okm-Hyperlink");

		hPanel.setSize("374px", "140px");
		hPanel.add(userTable);
		hPanel.add(buttonPanel);
		hPanel.add(notifyUsersTable);
		hPanel.setCellVerticalAlignment(buttonPanel, VerticalPanel.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(buttonPanel, HorizontalPanel.ALIGN_CENTER);
		hPanel.setCellWidth(buttonPanel, "20px");

		userTable.addStyleName("okm-Border-Left");
		userTable.addStyleName("okm-Border-Right");
		userTable.addStyleName("okm-Border-Bottom");
		notifyUsersTable.addStyleName("okm-Border-Left");
		notifyUsersTable.addStyleName("okm-Border-Right");
		notifyUsersTable.addStyleName("okm-Border-Bottom");

		reset();

		initWidget(hPanel);
	}

	/**
	 * correcIEBug
	 */
	public void correcIEBug() {
		// TODO:Solves minor bug with IE ( UI defect extra size needed )
		hPanel.setCellWidth(buttonPanel, "25px");
	}

	/**
	 * reset
	 */
	public void reset() {
		notifyUsersTable.reset();
		userTable.reset();
	}

	/**
	 * resetAvailableUsersTable
	 */
	public void resetAvailableUsersTable() {
		userTable.reset();
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		notifyUsersTable.langRefresh();
		userTable.langRefresh();
	}

	/**
	 * Add button handler
	 */
	ClickHandler addButtonHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			addUser();
		}
	};

	/**
	 * Add Table handler
	 */
	DoubleClickHandler addTableHandler = new DoubleClickHandler() {
		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			addUser();
		}
	};

	/**
	 * Remove button handler
	 */
	ClickHandler removeButtonHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			removeUser();
		}
	};

	/**
	 * Remove Table handler
	 */
	DoubleClickHandler removeTableHandler = new DoubleClickHandler() {
		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			removeUser();
		}
	};

	/**
	 * addUser
	 */
	private void addUser() {
		if (userTable.getUser() != null) {
			notifyUsersTable.addRow(userTable.getUser());
			if (Main.get().mailEditorPopup.recipientsPopup.notifyPanel.users != null) {
				Main.get().mailEditorPopup.recipientsPopup.notifyPanel.users.add(userTable.getUser().getId());
			}
			notifyUsersTable.selectLastRow();
			userTable.removeSelectedRow();
			Main.get().fileUpload.disableErrorNotify(); // Used in both widgets
			Main.get().notifyPopup.disableErrorNotify(); // has no bad effect disabling both
			notifyChange.onChange();
		}
	}

	/**
	 * removeUser
	 */
	private void removeUser() {
		if (notifyUsersTable.getUser() != null) {
			userTable.addRow(notifyUsersTable.getUser());
			if (Main.get().mailEditorPopup.recipientsPopup.notifyPanel.users != null) {
				Main.get().mailEditorPopup.recipientsPopup.notifyPanel.users.remove(notifyUsersTable.getUser().getId());
			}
			userTable.selectLastRow();
			notifyUsersTable.removeSelectedRow();
			notifyChange.onChange();
		}
	}

	/**
	 * Call back get all users
	 */
	final AsyncCallback<List<GWTUser>> callbackAllUsers = new AsyncCallback<List<GWTUser>>() {
		public void onSuccess(List<GWTUser> result) {
			for (GWTUser user : result) {
				if (selectedUsers != null) {
					if (!selectedUsers.contains(user.getId())) {
						userTable.addRow(user);
					} else {
						notifyUsersTable.addRow(user);
					}
				} else {
					userTable.addRow(user);
				}
			}
			if (selectedUsers != null) {
				selectedUsers = null;
				notifyChange.onChange();
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetAllUsers", caught);
		}
	};

	/**
	 * Call back get all users
	 */
	final AsyncCallback<List<GWTUser>> callbackFilterUsers = new AsyncCallback<List<GWTUser>>() {
		public void onSuccess(List<GWTUser> result) {
			for (GWTUser user : result) {
				if (selectedUsers != null) {
					if (!selectedUsers.contains(user.getId())) {
						userTable.addRow(user);
					} else {
						notifyUsersTable.addRow(user);
					}
				} else {
					userTable.addRow(user);
				}
			}

			if (selectedUsers != null) {
				selectedUsers = null;
				notifyChange.onChange();
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetFilterUsers", caught);
		}
	};

	/**
	 * Gets all users
	 */
	public void getAllUsers(List<String> selectedUsers, int type) {
		this.selectedUsers = selectedUsers;
		switch (type) {
			case DEFAULT:
				authService.getAllUsers(callbackAllUsers);
				break;

			case FILTER:
				if (selectedUsers != null) {
					authService.getUsers(selectedUsers, callbackFilterUsers);
				}
				break;
		}
	}

	/**
	 * Gets the all users by filter
	 */
	public void getFilteredAllUsers(String filter) {
		authService.getFilteredAllUsers(filter, notifyUsersTable.getUsersToNotifyList(), callbackAllUsers);
	}

	/**
	 * getUsersToNotify
	 */
	public String getUsersToNotify() {
		return notifyUsersTable.getUsersToNotify();
	}
}
