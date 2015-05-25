package com.wondersgroup.cloud.deployment.service;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.wondersgroup.cloud.deployment.DeployCommand;
import com.wondersgroup.cloud.deployment.DeployException;
import com.wondersgroup.cloud.deployment.INodeListener;
import com.wondersgroup.cloud.deployment.Node;
import com.wondersgroup.cloud.deployment.ScheduleDeployCommand;

/*
 * 需求确认
 * 每日开始时间定死
 * 但会读取业务，以及业务开始，结束日期
 * 每个业务只跑一次，会进去新的trigger 新的job
 * */
public class BackendScheduler implements INodeListener {

	private Node node;

	private String timePart;

	private Scheduler scheduler;

	public BackendScheduler(Node node) {
		this.node = node;

		try {
			Properties props = new Properties();
			props.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("node.properties"));
			timePart = props.getProperty("schedule.time");
		} catch (IOException ex) {
			throw new DeployException("初始化异常:" + ex.getMessage(), ex);
		}
	}

	@Override
	public void fireNodeEvent(String msg, String srcIp, Object... params) {
		if (srcIp == node.getIp()
				&& (Node.runStateOf(node.selectKey(msg)) == Node.INIT)) {
			try {
				SchedulerFactory sf = new StdSchedulerFactory("node.properties");
				scheduler = sf.getScheduler();
				scheduler.getContext().put("node", node);
				scheduler.start();
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		} else if (srcIp == node.getIp()
				&& (Node.runStateOf(node.selectKey(msg)) == Node.DEPLOY_SCHEDULE)) {
			ScheduleDeployCommand command = (ScheduleDeployCommand) params[0];
			String appId = command.getAppId();
			Date startDate = command.getStartDate();
			String[] datas = DeployCommand.toData(msg);
			String srcPath = datas[1];
			String ipList = datas[2];
			try {
				scheduler.deleteJob(new JobKey("job-" + appId, "app-group"));
				
				// // 初始化 quartz等对象 trigger job等等
				JobDetail job = newJob(QuartzJob.class)
						.withIdentity("job-" + appId, "app-group")
						.storeDurably(true)
						.build();
				job.getJobDataMap().put("appId", appId);
				job.getJobDataMap().put("srcPath", srcPath);
				job.getJobDataMap().put("ipList", ipList);
				String cronExpress = this.parse(startDate);
				Trigger trigger = newTrigger()
						.withIdentity("trigger-" + appId, "app-group")
						.startNow().withSchedule(cronSchedule(cronExpress))
						.build();
				scheduler.scheduleJob(job, trigger);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("dd MM ? YYYY");

	private String parse(Date startDate) {
		// like this 21 05 ? 2015
		String result = sdf.format(startDate);
		String[] _parts = timePart.split(":");
		return "0 " + _parts[1] + " " + _parts[0] + " " + result;
	}

}
