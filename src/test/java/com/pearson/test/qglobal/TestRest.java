package com.pearson.test.qglobal;
/*
import com.pearson.api.restservices.AssignAssesmentRequestBuilder;
import com.pearson.api.restservices.CreateExamineeRequestBuilder;
import com.pearson.api.restservices.EnterItemResponsesRequestBuilder;
import com.pearson.api.restservices.Go2ExamineeDetailsRequestBuilder;
import com.pearson.api.restservices.LoginRequestBuilder;
import com.pearson.api.restservices.OpenReportConfigurationModalRequestBuilder;
import com.pearson.api.restservices.SearchExamineeRequestBuilder;
import com.pearson.api.restservices.AssignAssesmentRequestBuilder.AssignAssesmentRequestType;
import com.pearson.api.restservices.CreateExamineeRequestBuilder.CreateExamineeRequestType;
import com.pearson.api.restservices.EnterItemResponsesRequestBuilder.EnterItemResponsesRequestType;
import com.pearson.api.restservices.GenerateReportRequestBuilder.GenerateReportRequestType;
import com.pearson.api.restservices.Go2ExamineeDetailsRequestBuilder.Go2ExamineeDetailsRequestType;
import com.pearson.api.restservices.HomePageRequestBuilder.HomePageRequestType;
import com.pearson.api.restservices.LogOutRequestBuilder.LogOutRequestType;
import com.pearson.api.restservices.LoginRequestBuilder.LoginRequestType;
import com.pearson.api.restservices.OpenReportConfigurationModalRequestBuilder.OpenReportConfigurationModalRequestType;
import com.pearson.api.restservices.SearchExamineeRequestBuilder.SearchExamineeRequestType;
import com.pearson.api.restservices.assessments.Assessment;
import com.pearson.api.restservices.assessments.AssessmentsFactory;
import com.pearson.api.restservices.core.SessionData;
import com.pearson.api.restservices.reports.sas.Ktea_3_Enum;
import com.pearson.common.enums.ReportType;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

public class TestRest {
    public TestRest() {
    }

    public static void main(String[] args) throws Exception {
        SessionData sessionData = new SessionData();
        HttpResponse response = null;
        String testResponse = "";
        response = LoginRequestType.GET_HOME.generateAndSendRequest(sessionData);
        sessionData.checkHttpResponse(response);
        testResponse = EntityUtils.toString(response.getEntity());
        if (!LoginRequestBuilder.isLoginDisplayed(testResponse)) {
            throw new Exception("Login not displayed");
        } else {
            sessionData.setJsfViewState(LoginRequestBuilder.getResponseJsfViewState(testResponse));
            sessionData.setLoginUser("alonso05");
            sessionData.setLoginPassword("Password1");
            response = LoginRequestType.POST_LOGIN.generateAndSendRequest(sessionData);
            sessionData.checkHttpResponse(response);
            sessionData = LoginRequestBuilder.getSessionAfterLogin(sessionData, response);
            response = LoginRequestType.SEARCH_EXAMINEE.generateAndSendRequest(sessionData);
            sessionData.checkHttpResponse(response);
            response = LoginRequestType.EXAMINEE_PAGING.generateAndSendRequest(sessionData);
            testResponse = EntityUtils.toString(response.getEntity());
            if (!LoginRequestBuilder.isExamineeListDisplayed(testResponse)) {
                throw new Exception("Examinee list not displayed");
            } else {
                response = HomePageRequestType.SEARCH_EXAMINEE.generateAndSendRequest(sessionData);
                testResponse = EntityUtils.toString(response.getEntity());
                sessionData.setJsfViewState(LoginRequestBuilder.getResponseJsfViewState(testResponse));
                response = HomePageRequestType.POST_SEARCH_EXAMINEE_VIEW_ROOT.generateAndSendRequest(sessionData);
                sessionData.checkHttpResponse(response);
                response = HomePageRequestType.POST_SEARCH_EXAMINEE_EXT_SESS_REGION_ID.generateAndSendRequest(sessionData);
                testResponse = EntityUtils.toString(response.getEntity());
                sessionData.setJsfViewState(LoginRequestBuilder.getResponseJsfViewState(testResponse));
                response = HomePageRequestType.EXAMINEE_PAGING.generateAndSendRequest(sessionData);
                testResponse = EntityUtils.toString(response.getEntity());
                if (!LoginRequestBuilder.isExamineeListDisplayed(testResponse)) {
                    throw new Exception("Examinee list not displayed");
                } else {
                    response = CreateExamineeRequestType.POST_SEARCH_EXAMINEE_SEARCH_FORM.generateAndSendRequest(sessionData);
                    sessionData = CreateExamineeRequestBuilder.getSessionAfterExamineeCreation(sessionData, response);
                    response = CreateExamineeRequestType.POST_ADD_EXAMINEE_AJAX_REGION.generateAndSendRequest(sessionData);
                    sessionData.checkHttpResponse(response);
                    response = CreateExamineeRequestType.POST_ADD_EXAMINEE_DATA.generateAndSendRequest(sessionData);
                    sessionData.checkHttpResponse(response);
                    response = CreateExamineeRequestType.POST_ADD_EXAMINEE_SAVE.generateAndSendRequest(sessionData);
                    sessionData.checkHttpResponse(response);
                    response = CreateExamineeRequestType.POST_ADD_EXAMINEE_AJAX_EVENT_COUNT.generateAndSendRequest(sessionData);
                    sessionData.checkHttpResponse(response);
                    response = CreateExamineeRequestType.GET_REFRESH_EXAMINEE_LIST.generateAndSendRequest(sessionData);
                    testResponse = EntityUtils.toString(response.getEntity());
                    sessionData.setJsfViewState(CreateExamineeRequestBuilder.getResponseJsfViewState(testResponse));
                    response = SearchExamineeRequestType.PSEARCH_EXAMINEE_SEARCH_FORM.generateAndSendRequest(sessionData);
                    testResponse = EntityUtils.toString(response.getEntity());
                    if (!SearchExamineeRequestBuilder.isSearchExamineeDisplayed(testResponse)) {
                        throw new Exception("Search Examinee not displayed");
                    } else {
                        response = SearchExamineeRequestType.PSEARCH_EXAMINEE_EDIT_FORM_BY_ID.generateAndSendRequest(sessionData);
                        sessionData.checkHttpResponse(response);
                        response = LoginRequestType.EXAMINEE_PAGING.generateAndSendRequest(sessionData);
                        testResponse = EntityUtils.toString(response.getEntity());
                        if (!SearchExamineeRequestBuilder.isExamineeIdPresent(testResponse, sessionData.getExamineeId())) {
                            throw new Exception("Examinee list not displayed");
                        } else {
                            sessionData.setExamineeSystemId(SearchExamineeRequestBuilder.getResponseSystemId(testResponse, "\"systemid\":\"([0-9]+?)\",\"examineeid\":\"" + sessionData.getExamineeId() + "\""));
                            response = HomePageRequestType.SEARCH_EXAMINEE.generateAndSendRequest(sessionData);
                            testResponse = EntityUtils.toString(response.getEntity());
                            sessionData.setJsfViewState(LoginRequestBuilder.getResponseJsfViewState(testResponse));
                            response = HomePageRequestType.POST_SEARCH_EXAMINEE_VIEW_ROOT.generateAndSendRequest(sessionData);
                            sessionData.checkHttpResponse(response);
                            response = HomePageRequestType.POST_SEARCH_EXAMINEE_EXT_SESS_REGION_ID.generateAndSendRequest(sessionData);
                            testResponse = EntityUtils.toString(response.getEntity());
                            sessionData.setJsfViewState(LoginRequestBuilder.getResponseJsfViewState(testResponse));
                            response = HomePageRequestType.EXAMINEE_PAGING.generateAndSendRequest(sessionData);
                            testResponse = EntityUtils.toString(response.getEntity());
                            if (!LoginRequestBuilder.isExamineeListDisplayed(testResponse)) {
                                throw new Exception("Examinee list not displayed");
                            } else {
                                response = Go2ExamineeDetailsRequestType.PSEARCH_EXAMINEE_SEARCH_FORM.generateAndSendRequest(sessionData);
                                sessionData.checkHttpResponse(response);
                                response = Go2ExamineeDetailsRequestType.PSEARCH_EXAMINEE_SEARCH_FORM_TARGET.generateAndSendRequest(sessionData);
                                testResponse = EntityUtils.toString(response.getEntity());
                                if (!Go2ExamineeDetailsRequestBuilder.isExamineeDetailsPresent(testResponse, sessionData.getExamineeFirstName())) {
                                    throw new Exception("Examinee searched not present");
                                } else {
                                    sessionData.setJsfViewState(Go2ExamineeDetailsRequestBuilder.getResponseJsfViewState(testResponse));
                                    response = LoginRequestType.EXAMINEE_PAGING.generateAndSendRequest(sessionData);
                                    sessionData.checkHttpResponse(response);
                                    Assessment ktea_3_form_a = AssessmentsFactory.getSas().get_Ktea_3_Form_A();
                                    String assesmentData = ktea_3_form_a.withQg().buildRequest();
                                    ktea_3_form_a.setAssesmentData(assesmentData);
                                    sessionData.setAssesment(ktea_3_form_a);
                                    response = AssignAssesmentRequestType.POST_VIEW_EXAMINEE_DETAILS.generateAndSendRequest(sessionData);
                                    testResponse = EntityUtils.toString(response.getEntity());
                                    sessionData.setJsfViewState(AssignAssesmentRequestBuilder.getResponseJsfViewState(testResponse));
                                    response = AssignAssesmentRequestType.GET_ALL_ASSESMENTS.generateAndSendRequest(sessionData);
                                    testResponse = EntityUtils.toString(response.getEntity());
                                    sessionData.getAssesment().setQgAssesmentIdRadioBtn(AssignAssesmentRequestBuilder.getAssesmentId(testResponse, sessionData.getAssesment().getAssesmentName()));
                                    sessionData.updateCache();
                                    response = AssignAssesmentRequestType.POST_SELECT_ASSESMENT_ID.generateAndSendRequest(sessionData);
                                    testResponse = EntityUtils.toString(response.getEntity());
                                    sessionData.setSeamCid(AssignAssesmentRequestBuilder.getSeamCid(testResponse));
                                    if (Integer.valueOf(sessionData.getSeamCid()).intValue() < 0) {
                                        response = AssignAssesmentRequestType.POST_ERROR_ASSIGN_ASSESMENT_BUTTON.generateAndSendRequest(sessionData);
                                        sessionData.checkHttpResponse(response);
                                        response = AssignAssesmentRequestType.POST_ERROR_SELECT_ASSESMENT_ID.generateAndSendRequest(sessionData);
                                        testResponse = EntityUtils.toString(response.getEntity());
                                        sessionData.setSeamCid(AssignAssesmentRequestBuilder.getSeamCid(testResponse));
                                    }

                                    response = AssignAssesmentRequestType.GET_EXAMINEE_ASSESMENT_DETAILS.generateAndSendRequest(sessionData);
                                    testResponse = EntityUtils.toString(response.getEntity());
                                    if (!AssignAssesmentRequestBuilder.isReadyForAdministration(testResponse)) {
                                        throw new Exception("Not ready for administration");
                                    } else {
                                        sessionData.setExaminerName(AssignAssesmentRequestBuilder.getExaminerName(testResponse));
                                        sessionData.setJsfViewState(AssignAssesmentRequestBuilder.getResponseJsfViewState(testResponse));
                                        response = AssignAssesmentRequestType.POST_ASSESMENT_DETAILS_FUNCID.generateAndSendRequest(sessionData);
                                        sessionData.checkHttpResponse(response);
                                        response = EnterItemResponsesRequestType.PASSESMENT_SAVE_ABOVE.generateAndSendRequest(sessionData);
                                        sessionData.checkHttpResponse(response);
                                        response = EnterItemResponsesRequestType.PASSESMENT_INVOKE_FUNID.generateAndSendRequest(sessionData);
                                        testResponse = EntityUtils.toString(response.getEntity());
                                        if (!EnterItemResponsesRequestBuilder.isReadyForReporting(testResponse)) {
                                            throw new Exception("Not ready for reporting");
                                        } else {
                                            sessionData.setJsfViewState(EnterItemResponsesRequestBuilder.getResponseJsfViewState(testResponse));
                                            response = OpenReportConfigurationModalRequestType.PASSESMENT_GENERATE_BUTTON.generateAndSendRequest(sessionData);
                                            sessionData.checkHttpResponse(response);
                                            response = OpenReportConfigurationModalRequestType.PASSESMENT_CLEAR_USER_OPTIONS.generateAndSendRequest(sessionData);
                                            testResponse = EntityUtils.toString(response.getEntity());
                                            if (!OpenReportConfigurationModalRequestBuilder.isReportConfigurationDisplayed(testResponse)) {
                                                throw new Exception("Report Configuration not displayed");
                                            } else {
                                                sessionData.setJsfViewState(OpenReportConfigurationModalRequestBuilder.getResponseJsfViewState(testResponse));
                                                String reportData = ktea_3_form_a.withQg().withReport(Ktea_3_Enum.Standard_Report, ReportType.DOC).buildRequest();
                                                ktea_3_form_a.getReport().setReportData(reportData);
                                                String reportBundleId = OpenReportConfigurationModalRequestBuilder.getReportBundleId(testResponse, ktea_3_form_a.getReport().getReportName());
                                                ktea_3_form_a.getReport().setReportBundleId(reportBundleId);
                                                sessionData.setAssesment(ktea_3_form_a);
                                                response = GenerateReportRequestType.REPORT_OPTION_REPORT_BUTTON.generateAndSendRequest(sessionData);
                                                sessionData.checkHttpResponse(response);
                                                response = GenerateReportRequestType.REPORT_OPTION_REPORT_LINK.generateAndSendRequest(sessionData);
                                                sessionData.saveReport(response, ktea_3_form_a);
                                                response = LogOutRequestType.LOG_OUT.generateAndSendRequest(sessionData);
                                                sessionData.checkHttpResponse(response);
                                                response = LogOutRequestType.LOG_OUT_ACTION_METHOD.generateAndSendRequest(sessionData);
                                                sessionData.checkHttpResponse(response);
                                                System.out.println(" This is the end");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}*/
