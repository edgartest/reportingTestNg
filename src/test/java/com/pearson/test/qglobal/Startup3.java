package com.pearson.test.qglobal;


import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.pearson.common.Utils;
import com.pearson.common.WebUITest;
import com.pearson.global.common.factory.PageFactory;
import com.pearson.qglobal.factory.GenericRegistrar;
import com.pearson.qglobal.pageobject.GroupAdministration;
import com.pearson.qglobal.pageobject.LogIn;
import com.pearson.qglobal.pageobject.SearchExaminee;
import com.pearson.testng.HtmlGenerator;
import com.pearson.testng.RetryAnalyzer;

@Listeners({HtmlGenerator.class})
public class Startup3 extends WebUITest {
    public Startup3() {
    }

    @Test(
        groups = {"testStartup"},
        retryAnalyzer = RetryAnalyzer.class,
        description = "This is a test description for Startup script 3"
    )
    public void pf003() throws Exception {
        try {
            this.initialize((new Object() {}).getClass(), PageFactory.class, GenericRegistrar.class);
            LogIn loginPage = (LogIn)PageFactory.getInstance(driver).create();
            loginPage.verifyLoginPage();
            Utils.captureScreenshot(driver);
            SearchExaminee examineeSearch = loginPage.signIn("fouser2", "Password3");
            examineeSearch.handleSessionMessage();
            Utils.captureScreenshot(driver);
            GroupAdministration groupAdmn = examineeSearch.clickTabGroupAdm();
            groupAdmn.clickBtnNewGroup();
            Utils.captureScreenshot(driver);
        } catch (Throwable var4) {
            this.handleException(var4);
        }

    }
}
