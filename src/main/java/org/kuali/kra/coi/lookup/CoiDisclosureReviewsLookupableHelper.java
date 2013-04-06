/*
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.kra.coi.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.coi.CoiDisclosure;
import org.kuali.kra.coi.CoiReviewer;
import org.kuali.kra.coi.CoiUserRole;
import org.kuali.kra.service.KcPersonService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;

public class CoiDisclosureReviewsLookupableHelper extends CoiDisclosureLookupableHelperBase {


    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<CoiDisclosure> allDisclosures = (List<CoiDisclosure>) super.getResults(fieldValues);
        List<CoiDisclosure> coiDisclosureReviews = new ArrayList<CoiDisclosure>();
        String currentUser = GlobalVariables.getUserSession().getPrincipalName();
        
        for (CoiDisclosure disclosure : allDisclosures) {
            List<CoiUserRole> userRoles = disclosure.getCoiUserRoles();
            for (CoiUserRole userRole : userRoles) {
                if (StringUtils.equalsIgnoreCase(userRole.getReviewerCode(), CoiReviewer.ASSIGNED_REVIEWER)) {
                    // userId is really the username . This should probably be "fixed" at some point.
                    if (StringUtils.equalsIgnoreCase(currentUser, userRole.getUserId())) {
                        coiDisclosureReviews.add(disclosure);
                    }
                }
            }
        }
        return coiDisclosureReviews;
    }
}
