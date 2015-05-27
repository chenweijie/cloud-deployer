package com.wondersgroup.cloud.deployment.service;

import java.util.Map;

public interface ApplicationService {

	boolean deploy(String appId, Map extraParams);

	boolean scheduleDeploy(String appId, Map extraParams);

	int getAppStatus(String appId);

}
