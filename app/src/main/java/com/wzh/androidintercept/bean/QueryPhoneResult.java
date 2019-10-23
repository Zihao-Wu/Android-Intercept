package com.wzh.androidintercept.bean;

import java.io.Serializable;

public class QueryPhoneResult implements Serializable {
    public String data;
//    public String html;
    public int status;//1 骚扰电话，其它
//    public String info;

    public String phone;
//    public String identity;//身份标识
    public String location;//归属地

}
