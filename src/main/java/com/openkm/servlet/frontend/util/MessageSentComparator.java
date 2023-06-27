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

package com.openkm.servlet.frontend.util;

import com.openkm.frontend.client.bean.extension.GWTMessageSent;

import java.util.Comparator;

/**
 * MessageSentComparator
 *
 * @author jllort
 */
public class MessageSentComparator implements Comparator<GWTMessageSent> {
	private static final Comparator<GWTMessageSent> INSTANCE = new MessageSentComparator();

	public static Comparator<GWTMessageSent> getInstance() {
		return INSTANCE;
	}

	public int compare(GWTMessageSent first, GWTMessageSent second) {
		return first.getSentDate().compareTo(second.getSentDate());
	}
}
