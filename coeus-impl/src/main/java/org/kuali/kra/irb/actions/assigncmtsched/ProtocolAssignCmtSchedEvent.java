/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.irb.actions.assigncmtsched;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.irb.ProtocolDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * The event that occurs when the IRB Administrator assigns a protocol
 * to a committee/schedule.
 */
public class ProtocolAssignCmtSchedEvent extends KcDocumentEventBase {
    
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProtocolAssignCmtSchedEvent.class);
    
    private ProtocolAssignCmtSchedBean actionBean;
    
    public ProtocolAssignCmtSchedEvent(ProtocolDocument document, ProtocolAssignCmtSchedBean actionBean) {
        super("Submitting for review for document " + getDocumentId(document), "", document);
        this.actionBean = actionBean;
        logEvent();
    }

    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (this.actionBean == null) {
            logMessage.append("null actionBean");
        }
        else {
            logMessage.append(actionBean.toString());
        }

        LOG.debug(logMessage);
    }

    public Class getRuleInterfaceClass() {
        return ExecuteProtocolAssignCmtSchedRule.class;
    }

    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((ExecuteProtocolAssignCmtSchedRule) rule).processAssignToCommitteeSchedule((ProtocolDocument) getDocument(), actionBean);
    }
}
