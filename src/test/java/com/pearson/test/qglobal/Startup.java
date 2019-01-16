package com.pearson.test.qglobal;

import org.testng.annotations.Test;

import com.pearson.common.Log;
import com.pearson.common.Utils;
import com.pearson.common.WebUITest;
import com.pearson.global.common.factory.PageFactory;
import com.pearson.qglobal.factory.GenericRegistrar;
import com.pearson.qglobal.pageobject.GroupAdministration;
import com.pearson.qglobal.pageobject.LogIn;
import com.pearson.qglobal.pageobject.SearchExaminee;
import com.pearson.testng.RetryAnalyzer;

//@Listeners({HtmlGenerator.class})
public class Startup extends WebUITest {
    public Startup() {
    }

    @Test(
        groups = {"testStartup"},
        retryAnalyzer = RetryAnalyzer.class,
        description = "This is a test description for Startup script"
    )
    public void pf001() throws Exception {
        try {
            this.initialize((new Object() {}).getClass(), PageFactory.class, GenericRegistrar.class);
            LogIn loginPage = (LogIn)PageFactory.getInstance(this.driver).create();
            loginPage.verifyLoginPage();
            Utils.captureScreenshot(this.driver);
            Log.errorLog(this.driver).info("Error log..");
            SearchExaminee examineeSearch = loginPage.signIn(this.jsonConfig.getJSONObject("credentials").getString("user"), this.jsonConfig.getJSONObject("credentials").getString("password"));
            examineeSearch.handleSessionMessage();
            GroupAdministration groupAdmn = examineeSearch.clickTabGroupAdm();
            groupAdmn.clickBtnNewGroup();
        } catch (Throwable var4) {
            this.handleException(var4);
        }

    }
}