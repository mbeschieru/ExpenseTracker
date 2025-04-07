package com.example.restapi.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DebugPasswordBean implements InitializingBean {

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("DEBUG: datasource.password = " + datasourcePassword);

    }
}
