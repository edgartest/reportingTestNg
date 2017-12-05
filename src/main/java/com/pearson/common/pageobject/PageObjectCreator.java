package com.pearson.common.pageobject;

public interface PageObjectCreator {

    public <T extends PageObject> boolean registerPageObject(T pageObject);

    public <T extends PageObject> T create() throws Exception;

    public <T extends PageObject> T create(String url) throws Exception;

    public PageObject createPageObject() throws Exception;

    public PageObject createPageObject(String url) throws Exception;

    public boolean isSnapshotOnCreation();

    public void setSnapshotOnCreation(boolean snapshotOnCreation);
}
