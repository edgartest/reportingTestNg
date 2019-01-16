package com.pearson.common.enums;

public enum QGUrl implements QGlobalUrl{
	//Full, Short
	ABOUT("F", "http://www.pearsonclinical.com/psychology/products/100000680/q-global-usages.html", "about"),
	CHANGE_PASSWORD("S", "changePassword.seam", "change_password"),
	CONTACT("F", "http://www.pearsonclinical.com/about/help.html", "contact"),
	FEEDBACK("S", "feedbackForm.seam", "feedback"),
	GROUP_ADMIN("S", "searchExaminee.seam", "group_admin"),
	HELLOQ("F","http://images.pearsonclinical.com/images/qglobal/", "helloq"),
	HELP("S","static/Platform/Customer/en/WebHelp/index.htm", "help"),
	LOGIN("S","login.seam", "login"),
	MANAGE_ACCOUNTS("S", "manageAccountHome.seam", "manage_accounts"),
	MOBILE_OPTIONS("S","mobileOptions.seam","mobile_options"),
	MY_ACCOUNT("S", "settings.seam", "my_account"),
	NEW_EXAMINEE("S", "addExaminee.seam", "new_examinee"),
	NOTIFICATIONS("S", "customerNotificationList.seam", "notifications"),
	PRIVACY("F", "http://images.pearsonassessments.com/images/assets/qglobal/Q-global-Privacy-Policy.pdf", "privacy"),
	RESOURCE_LIBRARY("S", "resourceLibraryTree.seam", "resource_library"),
	SEARCH_EXAMINEE("S", "searchExaminee.seam", "search_examinee"),
	TERMS("F", "http://images.pearsonassessments.com/images/assets/qglobal/Q-global-Terms-and-Conditions.pdf", "terms"),
	UNKNOWN("F", "INVALID_URL", "unknown")
	;

	private String lengthType = null;
    private String url = null;
    private String pageName = null;
    
    private QGUrl(String lengthType, String url, String name) {
        this.lengthType = lengthType;
    	this.url = url;
    }
    
	@Override
	public String getUrl() {
		return Environment.getBaseUrl() + url;
	}

	@Override
	public String getPageName() {
		return pageName;
	}

	@Override
	public String getLenType() {
		// TODO Auto-generated method stub
		return null;
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

	public boolean isUnique(String seed){
		
		for (QGUrl val : this.values()) {
			  
			}
		return false;
	}

	public char[] findNameByUrl(String string) {
		// TODO Auto-generated method stub
		return null;
	}



}
