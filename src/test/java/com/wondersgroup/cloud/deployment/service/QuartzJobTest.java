package com.wondersgroup.cloud.deployment.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class QuartzJobTest implements Job {
	private static Log logger = LogFactory.getLog(QuartzJobTest.class);

	@Override
	public void execute(JobExecutionContext context) {
		logger.info("start task....AAA");
	}

}