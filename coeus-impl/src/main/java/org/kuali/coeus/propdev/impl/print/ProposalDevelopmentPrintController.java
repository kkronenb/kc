package org.kuali.coeus.propdev.impl.print;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.person.KcPersonService;
import org.kuali.coeus.common.framework.print.AttachmentDataSource;
import org.kuali.coeus.common.framework.print.PrintConstants;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentControllerBase;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocumentForm;
import org.kuali.coeus.sys.framework.controller.ControllerFileUtils;
import org.kuali.rice.krad.web.controller.MethodAccessible;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ProposalDevelopmentPrintController extends ProposalDevelopmentControllerBase{

    @Autowired
    @Qualifier("proposalDevelopmentPrintingService")
    private ProposalDevelopmentPrintingService proposalDevelopmentPrintingService;

    @Autowired
    @Qualifier("kcPersonService")
    private KcPersonService kcPersonService;

    @RequestMapping(value = "/proposalDevelopment", params="methodToCall=preparePrintDialog")
    public ModelAndView preparePrintDialog(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        getProposalDevelopmentPrintingService().populateSponsorForms(form.getSponsorFormTemplates(),form.getDevelopmentProposal().getSponsorCode());

        return getModelAndViewService().showDialog("PropDev-Print-Dialog",true,form);
    }

    @RequestMapping(value = "/proposalDevelopment", params="methodToCall=printSponsorForms")
    public ModelAndView printSponsorForms(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, HttpServletResponse response) throws Exception {

        Map<String,Object> reportParameters = new HashMap<String,Object>();
        reportParameters.put(ProposalDevelopmentPrintingService.SELECTED_TEMPLATES,
                getProposalDevelopmentPrintingService().getSponsorFormTemplates(form.getSponsorFormTemplates()));


            AttachmentDataSource dataStream = getProposalDevelopmentPrintingService().printProposalDevelopmentReport(form.getDevelopmentProposal(),
                    ProposalDevelopmentPrintingService.PRINT_PROPOSAL_SPONSOR_FORMS, reportParameters);

            ControllerFileUtils.streamToResponse(dataStream, response);
            return null;
    }

    @MethodAccessible
    @RequestMapping(value = "/proposalDevelopment", params="methodToCall=generateReport")
    public ModelAndView generateReport(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, HttpServletResponse response) throws Exception {
        form.getReportHelper().getCurrentReportBeans().clear();
        form.getReportHelper().getPendingReportBeans().clear();
        form.getReportHelper().setTargetPerson(getKcPersonService().getKcPersonByPersonId(form.getReportHelper().getPersonId()));
        if (StringUtils.equals(form.getReportHelper().getReportType(), "current")) {
            form.getReportHelper().prepareCurrentReport();
        } else {
            form.getReportHelper().preparePendingReport();
        }

        return getRefreshControllerService().refresh(form);
    }

    @MethodAccessible
    @RequestMapping(value = "/proposalDevelopment", params="methodToCall=printReport")
    public ModelAndView printReport(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, HttpServletResponse response) throws Exception {
        form.getReportHelper().setTargetPerson(getKcPersonService().getKcPersonByPersonId(form.getReportHelper().getPersonId()));

        Map<String, Object> reportParameters = new HashMap<String, Object>();
        reportParameters.put(PrintConstants.PERSON_ID_KEY, form.getReportHelper().getPersonId());
        reportParameters.put(PrintConstants.REPORT_PERSON_NAME_KEY, form.getReportHelper().getTargetPerson().getFullName());
        AttachmentDataSource dataStream = null;
        if (StringUtils.equals(form.getReportHelper().getReportType(), "current")) {
            dataStream = form.getReportHelper().getCurrentAndPendingReportService().printCurrentReport(reportParameters);
        } else {
            dataStream = form.getReportHelper().getCurrentAndPendingReportService().printPendingReport(reportParameters);
        }

        ControllerFileUtils.streamToResponse(dataStream,response);
        return null;
    }

    public ProposalDevelopmentPrintingService getProposalDevelopmentPrintingService() {
        return proposalDevelopmentPrintingService;
    }

    public void setProposalDevelopmentPrintingService(ProposalDevelopmentPrintingService proposalDevelopmentPrintingService) {
        this.proposalDevelopmentPrintingService = proposalDevelopmentPrintingService;
    }

    public KcPersonService getKcPersonService() {
        return kcPersonService;
    }

    public void setKcPersonService(KcPersonService kcPersonService) {
        this.kcPersonService = kcPersonService;
    }
}


