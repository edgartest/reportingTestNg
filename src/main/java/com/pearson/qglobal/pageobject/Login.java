package com.pearson.qglobal.pageobject;

import com.pearson.common.Log;
import com.pearson.common.XPath;
import com.pearson.common.enums.QGUrl;
import com.pearson.qglobal.common.factory.PageFactory;
import com.pearson.qglobal.common.pageobject.QGPageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LogIn extends QGPageObject {
    static final QGUrl URL;
    protected String signInBtn = "//*[@id='login:signInButton']";
    protected String qGLogo = "//*[@alt='Q-global Logo']";
    protected String userNameInput = "//*[@id='login:uname']";
    protected String passwordInput = "//*[@id='login:pword']";
    protected String langCountrySelect = "//select[@id='login:languageCountry']";
    protected String langIdSelect = "//select[@id='login:languageId']";
    @FindBy(
        id = "login:uname"
    )
    protected WebElement userNameElement;

    static {
        URL = QGUrl.LOGIN;
    }

    public LogIn(WebDriver driver) {
        super(driver);
    }

    public String getPageName() {
        return URL.getPageName();
    }

    public String getPageUrl() {
        return URL.getUrl();
    }

    public void verifyLoginPage() throws Exception {
        Log.log(this.driver).info("Verifying Login Page");
        XPath.find(this.driver, this.qGLogo);
        Log.log(this.driver).info("Q-Global Logo found in the Login Page");
        XPath.find(this.driver, this.userNameInput);
        Log.log(this.driver).info("Username Input found in the Login Page");
        XPath.find(this.driver, this.passwordInput);
        Log.log(this.driver).info("Password Input found in the Login Page");
        XPath.find(this.driver, this.langCountrySelect);
        Log.log(this.driver).info("Language Country Select found in the Login Page");
        XPath.find(this.driver, this.langIdSelect);
        Log.log(this.driver).info("Language Id Select found in the Login Page");
        XPath.find(this.driver, this.signInBtn);
        Log.log(this.driver).info("Sing In Button found in the Login Page");
    }

    public SearchExaminee signIn(String userName, String password) throws Exception {
        this.userNameElement.sendKeys(new CharSequence[]{userName});
        XPath.waitForVisibleElement(this.driver, this.passwordInput).sendKeys(new CharSequence[]{password});
        XPath.waitForVisibleElement(this.driver, this.signInBtn).click();
        return (SearchExaminee)PageFactory.getInstance(this.driver).create("search_examinee");
    }
}
