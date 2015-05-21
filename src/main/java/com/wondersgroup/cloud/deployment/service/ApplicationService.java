package com.wondersgroup.cloud.deployment.service;

import java.util.Date;

public interface ApplicationService {

	boolean deploy(String appId);

	boolean deploy(String appId, Date startDate);

	int getAppStatus(String appId);

}
