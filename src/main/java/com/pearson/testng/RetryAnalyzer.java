package com.pearson.testng;

import com.pearson.common.Log;
import com.pearson.common.Utils;
import org.testng.IRetryAnalyzer;
import org.testng.ITestContext;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    int retryAttempts = 0;

    public RetryAnalyzer() {
    }

    public boolean retry(ITestResult result) {
        if (result != null && !result.isSuccess()) {
            ITestContext testContext = result.getTestContext();
            RetryConfiguration config = RetryConfiguration.getInstance();
            String testName = result.getMethod() != null ? result.getMethod().getMethodName() : result.getName();
            Integer number = testContext != null && testName != null ? config.lookupTestRetry(testName) : null;
            int maxAttempts = number == null ? config.getMaxRetries() : number.intValue();
            if (++this.retryAttempts > maxAttempts) {
                return false;
            } else {
                config.incrementRetryStatus(testName);
                Log.getDefaultLogger().info("Retrying " + result.getName() + " [" + this.retryAttempts + "/" + maxAttempts + "]");
                Utils.sleep(1500L);
                return true;
            }
        } else {
            return false;
        }
    }
}
