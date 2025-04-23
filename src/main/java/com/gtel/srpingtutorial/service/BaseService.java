package com.gtel.srpingtutorial.service;

import com.gtel.srpingtutorial.domains.DomainFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

public class BaseService {

    @Autowired
    public DomainFactory domainFactory;
    @Autowired
    public PasswordEncoder passwordEncoder;

    public Integer getParam(Map<String, String> params, String key){

        if (params.get(key) == null || StringUtils.isBlank(params.get(key)))
            return null;

        try {
            return Integer.valueOf(params.get(key));
        }catch (Exception e) {
            return null;
        }

    }


    public String getCurrentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return (String) authentication.getPrincipal();
    }

    public Integer getParam(Map<String, String> params , String key, int defaultValue){

        if (params.get(key) == null || StringUtils.isBlank(params.get(key)))
            return defaultValue;

        try {
            return Integer.valueOf(params.get(key));
        }catch (Exception e) {
            return defaultValue;
        }
    }

    public int validatePage(int page){
        return page < 1 ? 1 : page;
    }

    public int validatePageSize(int pageSize){
        return pageSize < 1 ? 10 : pageSize;
    }
}
