package com.wondersgroup.cloud.deployment.bootstrap;

import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wondersgroup.cloud.deployment.EventListener;
import com.wondersgroup.cloud.deployment.Node;
import com.wondersgroup.cloud.deployment.service.ApplicationService;
import com.wondersgroup.cloud.deployment.service.ApplicationServiceImpl;

/*
 * 发命令方 也就是所谓的中心端
 * 1.接受到deployee的加盟请求
 * 2.批准加盟
 * 3.接受外部指令，然后发送指令给各台deployee
 * 4.对于指令完整度进行把控（比如出错情况控制）
 */
public class Deployer {
	private static Log logger = LogFactory.getLog(Deployer.class);

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

		final ApplicationService applicationService = new ApplicationServiceImpl();
		applicationService.registerEventListener(Node.TEST, new EventListener() {
			@Override
			public void execute(String appId) {
				logger.info(appId + "完蛋咯！");
			}});
		
		Thread deamonAppStatus = new Thread(new Runnable() {
			@Override
			public void run() {
				// 我也没办法
				try {
					FileWriter writer = new FileWriter("/root/app_state.txt", true);
		            while (true) {
		            	try {
							Thread.currentThread().sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		            	int status = applicationService.getAppStatus("app1");
						writer.write(Node.debugState(status) + "\n");
						writer.flush();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
//		deamonAppStatus.setDaemon(true);
		deamonAppStatus.start();

		// this.srcPath = String.valueOf(extraParams.get("publishDir"))
		// + File.separator + String.valueOf(extraParams.get("bsVersion"))
		// + File.separator + String.valueOf(extraParams.get("bsWar"));
		// this.appServers = String.valueOf(extraParams.get("appServers"));

		Map params = new HashMap(2);
		params.put("publishDir", "/root");
		params.put("bsVersion", "/deploy-repos");
		params.put("bsWar", "lrbao.war");
		JSONArray jsonArray = new JSONArray();
		jsonArray.add("10.1.65.105");
		params.put("appServers", jsonArray.toString());

		while (true) {
			Scanner sc = new Scanner(System.in);
			System.out.print("Please enter a cmd : ");
			String cmmd = sc.nextLine();
			System.out.println("Your input is : " + cmmd);
			String[] _args = cmmd.split(" ");
			logger.info("call depoly service.....");
			// applicationService.deploy(_args[1], new HashMap(2));
			applicationService.deploy("app1", params);
		}
	}

}
