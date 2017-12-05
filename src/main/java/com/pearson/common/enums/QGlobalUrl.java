package com.pearson.common.enums;

public interface QGlobalUrl {

    public String getUrl();
    public String getText();
    public boolean equalTo(QGlobalUrl url);
    public boolean equalTo(String url);
    public boolean contains(String url);
    public boolean isValid();

}
