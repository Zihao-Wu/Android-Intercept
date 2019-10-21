package com.wzh.androidintercept.bean;

public class CheckPhoneResult {
    private String data;
    private String html;
    private int status;//1 骚扰电话，其它
    private String info;

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHtml() {
        return this.html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "CheckPhoneResult{" +
                "data='" + data + '\'' +
                ", html='" + html + '\'' +
                ", status=" + status +
                ", info='" + info + '\'' +
                '}';
    }
}
