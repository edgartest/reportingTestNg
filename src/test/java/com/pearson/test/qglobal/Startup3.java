package com.pearson.test.qglobal;


import com.pearson.common.Utils;
import com.pearson.common.WebUITest;
import com.pearson.qglobal.common.factory.PageFactory;
import com.pearson.qglobal.factory.GenericRegistrar;
import com.pearson.qglobal.pageobject.GroupAdministration;
import com.pearson.qglobal.pageobject.LogIn;
import com.pearson.qglobal.pageobject.SearchExaminee;
import com.pearson.testng.HtmlGenerator;
import com.pearson.testng.RetryAnalyzer;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

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
            this.initialize((new Object() {
            }).getClass(), PageFactory.class, GenericRegistrar.class);
            LogIn loginPage = (LogIn)PageFactory.getInstance(this.driver).create();
            loginPage.verifyLoginPage();
            Utils.captureScreenshot(this.driver);
            SearchExaminee examineeSearch = loginPage.signIn("fouser2", "Password3");
            examineeSearch.handleSessionMessage();
            Utils.captureScreenshot(this.driver);
            GroupAdministration groupAdmn = examineeSearch.clickTabGroupAdm();
            groupAdmn.clickBtnNewGroup();
            Utils.captureScreenshot(this.driver);
        } catch (Throwable var4) {
            this.handleException(var4);
        }

    }
}
