package com.pearson.qglobal.pageobject;

import com.pearson.common.Utils;
import com.pearson.common.XPath;
import com.pearson.common.enums.QGUrl;
import com.pearson.qglobal.common.factory.PageFactory;
import com.pearson.qglobal.common.pageobject.QGPageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SearchExaminee extends QGPageObject {
    static final QGUrl URL;
    protected String activeSession = "//*[@id='activeSession']";
    @FindBy(
        id = "activeSession"
    )
    protected WebElement activeSessionWebElement;
    @FindBy(
        id = "grpTabId_lbl"
    )
    protected WebElement GroupAdmnTab;
    @FindBy(
        id = "repTabId_lbl"
    )
    protected WebElement ReportTab;
    protected String activeSessionOK = "//*[substring(@id, string-length(@id) - string-length('confirmTerminate') +1) = 'confirmTerminate']";

    static {
        URL = QGUrl.SEARCH_EXAMINEE;
    }

    public SearchExaminee(WebDriver driver) {
        super(driver);
    }

    public String getPageName() {
        return URL.getPageName();
    }

    public String getPageUrl() {
        return URL.getUrl();
    }

    protected boolean isActiveSessionMessage() throws Exception {
        String rawStyle = this.activeSessionWebElement.getAttribute("style");
        return rawStyle.isEmpty();
    }

    public void handleSessionMessage() throws Exception {
        if (this.isActiveSessionMessage()) {
            XPath.find(this.driver, this.activeSessionOK).click();
        }

        Utils.waitForSpinner(this.driver);
        Utils.waitForGridLoad(this.driver);
    }

    public GroupAdministration clickTabGroupAdm() throws Exception {
        this.GroupAdmnTab.click();
        Utils.waitForSpinner(this.driver);
        return (GroupAdministration)PageFactory.getInstance(this.driver).create("group_admin");
    }
}
