package com.wondersgroup.cloud.deployment.service;

public interface ApplicationService {

	boolean deploy(String appId);

	int getAppStatus(String appId);

}
