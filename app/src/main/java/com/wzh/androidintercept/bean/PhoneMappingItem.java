package com.wzh.androidintercept.bean;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author faqi.tao
 * @time 2019/10/21
 */
public class PhoneMappingItem implements Serializable {
    private String originPhone;
    private String mappingPhone;
    private long createTime;

    public PhoneMappingItem(String originPhone, String mappingPhone) {
        this(originPhone, mappingPhone, System.currentTimeMillis());
    }

    public PhoneMappingItem(String originPhone, String mappingPhone, long createTime) {
        this.originPhone = originPhone;
        this.mappingPhone = mappingPhone;
        this.createTime = createTime;
    }

    public String getOriginPhone() {
        return originPhone;
    }

    public void setOriginPhone(String originPhone) {
        this.originPhone = originPhone;
    }

    public String getMappingPhone() {
        return mappingPhone;
    }

    public void setMappingPhone(String mappingPhone) {
        this.mappingPhone = mappingPhone;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof PhoneMappingItem) {
            PhoneMappingItem that = (PhoneMappingItem) o;
            return Objects.equals(originPhone, that.originPhone);
        } else if (o instanceof String) {
            String orig = (String) o;
            return Objects.equals(originPhone, orig);
        }
        return false;

    }

    @Override
    public int hashCode() {
        return originPhone != null ? originPhone.hashCode() : 0;
    }
}
