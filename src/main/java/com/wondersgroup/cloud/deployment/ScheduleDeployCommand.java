package com.wondersgroup.cloud.deployment;

import java.util.Date;


public class ScheduleDeployCommand extends DeployCommand implements ICommand {

	private Date startDate;

	public ScheduleDeployCommand(String appId, int key,
			Date startDate) {
		super(appId, key);
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
	}

}
