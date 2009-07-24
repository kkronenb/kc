/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
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
package org.kuali.kra.s2s.generator.impl;

import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.s2s.generator.S2STestBase;

public class RRSubAwardBudgetV1_0GeneratorTest extends S2STestBase<RRSubAwardBudgetV1_0Generator> {

    @Override
    protected Class<RRSubAwardBudgetV1_0Generator> getFormGeneratorClass() {
        return RRSubAwardBudgetV1_0Generator.class;
    }

    @Override
    protected void prepareData(ProposalDevelopmentDocument document) throws Exception {

        document.getDevelopmentProposal().setProposalNumber("8758");
        document.getDevelopmentProposal().setTitle("SubAwardBudget");
        document.getDevelopmentProposal().setCfdaNumber("565645");
    }
}
