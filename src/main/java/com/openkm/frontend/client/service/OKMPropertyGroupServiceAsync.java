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

package com.openkm.frontend.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.bean.form.GWTFormElement;

import java.util.List;

/**
 * @author jllort
 *
 */
public interface OKMPropertyGroupServiceAsync {
	void getAllGroups(AsyncCallback<List<GWTPropertyGroup>> callback);

	void getAllGroups(String path, AsyncCallback<List<GWTPropertyGroup>> callback);

	void addGroup(String path, String grpName, AsyncCallback<?> callback);

	void getGroups(String path, AsyncCallback<List<GWTPropertyGroup>> callback);

	void getProperties(String path, String grpName, boolean suggestion, AsyncCallback<List<GWTFormElement>> callback);

	void setProperties(String path, String grpName, List<GWTFormElement> formProperties, AsyncCallback<?> callback);

	void removeGroup(String path, String grpName, AsyncCallback<?> callback);

	void getPropertyGroupForm(String grpName, AsyncCallback<List<GWTFormElement>> callback);

	void getPropertyGroupForm(String grpName, String path, boolean suggestion, AsyncCallback<List<GWTFormElement>> callback);
}
