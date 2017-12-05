package com.pearson.testng;

import com.pearson.common.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestResultPruner extends TestListenerAdapter {
    public TestResultPruner() {
    }

    private int getId(ITestResult result) {
        int id = result.getTestClass().getName().hashCode();
        id = 31 * id + result.getMethod().getMethodName().hashCode();
        id = 31 * id + (result.getParameters() != null ? Arrays.hashCode(result.getParameters()) : 0);
        return id;
    }

    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
        List<ITestResult> testsToBeRemoved = new ArrayList();
        Set<Integer> passedTestIds = new HashSet();
        Set<ITestResult> passedTests = testContext.getPassedTests().getAllResults();
        Iterator var6 = passedTests.iterator();

        while(var6.hasNext()) {
            ITestResult passedTest = (ITestResult)var6.next();
            passedTestIds.add(this.getId(passedTest));
        }

        Set<Integer> failedTestIds = new HashSet();
        Set<ITestResult> failedTests = testContext.getFailedTests().getAllResults();
        Iterator var8 = failedTests.iterator();

        while(true) {
            while(var8.hasNext()) {
                ITestResult failedTest = (ITestResult)var8.next();
                int failedTestId = this.getId(failedTest);
                if (!failedTestIds.contains(failedTestId) && !passedTestIds.contains(failedTestId)) {
                    failedTestIds.add(failedTestId);
                } else {
                    testsToBeRemoved.add(failedTest);
                }
            }

            Iterator iterator = failedTests.iterator();

            while(iterator.hasNext()) {
                ITestResult testResult = (ITestResult)iterator.next();
                if (testsToBeRemoved.contains(testResult)) {
                    Log.getDefaultLogger().info("Pruning test result: " + testResult.toString());
                    iterator.remove();
                }
            }

            return;
        }
    }
}
