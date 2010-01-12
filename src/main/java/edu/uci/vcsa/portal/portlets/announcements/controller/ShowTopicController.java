/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package edu.uci.vcsa.portal.portlets.announcements.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import edu.uci.vcsa.portal.portlets.announcements.UnauthorizedException;
import edu.uci.vcsa.portal.portlets.announcements.model.Announcement;
import edu.uci.vcsa.portal.portlets.announcements.model.Topic;
import edu.uci.vcsa.portal.portlets.announcements.service.IAnnouncementService;
import edu.uci.vcsa.portal.portlets.announcements.service.UserPermissionChecker;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 * 
 * $LastChangedBy$
 * $LastChangedDate$
 */
public class ShowTopicController extends AbstractController {
	
	private IAnnouncementService announcementService;
	private Boolean includeJQuery;

	/* (non-Javadoc)
	 * @see org.springframework.web.portlet.mvc.AbstractController#handleRenderRequestInternal(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	@Override
	protected ModelAndView handleRenderRequestInternal(RenderRequest request,
			RenderResponse response) throws Exception {
		
		HashMap<String,Object> mav = new HashMap<String,Object>();
		
		String topicId = (String) request.getParameter("topicId");
		Topic topic = announcementService.getTopic(Long.parseLong(topicId));
		
		if (!UserPermissionChecker.inRoleForTopic(request, "authors", topic) &&
				!UserPermissionChecker.inRoleForTopic(request, "moderators", topic) &&
				!UserPermissionChecker.inRoleForTopic(request, "admins", topic)) {
			throw new UnauthorizedException("You do not have access to this topic!");
		}
		
		Set<Announcement> annSet = topic.getAnnouncements();
		List<Announcement> annList = new ArrayList<Announcement>();
		annList.addAll(annSet);
		if (annSet.size() < 1)
			annList = null;
		
		if (annList != null) {
			Collections.sort(annList);
		}
				
		mav.put("user", new UserPermissionChecker(request, topic));
		mav.put("topic", topic);
		mav.put("announcements", annList);
		mav.put("now", new Date());
		mav.put("includeJQuery", includeJQuery);
		
		return new ModelAndView("showTopic",mav);
	}

	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

	public void setIncludeJQuery(Boolean includeJQuery) {
		this.includeJQuery = includeJQuery;
	}
	
	
	
}
