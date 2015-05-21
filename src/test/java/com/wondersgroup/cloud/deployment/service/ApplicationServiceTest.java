package com.wondersgroup.cloud.deployment.service;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class ApplicationServiceTest {

	@Before
	public void openServer() {

	}

	@Test
	public void download() throws ParseException {
		// ApplicationService appService = new ApplicatioinServiceImpl();
		// SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD");
		// Date startDate = sdf.parse("2015-5-21");
		// String appId = "app1";
		// appService.deploy(appId , startDate);
	}

	@Test
	public void quartz() throws SchedulerException {
		// Thread deamon = new Thread(new Runnable() {
		// @Override
		// public void run() {

		try {
			Date startDate = new Date(System.currentTimeMillis());
			SchedulerFactory sf = new StdSchedulerFactory("node.properties");

			Scheduler scheduler = sf.getScheduler();
			scheduler.getContext().put("aaa", "aslfjsadkfja");
			scheduler.start();

			scheduler.deleteJob(new JobKey("job-example1", "app-group"));

			JobDetail job = newJob(ExampleJob.class)
					.withIdentity("job-example1", "app-group")
					.storeDurably(true).build();
			job.getJobDataMap().put("appId", "nimanima");
			String cronExpress = ApplicationServiceTest.this.parse(startDate);
			System.out.println(cronExpress);
			//
			Trigger trigger = newTrigger()
					.withIdentity("trigger-example1", "app-group")
					.withSchedule(cronSchedule(cronExpress)).forJob(job)
					.build();
			// dailyTimeIntervalSchedule().withIntervalInSeconds(10)
			// .withSchedule(cronSchedule("0 27 15 * * ?")).forJob(job).build();//cronExpress
			// scheduler.addJob(job, false);

			scheduler.scheduleJob(job, trigger);

		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// }
		// });
		// deamon.start();
		while (true) {
		}
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("dd MM ? YYYY");

	private String parse(Date startDate) {
		// like this 21 05 ? 2015
		String result = sdf.format(startDate);
		String timePart = "16:42";
		String[] _parts = timePart.split(":");
		return "0 " + _parts[1] + " " + _parts[0] + " " + result;
	}

	public static void main(String[] args) throws SchedulerException {
		// SchedulerFactory sf = new StdSchedulerFactory();// "node.properties"
		// Scheduler scheduler = sf.getScheduler();

		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		scheduler.getContext().put("aaa", "bbbbbb");
		scheduler.deleteJob(new JobKey("job1", "group1"));
		JobDetail job = newJob(ExampleJob.class).withIdentity("job1", "group1")
				.build();
		job.getJobDataMap().put("appId", "nimanima");
		// Trigger the job to run now, and then repeat every 40 seconds
		Trigger trigger = newTrigger()
				.withIdentity("trigger1", "group1")
				.startNow()
				.withSchedule(
						simpleSchedule().withIntervalInSeconds(40)
								.repeatForever()).build();

		// Tell quartz to schedule the job using our trigger
		scheduler.scheduleJob(job, trigger);

		scheduler.start();
	}
}
