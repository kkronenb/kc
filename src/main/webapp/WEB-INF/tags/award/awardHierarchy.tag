<%--
 Copyright 2006-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.osedu.org/licenses/ECL-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>

<c:set var="awardHierarchyAttributes" value="${DataDictionary.AwardHierarchy.attributes}" />
<c:set var="action" value="awardHierarchy" />

<kul:tab tabTitle="Hierarchy" defaultOpen="false" tabErrorKey="document.awardList[0].awardReportTermItems" auditCluster="reportsAuditErrors" tabAuditKey="document.reportTermsAuditRules*" useRiceAuditMode="true">
	<div class="tab-container" align="right">
    	<h3>
    		<span class="subhead-left">Hierarchy</span>
    		<span class="subhead-right">
    			<kul:help businessObjectClassName="org.kuali.kra.award.bo.AwardFandaRate" altText="help"/>
			</span>
        </h3>        
    
	<table cellpadding="0" cellspacing="0" summary="">
    	<%-- Header --%>
    		<tr>
          		<kul:htmlAttributeHeaderCell literalLabel="&nbsp;" scope="col" /> 
          		<kul:htmlAttributeHeaderCell attributeEntry="${awardHierarchyAttributes.rootAwardNumber}" scope="col" /></div></th>
          		<kul:htmlAttributeHeaderCell attributeEntry="${awardHierarchyAttributes.awardNumber}" scope="col" /></div></th>
          		<kul:htmlAttributeHeaderCell attributeEntry="${awardHierarchyAttributes.parentAwardNumber}" scope="col" /></div></th>
          		<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col" />
          	</tr>
		<c:forEach var="awardHierarchyNode" items="${KualiForm.awardHierarchyNodes}" varStatus="status">
          <tr>
			<th class="infoline">
				<c:out value="${status.index+1}" />
			</th>
                <td align="left" valign="middle">
			<div align="center">
              		<c:out value ="${awardHierarchyNode.value.rootAwardNumber}" />
			</div>
  			</td>
                <td align="left" valign="middle">
			<div align="center">
              		<c:out value ="${awardHierarchyNode.value.awardNumber}" />
			</div>
		  </td>
          <td align="left" valign="middle">
			<div align="center">
              		<c:out value ="${awardHierarchyNode.value.parentAwardNumber}" />
			</div>
		  </td>
		  <td class="infoline">
			<div align="center">
				<html:image property="methodToCall.maintainAwardHierarchy.line${status.index}.awardNumber${awardHierarchyNode.value.awardNumber}.anchor${currentTabIndex}"
					src='${ConfigProperties.kra.externalizable.images.url}tinybutton-maintain1.gif' styleClass="tinybutton"/>
				<html:image property="methodToCall.copyAwardInHierarchy.line${status.index}.awardNumber${awardHierarchyNode.value.awardNumber}.anchor${currentTabIndex}"
					src='${ConfigProperties.kra.externalizable.images.url}tinybutton-copy1.gif' styleClass="tinybutton"/>	
			</div>
          </td>
           </tr>
      	</c:forEach>    
      </table>	
    
    </div>
</kul:tab>
