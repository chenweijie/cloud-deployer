package com.wondersgroup.cloud.deployment.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.wondersgroup.cloud.deployment.DeployCommand;
import com.wondersgroup.cloud.deployment.ICommand;
import com.wondersgroup.cloud.deployment.Node;

public class QuartzJob implements Job {
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			String appId = String.valueOf(context.getJobDetail()
					.getJobDataMap().get("appId"));
			String srcPath = String.valueOf(context.getJobDetail()
					.getJobDataMap().get("srcPath"));
			String ipList = String.valueOf(context.getJobDetail()
					.getJobDataMap().get("ipList"));
			Node node = (Node) (context.getScheduler().getContext().get("node"));
			ICommand command = new DeployCommand(appId, Node.DEPLOY, srcPath,
					ipList);
			node.executeCommand(command);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}