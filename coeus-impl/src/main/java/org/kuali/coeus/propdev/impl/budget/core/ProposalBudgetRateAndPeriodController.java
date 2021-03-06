package org.kuali.coeus.propdev.impl.budget.core;

import static org.kuali.kra.infrastructure.KeyConstants.QUESTION_RECALCULATE_BUDGET_CONFIRMATION;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.budget.framework.core.Budget;
import org.kuali.coeus.common.budget.framework.core.SaveBudgetEvent;
import org.kuali.coeus.common.budget.framework.period.AddBudgetPeriodEvent;
import org.kuali.coeus.common.budget.framework.period.BudgetPeriod;
import org.kuali.coeus.common.budget.framework.period.GenerateBudgetPeriodEvent;
import org.kuali.coeus.propdev.impl.budget.ProposalDevelopmentBudgetExt;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.web.controller.MethodAccessible;
import org.kuali.rice.krad.web.form.DialogResponse;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/proposalBudget")
public class ProposalBudgetRateAndPeriodController extends ProposalBudgetControllerBase {

	private static final String CONFIRM_PERIOD_CHANGES_DIALOG_ID = "PropBudget-PeriodsPage-ConfirmPeriodChangesDialog";
	private static final String PERIOD_CHANGES_DIALOG_ID = "PropBudget-PeriodsPage-ChangePeriodDialog";
	
	
	@MethodAccessible
    @RequestMapping(params="methodToCall=resetToBudgetPeriodDefault")
    public ModelAndView resetToBudgetPeriodDefault(@ModelAttribute("KualiForm") ProposalBudgetForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentBudgetExt budget = form.getBudget();
        String warningMessage = getBudgetSummaryService().defaultWarningMessage(budget);
        DialogResponse dialogResponse = form.getDialogResponse(CONFIRM_PERIOD_CHANGES_DIALOG_ID);
        if (StringUtils.isNotBlank(warningMessage)) {
            if(dialogResponse == null) {
            	form.setDefaultBudgetPeriodWarningMessage(warningMessage);
            	return getModelAndViewService().showDialog(CONFIRM_PERIOD_CHANGES_DIALOG_ID, true, form);
            }else {
                boolean confirmResetDefault = dialogResponse.getResponseAsBoolean();
                if(confirmResetDefault) {
                	getBudgetSummaryService().defaultBudgetPeriods(budget);
                	getBudgetSummaryService().adjustStartEndDatesForLineItems(budget);
                }
            }
        }else {
        	getBudgetSummaryService().defaultBudgetPeriods(budget);
        }
        return getModelAndViewService().getModelAndView(form);
    }    
	
	@MethodAccessible
    @RequestMapping(params="methodToCall=recalculateBudgetWithChanges")
    public ModelAndView recalculateBudgetWithChanges(@ModelAttribute("KualiForm") ProposalBudgetForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProposalDevelopmentBudgetExt budget = form.getBudget();
    	getBudgetSummaryService().updateOnOffCampusFlag(budget, budget.getOnOffCampusFlag());
    	getBudgetSummaryService().calculateBudget(budget);
        return getModelAndViewService().getModelAndView(form);
    }    
	
    @RequestMapping(params="methodToCall=addBudgetPeriod")
    public ModelAndView addBudgetPeriod(@ModelAttribute("KualiForm") ProposalBudgetForm form) throws Exception {
        BudgetPeriod newBudgetPeriod = ((BudgetPeriod)form.getNewCollectionLines().get("budget.budgetPeriods"));
        Budget budget = form.getBudget();
        newBudgetPeriod.setBudget(budget);
        if (getKcBusinessRulesEngine().applyRules(new AddBudgetPeriodEvent(budget, newBudgetPeriod))) {
            getBudgetSummaryService().addBudgetPeriod(budget, newBudgetPeriod);
        }
        return getModelAndViewService().getModelAndView(form);
    }
    
    @RequestMapping(params="methodToCall=saveBudgetPeriod")
    public ModelAndView saveBudgetPeriod(@ModelAttribute("KualiForm") ProposalBudgetForm form) throws Exception {
    	Budget budget = form.getBudget();
    	ModelAndView modelAndView = getModelAndViewService().getModelAndView(form);
        int selectedLine = Integer.parseInt(form.getActionParamaterValue(UifParameters.SELECTED_LINE_INDEX));
        BudgetPeriod budgetPeriod = budget.getBudgetPeriods().get(selectedLine);
        boolean rulePassed = getKcBusinessRulesEngine().applyRules(new SaveBudgetEvent(budget));
        if (isBudgetPeriodDateChanged(budgetPeriod) && isOnlyLineItemDateError()) {
        	getGlobalVariableService().getMessageMap().clearErrorMessages();
            DialogResponse dialogResponse = form.getDialogResponse(PERIOD_CHANGES_DIALOG_ID);
            if(dialogResponse == null) {
            	return getModelAndViewService().showDialog(PERIOD_CHANGES_DIALOG_ID, true, form);
            }else {
                rulePassed = false;
                boolean confirmResetDefault = dialogResponse.getResponseAsBoolean();
                if(confirmResetDefault) {
                    getBudgetSummaryService().adjustStartEndDatesForLineItems(budgetPeriod);
                    //check rules again after adjusting date
                    rulePassed = getKcBusinessRulesEngine().applyRules(new SaveBudgetEvent(budget));
                }
            }
        }
        if(rulePassed) {
        	getBudgetCalculationService().calculateBudgetPeriod(budget, budgetPeriod);
            modelAndView = super.saveLine(form);
        }
        return modelAndView;
    }
		
