package com.wondersgroup.cloud.deployment;

import java.util.Date;
import java.util.Map;

public class ScheduleDeployCommand extends DeployCommand implements ICommand {

	private Date startDate;

	public ScheduleDeployCommand(String appId, int key, Map extraParams,
			Date startDate) {
		super(appId, key, extraParams);
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
	}

}
