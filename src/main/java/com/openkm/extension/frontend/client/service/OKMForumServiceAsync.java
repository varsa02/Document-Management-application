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

package com.openkm.extension.frontend.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.openkm.frontend.client.bean.extension.GWTForum;
import com.openkm.frontend.client.bean.extension.GWTForumPost;
import com.openkm.frontend.client.bean.extension.GWTForumTopic;

import java.util.List;

/**
 * OKMForumServiceAsync
 *
 * @author jllort
 */
public interface OKMForumServiceAsync extends RemoteService {
	void getTopicsByForum(long id, AsyncCallback<List<GWTForumTopic>> callback);

	void getTopicsByNode(String uuid, AsyncCallback<List<GWTForumTopic>> callback);

	void createTopic(long id, String uuid, GWTForumTopic topic, AsyncCallback<GWTForumTopic> callback);

	void findTopicByPK(long id, AsyncCallback<GWTForumTopic> callback);

	void createPost(long forumId, long postId, GWTForumPost post, AsyncCallback<?> callback);

	void increaseTopicView(long id, AsyncCallback<?> callback);

	void deletePost(long forumId, long topicId, long postId, AsyncCallback<Boolean> callback);

	void updatePost(GWTForumPost post, AsyncCallback<?> callback);

	void getAllForum(AsyncCallback<List<GWTForum>> callback);

	void createForum(GWTForum forum, AsyncCallback<GWTForum> callback);

	void deleteForum(long id, AsyncCallback<?> callback);

	void updateForum(GWTForum forum, AsyncCallback<?> callback);

	void updateTopic(long id, GWTForumPost post, AsyncCallback<?> callback);
}
