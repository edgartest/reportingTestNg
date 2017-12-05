package com.pearson.test.qglobal;

import com.pearson.common.Utils;
import com.pearson.common.WebUITest;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class LaunchQualtrics extends WebUITest {
    public LaunchQualtrics() {
    }

    @Test(
        groups = {"qualtrics1"}
    )
    public void testLaunchQualtrics() throws Exception {
        try {
            this.initialize((new Object() {
            }).getClass());
            System.out.println(this.jsonConfig.toString());
            JSONObject jCredentials = this.jsonConfig.getJSONObject("credentials");
            JSONObject jTestData = this.jsonConfig.getJSONObject("testData");
            this.driver.findElement(By.id("login:uname")).sendKeys(new CharSequence[]{jCredentials.getString("user")});
            this.driver.findElement(By.id("login:pword")).sendKeys(new CharSequence[]{jCredentials.getString("password")});
            this.driver.findElement(By.id("login:signInButton")).click();
            this.handleSessionMessage();
            this.driver.findElement(By.id("custFacingHeader:custFacingManageAccount")).click();
            this.driver.findElement(By.id("manageAcctForm:newAcctBtn")).click();
            WebElement panel = Utils.waitForVisibleElement(this.driver, By.xpath("//*[@id='acctMPanelCDiv']"), "20");
            if (Utils.isVisible(this.driver, By.id("acctMPanelCDiv"), "2")) {
                System.out.println("form displayed");
            } else {
                System.out.println("form NOT displayed");
            }

            Utils.waitForElementExtinct(this.driver, By.xpath("//*[@id='acctMPanelCDiv']"), "3");
            System.out.println("end");
        } catch (Throwable var4) {
            ;
        }

    }

    public boolean isActiveSessionMessage() {
        String rawStyle = this.driver.findElement(By.id("activeSession")).getAttribute("style");
        return rawStyle.isEmpty();
    }

    public void handleSessionMessage() throws Exception {
        if (this.isActiveSessionMessage()) {
            this.driver.findElement(By.xpath("//*[substring(@id, string-length(@id) - string-length('confirmTerminate') +1) = 'confirmTerminate']")).click();
        }

        Utils.waitForElementDisappear(this.driver, By.id("loadingMessage"), "20");
    }
}
