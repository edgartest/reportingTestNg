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
public class Startup2 extends WebUITest {
    public Startup2() {
    }

    @Test(
        groups = {"testStartup"},
        retryAnalyzer = RetryAnalyzer.class,
        description = "This is a test description for Startup script 2"
    )
    public void pf002() throws Exception {
        try {
            this.initialize((new Object() {}).getClass(), PageFactory.class, GenericRegistrar.class);
            LogIn loginPage = (LogIn)PageFactory.getInstance(this.driver).create();
            loginPage.verifyLoginPage();
            Utils.captureScreenshot(this.driver);
            SearchExaminee examineeSearch = loginPage.signIn("alonso05", "Password1");
            examineeSearch.handleSessionMessage();
            Utils.captureScreenshot(this.driver);
            GroupAdministration groupAdmn = examineeSearch.clickTabGroupAdm();
            groupAdmn.clickBtnNewGroup();
        } catch (Throwable var4) {
            this.handleException(var4);
        }

    }
}