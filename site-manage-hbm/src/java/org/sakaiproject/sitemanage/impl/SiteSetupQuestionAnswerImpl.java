/**********************************************************************************
 * $URL:  $
 * $Id:  $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/


package org.sakaiproject.sitemanage.impl;

import org.sakaiproject.sitemanage.api.model.*;

public class SiteSetupQuestionAnswerImpl implements SiteSetupQuestionAnswer{
	
	private static final long serialVersionUID = 1L;
	
	public SiteSetupQuestionAnswerImpl()
	{
		
	}
	
	public SiteSetupQuestionAnswerImpl(String answer, String answerString, boolean isFillInBlank)
	{
		this.answer = answer;
		this.answerString = answerString;
		this.isFillInBlank = isFillInBlank;
	}
	
	private String id;
	/**
	 * {@inheritDoc}
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setId(String id)
	{
		this.id = id;
	}
	
	private boolean isFillInBlank;
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isFillInBlank()
	{
		return isFillInBlank;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFillInBlank(boolean isFillInBlank)
	{
		this.isFillInBlank = isFillInBlank;
	}
	
	private String answer;

	/**
	 * {@inheritDoc}
	 */
	public String getAnswer()
	{
		return answer;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setAnswer(String answer)
	{
		this.answer = answer;
	}

	private String answerString;
	
	/**
	 * {@inheritDoc}
	 */
	public String getAnswerString()
	{
		return answerString;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setAnswerString(String answerString)
	{
		this.answerString = answerString;
	}

	private String questionId;
	
	/**
	 * {@inheritDoc}
	 */
	public String getQuestionId()
	{
		return questionId;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setQuestionId(String questionId)
	{
		this.questionId = questionId;
	}
}
