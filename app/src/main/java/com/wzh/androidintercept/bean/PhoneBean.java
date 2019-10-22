package com.wzh.androidintercept.bean;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * FileName: PhoneBean
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-10-18
 * Description:
 */
public class PhoneBean implements Serializable {

    public String phone;
    public String identity;//身份标识
    public String location;//归属地

    public PhoneBean(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof PhoneBean) {
            return ((PhoneBean) obj).phone.equals(phone);
        }
        return false;
    }
}
