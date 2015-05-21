package com.wondersgroup.cloud.deployment.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

public class ExampleJob implements Job {
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			String appId = String.valueOf(context.getJobDetail().getJobDataMap().get("appId"));
			System.out.println("app::::::::" + appId);
			
			String demo = String.valueOf(context.getScheduler().getContext().get("aaa"));
			System.out.println("demo::::::::" + demo);
			
			RandomAccessFile rfa = new RandomAccessFile(new File(
					"d:\\tmp\\quartz.log"), "rw");
			rfa.write(123);
			rfa.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
