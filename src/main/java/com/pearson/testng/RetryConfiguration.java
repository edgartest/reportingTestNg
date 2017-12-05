package com.pearson.testng;

import java.util.HashMap;
import java.util.Map;

public class RetryConfiguration {
    private static RetryConfiguration instance = null;
    private int maxRetries = 1;
    private Map<String, Integer> retryLookupMap = new HashMap();
    private Map<String, Integer> retryStatusMap = new HashMap();

    private RetryConfiguration() {
    }

    public static synchronized RetryConfiguration getInstance() {
        if (instance == null) {
            instance = new RetryConfiguration();
        }

        return instance;
    }

    public synchronized int getMaxRetries() {
        return this.maxRetries;
    }

    public synchronized void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        if (maxRetries < 0) {
            this.maxRetries = 0;
        }

    }

    public Map<String, Integer> getRetryLookupMap() {
        return this.retryLookupMap;
    }

    public synchronized Integer lookupTestRetry(String testName) {
        return this.retryLookupMap.containsKey(testName) ? ((Integer)this.retryLookupMap.get(testName)).intValue() : null;
    }

    public synchronized void setTestRetry(String testName, int retry) {
        this.retryLookupMap.put(testName, retry);
    }

    public synchronized void incrementRetryStatus(String testName) {
        int value = 1;
        if (this.retryStatusMap.containsKey(testName)) {
            value = ((Integer)this.retryStatusMap.get(testName)).intValue() + 1;
        }

        this.retryStatusMap.put(testName, value);
    }

    public synchronized boolean isTestRetrying(String testName) {
        if (this.retryStatusMap.containsKey(testName)) {
            return ((Integer)this.retryStatusMap.get(testName)).intValue() > 0;
        } else {
            return false;
        }
    }
}
