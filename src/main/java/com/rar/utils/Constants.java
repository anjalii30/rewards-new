package com.rar.utils;

import org.springframework.beans.factory.annotation.Value;

public final class Constants {

    public final static Integer CREATED = 0;
    public final static Integer ROLLED_OUT =1;
    public final static Integer END_DATE_PASSED = 2;
    public final static Integer DISCONTINUED =3;
    public final static Integer PUBLISHED = 4;
    public final static Integer EDITED_AFTER_ROLLOUT= 5;


    public final static Integer ROLE_EMPLOYEE = 1;
    public final static Integer ROLE_ADMIN = 2;
    
    @Value("${jwt.secret}")
    public final static String secret = "xsNxAnioE69S0DZ4YUVUrCzR";

}
