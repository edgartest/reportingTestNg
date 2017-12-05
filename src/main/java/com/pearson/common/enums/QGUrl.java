package com.pearson.common.enums;

public enum QGUrl implements QGlobalUrl{
	
	ABOUT("http://www.pearsonclinical.com/psychology/products/100000680/q-global-usages.html"),
	CHANGE_PASSWORD("changePassword.seam"),
	CONTACT("http://www.pearsonclinical.com/about/help.html"),
	FEEDBACK("feedbackForm.seam"),
	HELLOQ("http://images.pearsonclinical.com/images/qglobal/"),
	HELP("static/Platform/Customer/en/WebHelp/index.htm"),
	LOGIN("login.seam"),
	MANAGE_ACCOUNTS("manageAccountHome.seam"),
	MOBILE_OPTIONS("mobileOptions.seam"),
	MY_ACCOUNT("settings.seam"),
	NEW_EXAMINEE("addExaminee.seam"),
	NOTIFICATIONS("customerNotificationList.seam"),
	PRIVACY("http://images.pearsonassessments.com/images/assets/qglobal/Q-global-Privacy-Policy.pdf"),
	RESOURCE_LIBRARY("resourceLibraryTree.seam"),
	SEARCH_EXAMINEE("searchExaminee.seam"),
	TERMS("http://images.pearsonassessments.com/images/assets/qglobal/Q-global-Terms-and-Conditions.pdf"),
	UNKNOWN("INVALID_URL")
	;

    private String url = null;
    
    private QGUrl(String url) {
        this.url = url;
    }
    
	@Override
	public String getUrl() {
		return Environment.getBaseUrl() + url;
	}

	@Override
	public String getText() {
		return url;
	}

	@Override
	public boolean equalTo(QGlobalUrl url) {
		return this.url.equals(url.getUrl());
	}

	@Override
	public boolean equalTo(String url) {
		return this.url.equals(url);
	}

	@Override
	public boolean contains(String url) {
		return this.url.contains(url);
	}

	@Override
	public boolean isValid() {
		return !this.equals(UNKNOWN);
	}


}