	@MethodAccessible
    @RequestMapping(params="methodToCall=generateAllPeriods")
    public ModelAndView generateAllPeriods(@ModelAttribute("KualiForm") ProposalBudgetForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Budget budget = form.getBudget();
        boolean rulePassed = getKcBusinessRulesEngine().applyRules(
                new GenerateBudgetPeriodEvent(form.getBudget(), null));
        if(rulePassed) {
            DialogResponse dialogResponse = form.getDialogResponse(CONFIRM_PERIOD_CHANGES_DIALOG_ID);
            Budget originalBudget = getOriginalBudget(form);
        	if(dialogResponse == null && isRateTypeChanged(originalBudget, budget)) {
        		form.setDefaultBudgetPeriodWarningMessage(getKualiConfigurationService().getPropertyValueAsString(QUESTION_RECALCULATE_BUDGET_CONFIRMATION));
            	return getModelAndViewService().showDialog(CONFIRM_PERIOD_CHANGES_DIALOG_ID, true, form);
        	}
            boolean confirmRecalculate = dialogResponse !=null ? dialogResponse.getResponseAsBoolean() : true;
            if(confirmRecalculate) {
            	getBudgetSummaryService().updateOnOffCampusFlag(budget, budget.getOnOffCampusFlag());
                getBudgetSummaryService().generateAllPeriods(budget);
            }
        }
        return getModelAndViewService().getModelAndView(form);
	}

    @RequestMapping(params={"methodToCall=save", "pageId=PropBudget-PeriodsPage"})
    public ModelAndView save(ProposalBudgetForm form) {
    	ModelAndView modelAndView = getModelAndViewService().getModelAndView(form);
        Budget budget = form.getBudget();
        boolean rulePassed = getKcBusinessRulesEngine().applyRules(new SaveBudgetEvent(budget));
        if (isBudgetPeriodDateChanged(budget) && isOnlyLineItemDateError()) {
        	getGlobalVariableService().getMessageMap().clearErrorMessages();
            DialogResponse dialogResponse = form.getDialogResponse(PERIOD_CHANGES_DIALOG_ID);
            if(dialogResponse == null) {
            	return getModelAndViewService().showDialog(PERIOD_CHANGES_DIALOG_ID, true, form);
            }else {
                rulePassed = false;
                boolean confirmResetDefault = dialogResponse.getResponseAsBoolean();
                if(confirmResetDefault) {
                    getBudgetSummaryService().adjustStartEndDatesForLineItems(budget);
                    //check rules again after adjusting date
                    rulePassed = getKcBusinessRulesEngine().applyRules(new SaveBudgetEvent(budget));
                }
            }
        }
        if(rulePassed) {
            modelAndView = super.save(form);
            form.getEditableBudgetLineItems().clear();
        }
        return modelAndView;
    }
    
    /**
     * Method is to verify whether budget dates changed.
     * @param budget
     * @return
     */
    private boolean isBudgetPeriodDateChanged(Budget budget) {
    	boolean budgetPeriodDateChanged = false;
        for (BudgetPeriod budgetPeriod : budget.getBudgetPeriods()) {
        	budgetPeriodDateChanged = isBudgetPeriodDateChanged(budgetPeriod);
        	if(budgetPeriodDateChanged) {
        		break;
        	}
        }
        return budgetPeriodDateChanged;
    }
    
    private boolean isBudgetPeriodDateChanged(BudgetPeriod budgetPeriod) {
        if (budgetPeriod.getStartDate() != null && budgetPeriod.getOldStartDate() != null && 
                budgetPeriod.getEndDate() != null && budgetPeriod.getOldEndDate() != null && 
                (budgetPeriod.getStartDate().compareTo(budgetPeriod.getOldStartDate()) != 0
                || budgetPeriod.getEndDate().compareTo(budgetPeriod.getOldEndDate()) != 0)) {
            return true;
        }
        return false;
    }

    /**
     * This method is to check the error map to see if there is any error other than line item date error.
     * line item date date error should be resolved with adjustlineitem start/end date.
     * This is called after rule verification and before save.
     * @return
     */
    private boolean isOnlyLineItemDateError() {
    	Map<String, List<ErrorMessage>> errors = getGlobalVariableService().getMessageMap().getErrorMessages();
    	List<String> lineItemDateErrors = getLineItemDateErrors();
    	if (!errors.isEmpty()) {
            for (Map.Entry<String, List<ErrorMessage>> entry : errors.entrySet()) {
                List<ErrorMessage> errorMessages = entry.getValue();
                for(ErrorMessage errorMessage : errorMessages) {
                    if (!lineItemDateErrors.contains(errorMessage.getErrorKey())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * List of line item errors specific to date that we can skip 
     * to adjust line item dates as per adjusted period dates
     * @return
     */
    private List<String> getLineItemDateErrors() {
    	List<String> lineItemDateErrors = new ArrayList<String>();
    	lineItemDateErrors.add("error.lineItem.dateDoesNotmatch");
    	lineItemDateErrors.add("error.line.item.start.date");
    	lineItemDateErrors.add("error.line.item.end.date");
    	return lineItemDateErrors;
    }

}
