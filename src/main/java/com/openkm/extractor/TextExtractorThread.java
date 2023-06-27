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

package com.openkm.extractor;

import com.openkm.core.DatabaseException;
import com.openkm.dao.NodeDocumentDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

/**
 * @author pavila
 */
public class TextExtractorThread implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(TextExtractorThread.class);
	private static volatile long global = 1;
	private TextExtractorWork work;
	private long id = 0;

	public TextExtractorThread(TextExtractorWork work) {
		this.id = global++;
		this.work = work;
	}

	@Override
	public void run() {
		try {
			log.debug("processConcurrent.Working {} on {}", id, work);
			NodeDocumentDAO.getInstance().textExtractorHelper(work);
			log.debug("processConcurrent.Finish {} on {}", id, work);
		} catch (FileNotFoundException | DatabaseException e) {
			log.warn(e.getMessage(), e);
		}
	}
}
