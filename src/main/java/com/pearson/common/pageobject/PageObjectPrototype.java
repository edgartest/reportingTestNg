package com.pearson.common.pageobject;

public interface PageObjectPrototype {
    public <T extends PageObject> T newInstance() throws Exception;
}
