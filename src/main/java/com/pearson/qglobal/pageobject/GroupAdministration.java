package com.pearson.qglobal.pageobject;

import com.pearson.common.enums.QGUrl;
import com.pearson.qglobal.common.pageobject.QGPageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GroupAdministration extends QGPageObject {
    static final QGUrl URL;
    @FindBy(
        id = "searchExamineeGroupAdministration:createGroupBtn"
    )
    protected WebElement newGroupBtn;

    static {
        URL = QGUrl.GROUP_ADMIN;
    }

    public GroupAdministration(WebDriver driver) {
        super(driver);
    }

    public String getPageName() {
        return URL.getPageName();
    }

    public String getPageUrl() {
        return URL.getUrl();
    }

    public void clickBtnNewGroup() {
        this.newGroupBtn.click();
    }
}
