package com.pearson.test.qglobal;

import com.pearson.api.restservices.QgApiRequestBuilder.QgApiRequestType;
import com.pearson.api.restservices.assessments.Assessment;
import com.pearson.api.restservices.assessments.AssessmentsFactory;
import com.pearson.api.restservices.core.SessionData;
import org.apache.http.HttpResponse;

public class TestApi {
    public TestApi() {
    }

    public static void main(String[] args) throws Exception {
        SessionData sessionData = new SessionData();
        HttpResponse response = null;
        sessionData.setBaseUrl("qa1.qglobal-api.pearsonclinical.com");
        sessionData.setBasicAuth("apiaccuser10", "Pearson1!");
        Assessment ktea_3_form_a = AssessmentsFactory.getSas().get_Ktea_3_Form_A();
        String apiRequest = ktea_3_form_a.withApi().buildRequest();
        sessionData.setApiRequest(apiRequest);
        response = QgApiRequestType.GENERATE_REPORT.generateAndSendRequest(sessionData);
        sessionData.checkHttpResponse(response);
    }
}
