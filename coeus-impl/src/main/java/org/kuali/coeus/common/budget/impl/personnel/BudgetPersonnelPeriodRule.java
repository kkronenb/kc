/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.coeus.common.budget.impl.personnel;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Interval;
import org.kuali.coeus.common.budget.framework.core.Budget;
import org.kuali.coeus.common.budget.framework.nonpersonnel.BudgetLineItem;
import org.kuali.coeus.common.budget.framework.period.BudgetPeriod;
import org.kuali.coeus.common.budget.framework.personnel.BudgetAddPersonnelPeriodEvent;
import org.kuali.coeus.common.budget.framework.personnel.BudgetPersonnelDetails;
import org.kuali.coeus.common.budget.framework.personnel.BudgetSavePersonnelPeriodEvent;
import org.kuali.coeus.common.framework.ruleengine.KcBusinessRule;
import org.kuali.coeus.common.framework.ruleengine.KcEventMethod;
import org.kuali.coeus.common.framework.ruleengine.KcEventResult;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;

import java.util.List;

@KcBusinessRule("budgetPersonnelPeriodRule")
public class BudgetPersonnelPeriodRule {
    
    @KcEventMethod
    public KcEventResult validateAddBudgetPersonnelPeriod(BudgetAddPersonnelPeriodEvent event) {
    	KcEventResult result = new KcEventResult();
    	result.getMessageMap().addToErrorPath(event.getErrorPath());
        verifySummaryPersonnel(event.getBudgetLineItem(), event.getBudgetPeriod().getBudgetLineItems(), result);
    	verifyPersonnelEffortAndCharged(event.getBudgetPersonnelDetails(), result);
    	verifyPersonnelDates(event.getBudgetPersonnelDetails(),event.getBudgetPeriod(), result);
    	result.getMessageMap().removeFromErrorPath(event.getErrorPath());
    	return result;
    }

    @KcEventMethod
    public KcEventResult validateSaveBudgetPersonnelPeriod(BudgetSavePersonnelPeriodEvent event) {
        KcEventResult result = new KcEventResult();
        result.getMessageMap().addToErrorPath(event.getErrorPath());
        verifyPersonnelEffortAndCharged(event.getBudgetPersonnelDetails(), result);
        verifyPersonnelDates(event.getBudgetPersonnelDetails(),event.getBudgetPeriod(), result);
        result.getMessageMap().removeFromErrorPath(event.getErrorPath());
        return result;
    }

    protected void verifySummaryPersonnel(BudgetLineItem newBudgetLineItem,List<BudgetLineItem> budgetLineItemList , KcEventResult result) {
    	String newBudgetCategoryTypeCode = newBudgetLineItem.getBudgetCategory().getBudgetCategoryTypeCode();
        for(BudgetLineItem budgetLineItem : budgetLineItemList) {
        	String existingBudgetCategoryTypeCode = budgetLineItem.getBudgetCategory().getBudgetCategoryTypeCode();
        	if(existingBudgetCategoryTypeCode.equalsIgnoreCase(newBudgetCategoryTypeCode)) {
                if(newBudgetLineItem.getCostElement().equalsIgnoreCase(budgetLineItem.getCostElement()) && 
                        StringUtils.equals(newBudgetLineItem.getGroupName(), budgetLineItem.getGroupName()) &&
                        doDatesOverlap(newBudgetLineItem,budgetLineItem)) {
                	addSummaryPersonnelLineItemErrorMessage(budgetLineItem, result);
                	break;
                }
        	}
        }
    }

    protected boolean doDatesOverlap(BudgetLineItem newBudgetLineItem, BudgetLineItem existingBudgetLineItem){
        Interval newInterval = new Interval(newBudgetLineItem.getStartDate().getTime(),newBudgetLineItem.getEndDate().getTime());
        Interval existingInterval = new Interval(existingBudgetLineItem.getStartDate().getTime(),existingBudgetLineItem.getEndDate().getTime());
        return newInterval.overlaps(existingInterval);

    }
    
    protected void addSummaryPersonnelLineItemErrorMessage(BudgetLineItem budgetLineItem, KcEventResult result) {
        //Summary is already added and user is attempting to add a second summary
        if(budgetLineItem.getBudgetPersonnelDetailsList().isEmpty()) {
            result.getMessageMap().putError("personSequenceNumber", KeyConstants.ERROR_SUMMARY_LINEITEM_EXISTS);
            result.setSuccess(false);
        }else {
            //Condition where Personnel are already added for the line item
            result.getMessageMap().putError("personSequenceNumber", KeyConstants.ERROR_PERSONNEL_EXISTS);
            result.setSuccess(false);
        }
     }
    
    protected void verifyPersonnelEffortAndCharged(BudgetPersonnelDetails budgetPersonnelDetails, KcEventResult result) {
        if(budgetPersonnelDetails.getPercentEffort().isGreaterThan(new ScaleTwoDecimal(100))){
            result.getMessageMap().putError("percentEffort", KeyConstants.ERROR_PERCENTAGE, Constants.PERCENT_EFFORT_FIELD);
            result.setSuccess(false);
        }
        if(budgetPersonnelDetails.getPercentCharged().isGreaterThan(new ScaleTwoDecimal(100))){
            result.getMessageMap().putError("percentCharged", KeyConstants.ERROR_PERCENTAGE, Constants.PERCENT_CHARGED_FIELD);
            result.setSuccess(false);
        }
        if(budgetPersonnelDetails.getPercentCharged().isGreaterThan(budgetPersonnelDetails.getPercentEffort())){
            result.getMessageMap().putError("percentCharged", KeyConstants.ERROR_PERCENT_EFFORT_LESS_THAN_PERCENT_CHARGED);
            result.setSuccess(false);
        }
    }

    protected void verifyPersonnelDates(BudgetPersonnelDetails budgetPersonnelDetails, BudgetPeriod budgetPeriod, KcEventResult result) {
        if(budgetPersonnelDetails.getEndDate().compareTo(budgetPersonnelDetails.getStartDate()) < 0) {
            result.getMessageMap().putError("endDate", KeyConstants.ERROR_PERSONNEL_DETAIL_DATES);
            result.setSuccess(false);
        }
        if(budgetPeriod.getEndDate().compareTo(budgetPersonnelDetails.getEndDate()) < 0) {
            result.getMessageMap().putError("endDate", KeyConstants.ERROR_PERSONNEL_DETAIL_END_DATE, new String[] {"can not be after", "end date"});
            result.setSuccess(false);
        }
        if(budgetPeriod.getStartDate().compareTo(budgetPersonnelDetails.getEndDate()) > 0) {
            result.getMessageMap().putError("endDate", KeyConstants.ERROR_PERSONNEL_DETAIL_END_DATE, new String[] {"can not be before", "start date"});
            result.setSuccess(false);
        }
        if(budgetPeriod.getStartDate().compareTo(budgetPersonnelDetails.getStartDate()) > 0) {
            result.getMessageMap().putError("startDate", KeyConstants.ERROR_PERSONNEL_DETAIL_START_DATE, new String[] {"can not be before", "start date"});
            result.setSuccess(false);
        }
        if(budgetPeriod.getEndDate().compareTo(budgetPersonnelDetails.getStartDate()) < 0) {
            result.getMessageMap().putError("startDate", KeyConstants.ERROR_PERSONNEL_DETAIL_START_DATE, new String[] {"can not be after", "end date"});
            result.setSuccess(false);
        }
    }

}
