package com.wondersgroup.cloud.deployment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CloseReceiveHandler extends AbstractReceiveHandler {

	private Log logger = LogFactory.getLog(CloseReceiveHandler.class);
	
	public CloseReceiveHandler(Node node) {
		super(node);
	}

	@Override
	public void handle(String msg, String srcIp) {
		String[] datas = DeployCommand.toData(msg);
		String appId = datas[0];
		String srcPath = datas[1];
		String ipList = datas[2];

		logger.info("execute local close*****************************");
		// TODO 从IPList列表中找到是否是本地IP
		// 如果是的话 在本地执行 execute相关指令
		try {
			Process proc = Runtime.getRuntime().exec(
					"/root/apache-tomcat-6.0.39/server_stop.sh " + appId);
			proc.waitFor();
			node.executeCommand(new DeployCommand(appId, Node.CLOSE
					| Node.SUCCESS, srcPath, ipList));
		} catch (Exception e) {
			e.printStackTrace();
			node.executeCommand(new DeployCommand(appId, Node.CLOSE
					| Node.FAILURE, srcPath, ipList));
		}
	}

}
