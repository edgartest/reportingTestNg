package com.pearson.test.qglobal;

import com.pearson.common.WebUITest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Test extends WebUITest {
    public Test() {
    }

    public static void main(String[] args) throws Exception {
        WebDriver driver = new FirefoxDriver();
        driver.get("https://qa1.qglobal.pearsonclinical.com/qg/login.seam");
        System.out.println("Page title is: " + driver.getTitle());
        WebElement user = driver.findElement(By.name("login:uname"));
        user.sendKeys(new CharSequence[]{"alonso05"});
        WebElement pass = driver.findElement(By.name("login:pword"));
        pass.sendKeys(new CharSequence[]{"Password"});
        WebElement btn = driver.findElement(By.id("login:signInButton"));
        btn.click();
        System.out.println("Page title is: " + driver.getTitle());
        driver.quit();
    }
}