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

package com.openkm.hibernate.search;

import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Indexed(index="DocIndex")
@Table(name="OKM_DOCUMENT")
public class Document implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@DocumentId
	@Column(name="NDC_UUID", length=64)
	protected String uuid;

	@Column(name="NDC_NAME", length=256)
	@Field(index=Index.UN_TOKENIZED, store=Store.YES)
	protected String name;

	@ElementCollection
	@Column(name="NDK_KEYWORD")
	@CollectionTable(name="OKM_DOCUMENT_KEYWORD", joinColumns = { @JoinColumn( name = "NDK_DOCUMENT" ) })
	@Field(index=Index.UN_TOKENIZED, store=Store.YES)
	@FieldBridge(impl=SetFieldBridge.class)
	protected Set<String> keywords = new HashSet<>();

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("uuid="); sb.append(uuid);
		sb.append(", name="); sb.append(name);
		sb.append(", keywords="); sb.append(keywords);
		sb.append("}");
		return sb.toString();
	}
}
