/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.module.db.base;

import com.openkm.automation.AutomationException;
import com.openkm.automation.AutomationManager;
import com.openkm.automation.AutomationUtils;
import com.openkm.bean.*;
import com.openkm.core.Config;
import com.openkm.core.*;
import com.openkm.dao.*;
import com.openkm.dao.bean.*;
import com.openkm.extension.dao.WikiPageDAO;
import com.openkm.extension.dao.bean.WikiPage;
import com.openkm.module.db.stuff.DbAccessManager;
import com.openkm.module.db.stuff.SecurityHelper;
import com.openkm.util.CloneUtils;
import com.openkm.util.SystemProfiling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class BaseMailModule {
	private static final Logger log = LoggerFactory.getLogger(BaseMailModule.class);

	/**
	 * Create a new mail
	 */
	@SuppressWarnings("unchecked")
	public static NodeMail create(String user, String parentPath, NodeFolder parentFolder, String name, long size, String from,
			String[] reply, String[] to, String[] cc, String[] bcc, Calendar sentDate, Calendar receivedDate, String subject,
			String content, String mimeType, Set<String> keywords, Set<String> categories, Set<NodeProperty> propertyGroups,
			List<NodeNote> notes, WikiPage wiki, Ref<FileUploadResponse> fuResponse) throws PathNotFoundException,
			AccessDeniedException, ItemExistsException, AutomationException, DatabaseException {
		// AUTOMATION - PRE
		Map<String, Object> env = new HashMap<>();
		env.put(AutomationUtils.PARENT_UUID, parentFolder.getUuid());
		env.put(AutomationUtils.PARENT_PATH, parentPath);
		env.put(AutomationUtils.PARENT_NODE, parentFolder);
		env.put(AutomationUtils.MAIL_NAME, name);
		env.put(AutomationUtils.MAIL_MIME_TYPE, mimeType);
		env.put(AutomationUtils.MAIL_KEYWORDS, keywords);

		AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_MAIL_CREATE, AutomationRule.AT_PRE, env);
		parentFolder = (NodeFolder) env.get(AutomationUtils.PARENT_NODE);
		name = (String) env.get(AutomationUtils.MAIL_NAME);
		mimeType = (String) env.get(AutomationUtils.MAIL_MIME_TYPE);
		keywords = (Set<String>) env.get(AutomationUtils.MAIL_KEYWORDS);

		// Create and add a new folder node
		NodeMail mailNode = new NodeMail();
		mailNode.setUuid(UUID.randomUUID().toString());
		mailNode.setContext(parentFolder.getContext());
		mailNode.setParent(parentFolder.getUuid());
		mailNode.setAuthor(user);
		mailNode.setName(name);
		mailNode.setSize(size);
		mailNode.setFrom(from);
		mailNode.setReply(new HashSet<>(Arrays.asList(reply)));
		mailNode.setTo(new HashSet<>(Arrays.asList(to)));
		mailNode.setCc(new HashSet<>(Arrays.asList(cc)));
		mailNode.setBcc(new HashSet<>(Arrays.asList(bcc)));
		mailNode.setSentDate(sentDate);
		mailNode.setReceivedDate(receivedDate);
		mailNode.setSubject(subject);
		mailNode.setContent(content);
		mailNode.setMimeType(mimeType);
		mailNode.setCreated(Calendar.getInstance());

		if (Config.STORE_NODE_PATH) {
			mailNode.setPath(parentFolder.getPath() + "/" + name);
		}

		// Extended Copy Attributes
		mailNode.setKeywords(CloneUtils.clone(keywords));
		mailNode.setCategories(CloneUtils.clone(categories));

		for (NodeProperty nProp : propertyGroups) {
			NodeProperty nPropClone = new NodeProperty();
			nPropClone.setNode(mailNode);
			nPropClone.setName(nProp.getName());
			nPropClone.setGroup(nProp.getGroup());
			nPropClone.setValue(nProp.getValue());
			mailNode.getProperties().add(nPropClone);
		}

		// Get parent node auth info
		Map<String, Integer> userPerms = parentFolder.getUserPermissions();
		Map<String, Integer> rolePerms = parentFolder.getRolePermissions();

		// Always assign all grants to creator
		if (Config.USER_ASSIGN_DOCUMENT_CREATION) {
			int allGrants = Permission.ALL_GRANTS;

			if ((Config.SECURITY_EXTENDED_MASK & Permission.PROPERTY_GROUP) == Permission.PROPERTY_GROUP) {
				allGrants = allGrants | Permission.PROPERTY_GROUP;
			}

			if ((Config.SECURITY_EXTENDED_MASK & Permission.COMPACT_HISTORY) == Permission.COMPACT_HISTORY) {
				allGrants = allGrants | Permission.COMPACT_HISTORY;
			}

			if ((Config.SECURITY_EXTENDED_MASK & Permission.START_WORKFLOW) == Permission.START_WORKFLOW) {
				allGrants = allGrants | Permission.START_WORKFLOW;
			}

			if ((Config.SECURITY_EXTENDED_MASK & Permission.DOWNLOAD) == Permission.DOWNLOAD) {
				allGrants = allGrants | Permission.DOWNLOAD;
			}

			userPerms.put(user, allGrants);
		}

		// Set auth info
		// NOTICE: Pay attention to the need of cloning
		mailNode.setUserPermissions(CloneUtils.clone(userPerms));
		mailNode.setRolePermissions(CloneUtils.clone(rolePerms));

		NodeMailDAO.getInstance().create(mailNode);

		// Extended Copy Attributes
		for (NodeNote nNote : CloneUtils.clone(notes)) {
			BaseNoteModule.create(mailNode.getUuid(), nNote.getAuthor(), nNote.getText());
		}

		if (wiki != null) {
			wiki.setNode(mailNode.getUuid());
			wiki.setDate(Calendar.getInstance());
			wiki.setLockUser(null);
			wiki.setDeleted(false);
			WikiPageDAO.create(wiki);
		}

		// AUTOMATION - POST
		env.put(AutomationUtils.MAIL_NODE, mailNode);
		AutomationManager.getInstance().fireEvent(AutomationRule.EVENT_MAIL_CREATE, AutomationRule.AT_POST, env);

		if (Config.USER_ITEM_CACHE) {
			// Update user items size
			// UserItemsManager.incFolders(user, 1);
		}

		// Setting wizard properties
		fuResponse.set((FileUploadResponse) env.get(AutomationUtils.UPLOAD_RESPONSE));

		return mailNode;
	}

	/**
	 * Get folder properties
	 */
	public static Mail getProperties(String user, NodeMail nMail) throws PathNotFoundException, DatabaseException {
		log.debug("getProperties({}, {})", user, nMail);
		long begin = System.currentTimeMillis();
		Mail mail = new Mail();

		// Properties
		String mailPath = NodeBaseDAO.getInstance().getPathFromUuid(nMail.getUuid());
		mail.setPath(mailPath);
		mail.setCreated(nMail.getCreated());
		mail.setAuthor(nMail.getAuthor());
		mail.setUuid(nMail.getUuid());
		mail.setSize(nMail.getSize());
		mail.setHasAttachments(nMail.getHasAttachments());
		mail.setFrom(nMail.getFrom());
		mail.setReply(nMail.getReply().toArray(new String[nMail.getReply().size()]));
		mail.setTo(nMail.getTo().toArray(new String[nMail.getTo().size()]));
		mail.setCc(nMail.getCc().toArray(new String[nMail.getCc().size()]));
		mail.setBcc(nMail.getBcc().toArray(new String[nMail.getBcc().size()]));
		mail.setSentDate(nMail.getSentDate());
		mail.setReceivedDate(nMail.getReceivedDate());
		mail.setSubject(nMail.getSubject());
		mail.setContent(nMail.getContent());
		mail.setMimeType(nMail.getMimeType());

		// Get permissions
		BaseModule.setPermissions(nMail, mail);

		// Get user subscription & keywords
		// mail.setSubscriptors(nMail.getSubscriptors());
		// mail.setSubscribed(nMail.getSubscriptors().contains(user));
		mail.setKeywords(nMail.getKeywords());

		// Get categories
		Set<Folder> categories = new HashSet<>();
		NodeFolderDAO nFldDao = NodeFolderDAO.getInstance();
		Set<NodeFolder> resolvedCategories = nFldDao.resolveCategories(nMail.getCategories());

		for (NodeFolder nfldCat : resolvedCategories) {
			categories.add(BaseFolderModule.getProperties(user, nfldCat));
		}

		mail.setCategories(categories);

		// Get notes
		List<Note> notes = new ArrayList<>();
		List<NodeNote> nNoteList = NodeNoteDAO.getInstance().findByParent(nMail.getUuid());

		for (NodeNote nNote : nNoteList) {
			notes.add(BaseNoteModule.getProperties(nNote, mailPath + "/" + nNote.getUuid()));
		}

		mail.setNotes(notes);

		SystemProfiling.log(user + ", " + nMail, System.currentTimeMillis() - begin);
		log.trace("getProperties.Time: {}", System.currentTimeMillis() - begin);
		log.debug("getProperties: {}", mail);
		return mail;
	}

	/**
	 * Is invoked from DbMailNode and DbFolderNode.
	 */
	public static NodeMail copy(String user, NodeMail srcMailNode, String dstPath, NodeFolder dstFldNode, ExtendedAttributes extAttr)
			throws ItemExistsException, UserQuotaExceededException, PathNotFoundException, AccessDeniedException, AutomationException,
			DatabaseException, IOException {
		log.debug("copy({}, {}, {}, {})", user, srcMailNode, dstFldNode, extAttr);
		NodeMail newMail = null;

		try {
			String[] reply = srcMailNode.getReply().toArray(new String[0]);
			String[] to = srcMailNode.getTo().toArray(new String[0]);
			String[] cc = srcMailNode.getCc().toArray(new String[0]);
			String[] bcc = srcMailNode.getBcc().toArray(new String[0]);

			Set<String> keywords = new HashSet<>();
			Set<String> categories = new HashSet<>();
			Set<NodeProperty> propertyGroups = new HashSet<>();
			List<NodeNote> notes = new ArrayList<>();
			WikiPage wiki = null;

			if (extAttr != null) {
				if (extAttr.isKeywords()) {
					keywords = srcMailNode.getKeywords();
				}

				if (extAttr.isCategories()) {
					categories = srcMailNode.getCategories();
				}

				if (extAttr.isPropertyGroups()) {
					propertyGroups = srcMailNode.getProperties();
				}

				if (extAttr.isNotes()) {
					notes = NodeNoteDAO.getInstance().findByParent(srcMailNode.getUuid());
				}

				if (extAttr.isWiki()) {
					wiki = WikiPageDAO.findLatestByNode(srcMailNode.getUuid());
				}
			}

			Ref<FileUploadResponse> fuResponse = new Ref<>(new FileUploadResponse());
			newMail = create(user, dstPath, dstFldNode, srcMailNode.getName(), srcMailNode.getSize(), srcMailNode.getFrom(), reply, to, cc,
					bcc, srcMailNode.getSentDate(), srcMailNode.getReceivedDate(), srcMailNode.getSubject(), srcMailNode.getContent(),
					srcMailNode.getMimeType(), keywords, categories, propertyGroups, notes, wiki, fuResponse);

			// Add attachments
			for (NodeDocument nDocument : NodeDocumentDAO.getInstance().findByParent(srcMailNode.getUuid())) {
				String newPath = NodeBaseDAO.getInstance().getPathFromUuid(newMail.getUuid());
				BaseDocumentModule.copy(user, nDocument, newPath, newMail, nDocument.getName(), extAttr);
			}
		} finally {
		}

		log.debug("copy: {}", newMail);
		return newMail;
	}

	/**
	 * Check recursively if the mail contains locked nodes
	 */
	public static boolean hasLockedNodes(String mailUuid) throws PathNotFoundException, DatabaseException {
		boolean hasLock = false;

		for (NodeDocument nDoc : NodeDocumentDAO.getInstance().findByParent(mailUuid)) {
			hasLock |= nDoc.isLocked();
		}

		return hasLock;
	}

	/**
	 * Check if a node has removable childs TODO: Is this necessary? The access manager should prevent this and make the
	 * core thrown an exception.
	 */
	public static boolean hasWriteAccess(String mailUuid) throws PathNotFoundException, DatabaseException {
		log.debug("hasWriteAccess({})", mailUuid);
		DbAccessManager am = SecurityHelper.getAccessManager();
		boolean canWrite = true;

		for (NodeDocument nDoc : NodeDocumentDAO.getInstance().findByParent(mailUuid)) {
			canWrite &= am.isGranted(nDoc, Permission.WRITE);
		}

		log.debug("hasWriteAccess: {}", canWrite);
		return canWrite;
	}
}
