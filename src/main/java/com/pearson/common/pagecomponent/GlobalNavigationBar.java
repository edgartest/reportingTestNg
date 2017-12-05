package com.pearson.common.pagecomponent;

import com.pearson.common.pageobject.PageObject;

public interface GlobalNavigationBar {

	public <T extends PageObject> T clickNavBarHome() throws Exception;
	
	public <T extends PageObject> T clickNavBarMyAccount() throws Exception;
	
	public <T extends PageObject> T clickNavBarManageAccounts() throws Exception;
	
	public <T extends PageObject> T clickNavBarResourceLibrary() throws Exception;
	
	public <T extends PageObject> T clickNavBarNotifications() throws Exception;
	
	public <T extends PageObject> T clickNavBarFeedback() throws Exception;
	
	public <T extends PageObject> T clickNavBarHelp() throws Exception;
}
