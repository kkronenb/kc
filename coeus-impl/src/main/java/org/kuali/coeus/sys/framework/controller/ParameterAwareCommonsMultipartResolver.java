package org.kuali.coeus.sys.framework.controller;

import org.apache.commons.lang3.StringUtils;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.KRADConstants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class ParameterAwareCommonsMultipartResolver extends CommonsMultipartResolver implements InitializingBean {

    private ParameterService parameterService;

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

   public void afterPropertiesSet() throws Exception {
       this.setMaxUploadSize(getMaxUploadSizeParameter());
   }

    private Long getMaxUploadSizeParameter() {
        long maxUploadSize = 0;
        String maxString = getParameterService().getParameterValueAsString(KRADConstants.KNS_NAMESPACE, ParameterConstants.ALL_COMPONENT,KRADConstants.MAX_UPLOAD_SIZE_PARM_NM);

        String suffix = StringUtils.substring(maxString,maxString.length()-1);
        Long multiplier = Long.parseLong(StringUtils.stripEnd(maxString, suffix));
        if (StringUtils.equals(suffix,"K")) {
            maxUploadSize = multiplier * 1000L;
        } else if (StringUtils.equals(suffix,"M")) {
            maxUploadSize = multiplier * 1000000L;
        } else if (StringUtils.equals(suffix,"G")) {
            maxUploadSize = multiplier * 1000000000L;
        } else {
            maxUploadSize = Long.parseLong(maxString);
        }

        return maxUploadSize;
    }
}


