package com.wondersgroup.cloud.deployment.bootstrap;

import java.net.UnknownHostException;
import java.util.Scanner;

import com.wondersgroup.cloud.deployment.service.ApplicatioinServiceImpl;
import com.wondersgroup.cloud.deployment.service.ApplicationService;

/*
 * 发命令方 也就是所谓的中心端
 * 1.接受到deployee的加盟请求
 * 2.批准加盟
 * 3.接受外部指令，然后发送指令给各台deployee
 * 4.对于指令完整度进行把控（比如出错情况控制）
 */
public class Deployer {

	public static void main(String[] args) throws UnknownHostException {
		
		// // TODO: 完成验证等一系列发布指令工作
		// ICommand command = new PlainCommand("close app1");
		// node.executeCommand(command);
		// command = new PlainCommand("delete app1");
		// node.executeCommand(command);
		// command = new PlainCommand("transport app1");
		// node.executeCommand(command);
		// command = new PlainCommand("start app1");
		// node.executeCommand(command);
		// command = new PlainCommand("test app1");
		// node.executeCommand(command);

		final ApplicationService applicationService = new ApplicatioinServiceImpl();
		Thread deamonAppStatus = new Thread(new Runnable(){
			@Override
			public void run() {
				// 我也没办法
				int status = applicationService.getAppStatus("app1");
			}});
		deamonAppStatus.setDaemon(true);
		deamonAppStatus.start();

		while (true) {
			Scanner sc = new Scanner(System.in);
			System.out.print("Please enter a cmd : ");
			String cmmd = sc.nextLine();
			System.out.println("Your input is : " + cmmd);
			String[] _args = cmmd.split(" ");
			applicationService.deploy(_args[1]);
		}
	}

}
