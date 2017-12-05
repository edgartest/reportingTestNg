package com.pearson.test.qglobal;

import com.pearson.api.restservices.assessments.Assessment;
import com.pearson.api.restservices.assessments.AssessmentsFactory;

public class TestReportMaker {
    public TestReportMaker() {
    }

    public static void main(String[] args) throws Exception {
        Assessment ktea_3_form_a = AssessmentsFactory.getSas().get_Ktea_3_Form_A();
        String reportData = ktea_3_form_a.withApi().buildRequest();
        System.out.println(reportData);
    }
}
