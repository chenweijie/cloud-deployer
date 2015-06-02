/**
 * 
 */
package com.wondersgroup.cloud.deployment.service;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 */
public class QuartzTest {
	private static Log logger = LogFactory.getLog(QuartzTest.class);

	private String timePart;

	@Before
	public void init() {
		try {
			Properties props = new Properties();
			props.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("node.properties"));
			timePart = props.getProperty("schedule.time");
		} catch (IOException ex) {
			throw new RuntimeException("AAAAAAAAAAAA" + ex.getMessage(), ex);
		}
	}

	@Test
	public void testJob() throws IOException, SchedulerException,
			ParseException {

		SchedulerFactory sf = new StdSchedulerFactory("node.properties");
		Scheduler scheduler = sf.getScheduler();
		scheduler.getContext().put("node", "1");
		scheduler.start();

		scheduler.deleteJob(new JobKey("job-1", "app-group"));

		JobDetail job = newJob(QuartzJobTest.class)
				.withIdentity("job-1", "app-group").storeDurably(true).build();
		job.getJobDataMap().put("appId", 1);
		job.getJobDataMap().put("srcPath", "2");
		job.getJobDataMap().put("ipList", "3");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = sdf.parse("2015-6-2");
		String cronExpress = "0 29 23 02 06 ? 2015";// this.parse(startDate);
		logger.info("schedule deploy start...." + cronExpress);
		Trigger trigger = newTrigger().withIdentity("trigger-1", "app-group")
				.startNow().withSchedule(cronSchedule(cronExpress)).build();
		scheduler.scheduleJob(job, trigger);
		
		while (true) {
			
		}
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("dd MM ? yyyy");

	private String parse(Date startDate) {
		// like this 21 05 ? 2015
		String result = sdf.format(startDate);
		String[] _parts = timePart.split(":");
		return "0 " + _parts[1] + " " + _parts[0] + " " + result;
	}
}
